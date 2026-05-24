package com.ttibuttibu.chat.coreapi.domain.room.service;

import com.ttibuttibu.chat.coreapi.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncChatProcessor {

    private final ChatService chatService;

    /**
     * 채팅 비동기 처리 (트랜잭션 종료 이후 실행)
     */
    @Async("aiTaskExecutor")
    public void processAsync(Long chatId, Long branchId, String decryptedKey, String contextPrompt) {
        if (chatId == null) {
            log.error("[ASYNC] chatId 가 null 입니다.");
            return;
        }
        if (branchId == null) {
            log.error("[ASYNC] branchId 가 null 입니다.");
            return;
        }

        log.info("[ASYNC] processAsync 호출: chatId={}, branchId={}, ctxLen={}",
                chatId,
                branchId,
                (contextPrompt == null ? 0 : contextPrompt.length()));

        try {
            runWithRetry(() -> chatService.processChatAsync(
                            chatId,
                            branchId,
                            decryptedKey,
                            contextPrompt
                    ),
                    Duration.ofSeconds(1)
            );

            log.info("[ASYNC] 비동기 채팅 처리 성공 → chatId={}", chatId);
        } catch (Exception e) {
            log.error("[ASYNC] processAsync 실행 실패", e);
        }
    }

    // 공통 재시도 유틸리티
    private void runWithRetry(Runnable task, Duration delay) throws Exception {
        int attempt = 0;

        while (true) {
            try {
                task.run();
                return;
            } catch (Exception e) {
                attempt++;
                if (attempt > 2) {
                    throw e;
                }
                log.warn("[ASYNC][Retry] {}회 실패 → {}초 후 재시도. Error={}",
                        attempt, delay.toSeconds(), e.getMessage());
                try {
                    Thread.sleep(delay.toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ie;
                }
            }
        }
    }
}
