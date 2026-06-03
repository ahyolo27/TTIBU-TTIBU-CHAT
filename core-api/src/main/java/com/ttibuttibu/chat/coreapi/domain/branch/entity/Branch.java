package com.ttibuttibu.chat.coreapi.domain.branch.entity;

import com.ttibuttibu.chat.coreapi.common.entity.BaseTimeEntity;
import com.ttibuttibu.chat.coreapi.domain.chat.entity.Chat;
import com.ttibuttibu.chat.coreapi.domain.room.entity.Room;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "branch",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_branch_room_name", columnNames = {"room_id", "name"})
        },
        indexes = {
                @Index(name = "idx_branch_room", columnList = "room_id"),
                @Index(name = "idx_branch_root_chat", columnList = "root_chat_id"),
                @Index(name = "idx_branch_parent_chat", columnList = "parent_chat_id")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Branch extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_uid")
    private Long branchUid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_chat_id")
    private Chat rootChat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_chat_id")
    private Chat parentChat;

    public static Branch create(Room room, String name, Chat rootChat, Chat parentChat) {
        return Branch.builder()
                .room(room)
                .name(name)
                .rootChat(rootChat)
                .parentChat(parentChat)
                .build();
    }

    public void rename(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }
}
