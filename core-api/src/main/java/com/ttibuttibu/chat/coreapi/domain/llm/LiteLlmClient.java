package com.ttibuttibu.chat.coreapi.domain.llm;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public interface LiteLlmClient {

    /**
     * apiKey 유효성 검사: 1토큰짜리 요청 보냄
     */
    void test(String apiKey, String model);

    Flux<String> createChatStream(String apiKey, String model, List<Map<String, String>> messages);
}