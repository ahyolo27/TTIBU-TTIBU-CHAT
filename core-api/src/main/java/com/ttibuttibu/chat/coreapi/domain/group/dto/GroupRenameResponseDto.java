package com.ttibuttibu.chat.coreapi.domain.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRenameResponseDto {

    private Long groupId;
    private String name;
    private LocalDateTime updatedAt;
}
