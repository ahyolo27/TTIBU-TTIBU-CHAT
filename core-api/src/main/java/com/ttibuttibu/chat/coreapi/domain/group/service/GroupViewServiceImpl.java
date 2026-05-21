package com.ttibuttibu.chat.coreapi.domain.group.service;

import com.ttibuttibu.chat.coreapi.common.error.ApiException;
import com.ttibuttibu.chat.coreapi.common.error.ErrorCode;
import com.ttibuttibu.chat.coreapi.domain.group.entity.GroupView;
import com.ttibuttibu.chat.coreapi.domain.group.repository.GroupViewRepository;
import com.ttibuttibu.chat.coreapi.domain.member.entity.Member;
import com.ttibuttibu.chat.coreapi.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupViewServiceImpl implements GroupViewService {

    private final MemberRepository memberRepository;
    private final GroupViewRepository groupViewRepository;

    @Override
    @Transactional
    public void updateGroupView(Long memberId, String contentJson) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        GroupView groupView = groupViewRepository.findByMember(member)
                .map(existing -> {
                    existing.updateContent(contentJson);
                    return existing;
                })
                .orElseGet(() -> GroupView.create(member, contentJson));

        groupViewRepository.save(groupView);

        log.info("[GROUP_VIEW] 그룹 뷰 JSON 저장 완료 → memberId={}, length={}", memberId, contentJson.length());
    }

    @Override
    @Transactional(readOnly = true)
    public String getGroupView(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEMBER_NOT_FOUND));

        return groupViewRepository.findByMember(member)
                .map(GroupView::getContent)
                .orElse("{}");
    }
}
