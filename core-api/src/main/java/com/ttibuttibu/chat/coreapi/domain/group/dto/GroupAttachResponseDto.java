package com.ttibuttibu.chat.coreapi.domain.group.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupAttachResponseDto {

    private Long roomId;

    private Long newChatId;

    private Long groupId;

    private LocalDateTime createdAt;

}
