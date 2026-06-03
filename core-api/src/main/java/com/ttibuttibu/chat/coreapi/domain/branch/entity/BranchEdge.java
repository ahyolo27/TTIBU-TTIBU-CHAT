package com.ttibuttibu.chat.coreapi.domain.branch.entity;

import com.ttibuttibu.chat.coreapi.common.entity.BaseTimeEntity;
import com.ttibuttibu.chat.coreapi.domain.chat.entity.ChatEdge;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "branch_edge",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_branch_edge_branch_edge", columnNames = {"branch_id", "chat_edge_id"})
        },
        indexes = {
                @Index(name = "idx_branch_edge_branch", columnList = "branch_id"),
                @Index(name = "idx_branch_edge_chat_edge", columnList = "chat_edge_id")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BranchEdge extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_edge_uid")
    private Long branchEdgeUid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_edge_id", nullable = false)
    private ChatEdge chatEdge;

    @Column(nullable = false)
    private Integer orderIndex;

    public static BranchEdge create(Branch branch, ChatEdge chatEdge, Integer orderIndex) {
        return BranchEdge.builder()
                .branch(branch)
                .chatEdge(chatEdge)
                .orderIndex(orderIndex)
                .build();
    }
}
