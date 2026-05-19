package com.ttibuttibu.chat.coreapi.domain.room.service;

import com.ttibuttibu.chat.coreapi.domain.chat.dto.ChatCreateRequestDto;
import com.ttibuttibu.chat.coreapi.domain.chat.dto.ChatCreateResponseDto;
import com.ttibuttibu.chat.coreapi.domain.room.dto.RoomCreateRequestDto;
import com.ttibuttibu.chat.coreapi.domain.room.dto.RoomRenameRequestDto;
import com.ttibuttibu.chat.coreapi.domain.room.dto.RoomRenameResponseDto;
import com.ttibuttibu.chat.coreapi.domain.room.dto.RoomResponseDto;

import java.util.List;

public interface RoomService {

    Long createRoom(Long memberId, RoomCreateRequestDto request);

    List<RoomResponseDto.RoomListInfo> getList(Long memberUid);

    RoomResponseDto.ChatBranchUpdatedInfo saveChatAndBranch(Long roomUid, Long memberUid, String chatInfo, String branchView);

    RoomResponseDto.ChatBranchInfo getChatAndBranch(Long roomUid, Long memberUid);

    void delete(Long roomUid, Long memberUid);

    void isOwner(Long memberId, Long roomId);

    RoomRenameResponseDto updateRoomName(Long memberId, Long roomId, RoomRenameRequestDto request);

    ChatCreateResponseDto createChatInRoom(Long memberId, Long roomId, ChatCreateRequestDto request);
}
