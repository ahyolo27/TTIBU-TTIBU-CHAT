package com.ttibuttibu.chat.coreapi.domain.group.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GroupUpdateRequestDto {

    private List<Long> nodes;

    private Boolean summaryRegen;

    private String name;
}
