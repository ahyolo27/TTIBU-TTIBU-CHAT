package com.ttibuttibu.chat.coreapi.domain.group.service;

public interface GroupViewService {

    void updateGroupView(Long memberId, String contentJson);

    String getGroupView(Long memberId);
}
