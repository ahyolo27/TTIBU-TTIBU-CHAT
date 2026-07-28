package com.ttibuttibu.chat.coreapi.domain.chat.repository;

import com.ttibuttibu.chat.coreapi.domain.chat.entity.Chat;
import com.ttibuttibu.chat.coreapi.domain.chat.enums.ChatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // 채팅방 내 전체 대화 조회
    List<Chat> findAllByRoom_RoomUidOrderByCreatedAtAscChatUidAsc(Long roomUid);

    // 요청된 대화 ID 목록이 특정 채팅방에 속하는지 검증 및 조회
    List<Chat> findAllByChatUidInAndRoom_RoomUid(List<Long> chatUids, Long roomUid);

    // 채팅방 내 특정 대화 존재 여부 확인
    boolean existsByChatUidAndRoom_RoomUid(Long chatUid, Long roomUid);

    // 그룹 내 특정 타입 대화 조회
    List<Chat> findAllByGroup_GroupUidAndChatType(Long groupUid, ChatType chatType);

    // 그룹에 복사된 대화 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from Chat c
            where c.group.groupUid = :groupId
              and c.chatType = com.ttibuttibu.chat.coreapi.domain.chat.enums.ChatType.GROUP
            """)
    int deleteAllGroupCopies(@Param("groupId") Long groupId);

    // 키워드 기반 대화 검색
    @Query(
            value = """
                    SELECT c.*
                    FROM chat c
                    JOIN room r ON r.room_uid = c.room_id
                    WHERE r.owner_id = :memberId
                      AND c.group_id IS NULL
                      AND c.status   = 'SUMMARY_KEYWORDS'
                      AND c.is_chat  = 'CHAT'
                      AND c.search_content ILIKE ALL (CAST(:keywords AS text[]))
                    ORDER BY c.created_at DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM chat c
                    JOIN room r ON r.room_uid = c.room_id
                    WHERE r.owner_id = :memberId
                      AND c.group_id IS NULL
                      AND c.status   = 'SUMMARY_KEYWORDS'
                      AND c.is_chat  = 'CHAT'
                      AND c.search_content ILIKE ALL (CAST(:keywords AS text[]))
                    """,
            nativeQuery = true
    )
    Page<Chat> searchByAllKeywords(Long memberId, String[] keywords, Pageable pageable);

    // 채팅방 삭제 전 그룹 스냅샷 대화 연결 해제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Chat c
               set c.room = null
             where c.room.roomUid = :roomUid
               and c.chatType = 'GROUP'
               and c.group.groupUid is not null
            """)
    int detachGroupChats(Long roomUid);

    // 채팅방 내 대화 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from Chat c
             where c.room.roomUid = :roomUid
            """)
    int deleteChatsByRoom(Long roomUid);

    // 회원의 전체 대화 조회
    @Query(
            value = """
                    SELECT c.*
                    FROM chat c
                    WHERE c.status  = 'SUMMARY_KEYWORDS'
                      AND c.is_chat = 'CHAT'
                      AND c.group_id IS NULL
                      AND EXISTS (
                          SELECT 1
                          FROM room r
                          WHERE r.room_uid = c.room_id
                            AND r.owner_id = :memberId
                      )
                    ORDER BY c.created_at DESC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM chat c
                    WHERE c.status  = 'SUMMARY_KEYWORDS'
                      AND c.is_chat = 'CHAT'
                      AND c.group_id IS NULL
                      AND EXISTS (
                          SELECT 1
                          FROM room r
                          WHERE r.room_uid = c.room_id
                            AND r.owner_id = :memberId
                      )
                    """,
            nativeQuery = true
    )
    Page<Chat> findAllChats(Long memberId, Pageable pageable);
}