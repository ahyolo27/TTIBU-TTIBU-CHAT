package com.ttibuttibu.chat.coreapi.domain.branch.repository;

import com.ttibuttibu.chat.coreapi.domain.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    // 채팅방 내 전체 브랜치 조회
    List<Branch> findAllByRoom_RoomUidOrderByCreatedAtAscBranchUidAsc(Long roomUid);

    // 채팅방 소속 검증을 포함한 브랜치 단건 조회
    Optional<Branch> findByBranchUidAndRoom_RoomUid(Long branchUid, Long roomUid);

    // 채팅방 내 브랜치 이름 중복 여부 확인
    boolean existsByRoom_RoomUidAndName(Long roomUid, String name);

    // 채팅방 삭제 전 브랜치 전체 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int deleteAllByRoom_RoomUid(Long roomUid);
}
