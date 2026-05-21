package com.ttibuttibu.chat.coreapi.domain.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRenameResponseDto {

    private Long roomId;

    private LocalDateTime updatedAt;
}
