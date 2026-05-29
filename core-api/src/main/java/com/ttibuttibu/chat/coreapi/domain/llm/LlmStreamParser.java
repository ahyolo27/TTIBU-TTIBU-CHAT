package com.ttibuttibu.chat.coreapi.domain.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LlmStreamParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Chunk에서 content(답변 텍스트) 부분만 추출
     */
    public String extractDeltaContent(String chunk) {
        if (chunk == null || chunk.isBlank()) return null;

        try {
            String json = normalizeJson(chunk);
            JsonNode root = safeParse(json);
            if (root == null) return null;

            root = unwrapFirstIfArray(root);

                JsonNode choices = root.path("choices");
                if (choices.isArray() && !choices.isEmpty()) {
                    JsonNode delta = choices.get(0).path("delta");
                    if (delta != null && delta.has("content")) {
                        return delta.get("content").asText();
                    }
                }

        } catch (Exception e) {
            log.warn("[Parser] extractDeltaContent 오류 err={}, chunk={}",
                    e.getMessage(), chunk);
        }

        return null;
    }

    /**
     * Chunk가 [DONE] 종료 신호인지 여부
     */
    public boolean isDoneChunk(String chunk) {
        if (chunk == null) return false;

        String c = chunk.trim();

        try {
            if (c.equals("[DONE]") || c.equalsIgnoreCase("data: [DONE]"))
                return true;

        } catch (Exception e) {
            log.warn("[Parser] isDoneChunk 파싱 오류 chunk={}, err={}",
                    chunk, e.getMessage());
        }

        return false;
    }

    /**
     * usage 추출
     */
    public JsonNode extractUsage(String chunk) {
        if (chunk == null || chunk.isBlank()) return null;

        try {
            String json = normalizeJson(chunk);
            JsonNode root = safeParse(json);
            if (root == null) return null;

            root = unwrapFirstIfArray(root);

            if (root.has("usage"))
                return root.get("usage");

        } catch (Exception e) {
            log.warn("[Parser] usage 파싱 실패");
        }
        return null;
    }

    /* 안전한 JSON 파싱 */
    private JsonNode safeParse(String raw) {
        try {
            return objectMapper.readTree(raw);
        } catch (Exception e) {
            log.debug("[Parser] safeParse 실패: {}", e.getMessage());
            return null;
        }
    }

    private JsonNode unwrapFirstIfArray(JsonNode root) {
        if (root != null && root.isArray() && !root.isEmpty()) {
            // 배열 전체를 다 도는 대신, delta 추출에는 "첫 번째 조각"만 사용한다.
            return root.get(0);
        }
        return root;
    }

    private String normalizeJson(String raw) {
        String json = raw.trim();
        if (json.startsWith("data:")) {
            json = json.substring(5).trim();
        }
        return json;
    }
}
