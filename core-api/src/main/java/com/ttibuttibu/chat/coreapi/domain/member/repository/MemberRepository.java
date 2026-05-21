package com.ttibuttibu.chat.coreapi.domain.member.repository;

import com.ttibuttibu.chat.coreapi.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
}