package com.ttibuttibu.chat.coreapi.domain.room.repository;

import com.ttibuttibu.chat.coreapi.domain.room.entity.ChatViewState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface ChatViewStateRepository extends JpaRepository<ChatViewState, Long> {

    // 채팅방 내 전체 대화 노드의 화면 상태 조회
    List<ChatViewState> findAllByRoom_RoomUidOrderByChat_ChatUidAsc(Long roomUid);

    // 채팅방 내 특정 대화 노드의 화면 상태 조회
    Optional<ChatViewState> findByRoom_RoomUidAndChat_ChatUid(Long roomUid, Long chatUid);

    // 채팅방 삭제 전 화면 상태 전체 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int deleteAllByRoom_RoomUid(Long roomUid);

    // 대화 삭제 전 해당 대화의 화면 상태 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int deleteAllByChat_ChatUid(Long chatUid);
}
