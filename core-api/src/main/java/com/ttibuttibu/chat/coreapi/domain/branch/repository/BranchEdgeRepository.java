package com.ttibuttibu.chat.coreapi.domain.branch.repository;

import com.ttibuttibu.chat.coreapi.domain.branch.entity.BranchEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface BranchEdgeRepository extends JpaRepository<BranchEdge, Long> {

    // 채팅방 내 브랜치별 연결선 구성 조회
    List<BranchEdge> findAllByBranch_Room_RoomUidOrderByBranch_BranchUidAscOrderIndexAsc(Long roomUid);

    // 특정 브랜치의 연결선 구성 조회
    List<BranchEdge> findAllByBranch_BranchUidOrderByOrderIndexAsc(Long branchUid);

    // 브랜치 삭제 전 해당 브랜치의 연결선 구성 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int deleteAllByBranch_BranchUid(Long branchUid);

    // 연결선 삭제 전 해당 연결선을 참조하는 브랜치 구성 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int deleteAllByChatEdge_ChatEdgeUid(Long chatEdgeUid);
}
