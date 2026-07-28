package com.ttibuttibu.chat.coreapi.domain.room.repository;

import com.ttibuttibu.chat.coreapi.domain.room.dto.RoomResponseDto;
import com.ttibuttibu.chat.coreapi.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // 회원의 채팅방 목록과 각 채팅방의 마지막 대화 조회
    @Query("""
            select new com.ttibuttibu.chat.coreapi.domain.room.dto.RoomResponseDto.RoomListInfo(
                r.roomUid,
                r.name,
                c.question,
                c.createdAt
            )
            from Room r
            join Chat c
              on c.room = r
             and c.chatUid = (
                  select max(c2.chatUid)
                  from Chat c2
                  where c2.room = r
             )
            where r.owner.memberUid = :memberUid
            order by r.roomUid asc
            """)
    List<RoomResponseDto.RoomListInfo> findRoomListWithLastChat(Long memberUid);

    // 소유자 검증 후 채팅방 삭제
    int deleteByRoomUidAndOwner_MemberUid(Long roomUid, Long memberUid);

    // TODO: RDB 전환 완료 후 삭제
    // 기존 JSON 기반 채팅/브랜치 뷰 저장
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE room
            SET branch_view = CAST(:branchView AS json),
                chat_info   = CAST(:chatInfo   AS json),
                updated_at  = now()
            WHERE room_uid = :roomUid
            """, nativeQuery = true)
    int updateViews(Long roomUid, String chatInfo, String branchView);

    // 회원이 특정 채팅방을 소유하는지 확인
    boolean existsByRoomUidAndOwner_MemberUid(Long roomUid, Long memberUid);

    // 소유자 검증 후 채팅방 단건 조회
    Optional<Room> findByRoomUidAndOwner_MemberUid(Long roomUid, Long memberUid);

    // 채팅방의 최종 수정 시각 조회
    @Query(value = "SELECT updated_at FROM room WHERE room_uid = :roomUid", nativeQuery = true)
    LocalDateTime getUpdatedAtByRoomUid(Long roomUid);

    // TODO: RDB 전환 완료 후 삭제
    // 기존 JSON 기반 채팅/브랜치 뷰 조회
    @Query(value = """
            SELECT r.chat_info::text AS chatInfo,
                   r.branch_view::text AS branchView
            FROM room r
            WHERE r.room_uid = :roomUid
            """, nativeQuery = true)
    RoomViewsRow findViewsByRoomUid(Long roomUid);

    // TODO: RDB 전환 완료 후 삭제
    interface RoomViewsRow {
        String getChatInfo();

        String getBranchView();
    }
}
