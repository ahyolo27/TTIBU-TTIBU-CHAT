package com.ttibuttibu.chat.coreapi.domain.group.repository;

import com.ttibuttibu.chat.coreapi.domain.group.entity.GroupView;
import com.ttibuttibu.chat.coreapi.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupViewRepository extends JpaRepository<GroupView, Long> {

    Optional<GroupView> findByMember(Member member);
}
