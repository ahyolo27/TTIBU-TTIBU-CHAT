package com.ttibuttibu.chat.coreapi.domain.llm;

import com.ttibuttibu.chat.coreapi.common.error.ApiException;
import com.ttibuttibu.chat.coreapi.common.error.ErrorCode;
import com.ttibuttibu.chat.coreapi.config.properties.AiProcessingProperties;
import com.ttibuttibu.chat.coreapi.config.properties.LiteLlmProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service
public class LiteLlmWebClient implements LiteLlmClient {

    private final WebClient liteLlmWebClient;

    private final LiteLlmProperties liteLlmProperties;

    private final AiProcessingProperties aiProcessingProperties;

    public LiteLlmWebClient(@Qualifier("liteLlmClient") WebClient liteLlmWebClient, LiteLlmProperties liteLlmProperties, AiProcessingProperties aiProcessingProperties) {
        this.liteLlmWebClient = liteLlmWebClient;
        this.liteLlmProperties = liteLlmProperties;
        this.aiProcessingProperties = aiProcessingProperties;
    }

    /**
     * apiKey 유효성 검사
     */
    @Override
    public void test(String apiKey, String model) {
        Map<String, Object> body = Map.of(
                "model", model,
                "api_key", apiKey,
                "messages", List.of(Map.of("role", "user", "content", "ping")),
                "max_tokens", 1,
                "temperature", 0
        );
        try {
            liteLlmWebClient.post()
                    .uri("/v1/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + liteLlmProperties.getApiKey())
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            r -> map4xxToApiEx(r, model, false))
                    .onStatus(HttpStatusCode::is5xxServerError, r -> Mono.error(new ApiException(ErrorCode.UPSTREAM_ERROR)))
                    .toBodilessEntity()
                    .block();
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.UPSTREAM_ERROR, e.getMessage());
        }
    }

    /**
     * 스트리밍 기반 채팅 생성
     */
    @Override
    public Flux<String> createChatStream(String apiKey, String model, List<Map<String, String>> messages) {
        final String masterKey = liteLlmProperties.getApiKey();

        final boolean streamEnabled = aiProcessingProperties.isStreamEnabled();
        final double temperature = resolveTemperature(model, aiProcessingProperties.getTemperature());


            Map<String, Object> llmBody = Map.of(
                    "model", model,
                    "api_key", apiKey,
                    "messages", messages,
                    "stream", streamEnabled,
                    "temperature", temperature
            );

            var spec = liteLlmWebClient.post()
                    .uri("/v1/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + masterKey)
                    .bodyValue(llmBody);

            return addAcceptIfStream(spec, streamEnabled)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, r -> map4xxToApiEx(r, model, streamEnabled))
                    .onStatus(HttpStatusCode::is5xxServerError, r -> Mono.error(new ApiException(ErrorCode.UPSTREAM_ERROR)))
                    .bodyToFlux(String.class);
    }

    private WebClient.RequestHeadersSpec<?> addAcceptIfStream(WebClient.RequestHeadersSpec<?> spec, boolean stream) {
        return stream ? spec.header(HttpHeaders.ACCEPT, "text/event-stream") : spec;
    }

    private double resolveTemperature(String model, double defaultTemperature) {
        if (model == null) return defaultTemperature;

        String m = model.toLowerCase(Locale.ROOT);

        // gpt-5, gpt-5-mini, gpt-5-nano 모두 포함
        if (m.startsWith("gpt-5")) {
            return 1.0;
        }

        // 나머지는 기존 설정값 그대로
        return defaultTemperature;
    }

    /** 4xx 로깅 강화: model/stream 포함 */
    private Mono<? extends Throwable> map4xxToApiEx(ClientResponse response, String model, boolean stream) {
        return response.bodyToMono(String.class).defaultIfEmpty("")
                .map(body -> {
                    int sc = response.statusCode().value();
                    log.warn("[LLM-4xx] model={}, stream={}, status={}, body={}",
                            model, stream, sc, body);
                    return switch (sc) {
                        case 401, 403 -> new ApiException(ErrorCode.INVALID_KEY, body);
                        case 429 -> new ApiException(ErrorCode.RATE_LIMITED, body);
                        default -> new ApiException(ErrorCode.UPSTREAM_ERROR, body);
                    };
                });
    }
}
