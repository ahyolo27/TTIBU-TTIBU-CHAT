package com.ttibuttibu.chat.coreapi.domain.group.repository;

import com.ttibuttibu.chat.coreapi.domain.group.entity.Group;
import com.ttibuttibu.chat.coreapi.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findAllByOwnerOrderByUpdatedAtDesc(Member owner);

}
