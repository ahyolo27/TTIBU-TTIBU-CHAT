package com.ttibuttibu.chat.coreapi.domain.llm;

import com.ttibuttibu.chat.coreapi.common.error.ApiException;
import com.ttibuttibu.chat.coreapi.common.error.ErrorCode;
import com.ttibuttibu.chat.coreapi.config.properties.SummaryApiProperties;
import com.ttibuttibu.chat.coreapi.domain.chat.dto.AiSummaryKeywordsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiSummaryClient {

    private final RestClient restClient;
    private final SummaryApiProperties summaryApiProperties;

    public AiSummaryKeywordsResponseDto summarizeGroupText(String text) {
        try {
            String url = summaryApiProperties.getBaseUrl() + "/summarize";

            return restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "text", text,
                            "maxLength", 150,
                            "minLength", 30
                    ))
                    .retrieve()
                    .body(AiSummaryKeywordsResponseDto.class);
        } catch (RestClientException e) {
            throw new ApiException(ErrorCode.EXTERNAL_API_CONNECTION_FAILED);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }
}
