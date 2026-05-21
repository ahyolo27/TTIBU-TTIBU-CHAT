package com.ttibuttibu.chat.coreapi.domain.member.service;

import com.ttibuttibu.chat.coreapi.domain.member.dto.MemberRequestDto;
import com.ttibuttibu.chat.coreapi.domain.member.dto.MemberResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {
    MemberResponseDto.RegisteredMemberInfo register(MemberRequestDto.RegisterMember registerMember);

    MemberResponseDto.MemberInfo login(MemberRequestDto.LoginMember loginMember, HttpServletRequest request, HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);
}