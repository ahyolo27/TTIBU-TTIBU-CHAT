package com.ttibuttibu.chat.coreapi.domain.branch.repository;

import com.ttibuttibu.chat.coreapi.domain.branch.entity.BranchChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface BranchChatRepository extends JpaRepository<BranchChat, Long> {

    // 채팅방 내 브랜치별 대화 구성 조회
    List<BranchChat> findAllByBranch_Room_RoomUidOrderByBranch_BranchUidAscOrderIndexAsc(Long roomUid);

    // 특정 브랜치의 대화 구성 조회
    List<BranchChat> findAllByBranch_BranchUidOrderByOrderIndexAsc(Long branchUid);

    // 브랜치 삭제 전 해당 브랜치의 대화 구성 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int deleteAllByBranch_BranchUid(Long branchUid);

    // 대화 삭제 전 해당 대화를 참조하는 브랜치 구성 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    int deleteAllByChat_ChatUid(Long chatUid);
}
