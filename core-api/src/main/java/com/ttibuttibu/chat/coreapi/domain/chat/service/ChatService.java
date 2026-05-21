package com.ttibuttibu.chat.coreapi.domain.chat.service;

import com.ttibuttibu.chat.coreapi.domain.chat.dto.ChatRequestDto;
import com.ttibuttibu.chat.coreapi.domain.chat.dto.ChatResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

    Page<ChatResponseDto.SearchedResultInfo> searchByKeywords(List<String> keywords, Pageable pageable, Long memberUid);

    ChatResponseDto.CopiedChatInfo copyChat(ChatRequestDto.CopyChat request, Long memberUid);

    void processChatAsync(Long chatId, Long branchId, String apiKey, String model, String provider, boolean useLlm, String contextPrompt);
}
