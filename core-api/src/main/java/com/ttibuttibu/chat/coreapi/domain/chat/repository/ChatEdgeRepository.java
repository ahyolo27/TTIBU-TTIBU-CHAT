package com.ttibuttibu.chat.coreapi.domain.chat.repository;

import com.ttibuttibu.chat.coreapi.domain.chat.entity.ChatEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatEdgeRepository extends JpaRepository<ChatEdge, Long> {

    // 채팅방 내 전체 연결선 조회
    List<ChatEdge> findAllByRoom_RoomUidOrderByChatEdgeUidAsc(Long roomUid);

    // 채팅방 내 출발·도착 대화 기준 연결선 단건 조회
    Optional<ChatEdge> findByRoom_RoomUidAndSourceChat_ChatUidAndTargetChat_ChatUid(
            Long roomUid,
            Long sourceChatUid,
            Long targetChatUid
    );

    // 채팅방 삭제 전 연결선 전체 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int deleteAllByRoom_RoomUid(Long roomUid);

    // 대화 삭제 전 해당 대화와 연결된 모든 연결선 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from ChatEdge edge
            where edge.sourceChat.chatUid = :chatUid
               or edge.targetChat.chatUid = :chatUid
            """)
    int deleteAllByChatUid(@Param("chatUid") Long chatUid);
}
