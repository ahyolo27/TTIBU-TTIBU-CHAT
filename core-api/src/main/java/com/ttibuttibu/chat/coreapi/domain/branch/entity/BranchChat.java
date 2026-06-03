package com.ttibuttibu.chat.coreapi.domain.branch.entity;

import com.ttibuttibu.chat.coreapi.common.entity.BaseTimeEntity;
import com.ttibuttibu.chat.coreapi.domain.chat.entity.Chat;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "branch_chat",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_branch_chat_branch_chat", columnNames = {"branch_id", "chat_id"})
        },
        indexes = {
                @Index(name = "idx_branch_chat_branch", columnList = "branch_id"),
                @Index(name = "idx_branch_chat_chat", columnList = "chat_id")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BranchChat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_chat_uid")
    private Long branchChatUid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(nullable = false)
    private Integer orderIndex;

    public static BranchChat create(Branch branch, Chat chat, Integer orderIndex) {
        return BranchChat.builder()
                .branch(branch)
                .chat(chat)
                .orderIndex(orderIndex)
                .build();
    }
}
