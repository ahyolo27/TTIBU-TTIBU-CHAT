package com.ttibuttibu.chat.coreapi.domain.member.dto;

import com.ttibuttibu.chat.coreapi.domain.key.dto.KeyResponseDto;
import com.ttibuttibu.chat.coreapi.domain.model.dto.ModelResponseDto;
import lombok.Builder;

import java.util.List;

public class MemberResponseDto {

    /**
     * 회원가입 응답 DTO
     */
    @Builder
    public record RegisteredMemberInfo(
            Long memberUid
    ) {
    }

    /**
     * 로그인 응답 DTO
     */
    @Builder
    public record MemberInfo(
            String email,
            String name
    ) {
    }

    /**
     * 내 정보 조회 응답 DTO
     */
    @Builder
    public record MemberDetailInfo(
            KeyResponseDto.TokenInfo tokens,
            List<KeyResponseDto.KeyListInfo> keys,
            List<ModelResponseDto.ModelListInfo> models
    ) {
    }
}