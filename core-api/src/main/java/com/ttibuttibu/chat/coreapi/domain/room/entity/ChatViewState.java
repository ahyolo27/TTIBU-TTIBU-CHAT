package com.ttibuttibu.chat.coreapi.domain.room.entity;

import com.ttibuttibu.chat.coreapi.common.entity.BaseTimeEntity;
import com.ttibuttibu.chat.coreapi.domain.chat.entity.Chat;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "chat_view_state",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chat_view_state_room_chat", columnNames = {"room_id", "chat_id"})
        },
        indexes = {
                @Index(name = "idx_chat_view_state_room", columnList = "room_id"),
                @Index(name = "idx_chat_view_state_chat", columnList = "chat_id")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatViewState extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_view_state_uid")
    private Long chatViewStateUid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(nullable = false)
    private double x;

    @Column(nullable = false)
    private double y;

    @Builder.Default
    @Column(name = "is_collapsed", nullable = false)
    private boolean isCollapsed = false;

    public static ChatViewState create(Room room, Chat chat, double x, double y) {
        return ChatViewState.builder()
                .room(room)
                .chat(chat)
                .x(x)
                .y(y)
                .isCollapsed(false)
                .build();
    }

    public void updatePosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void updateCollapsed(boolean collapsed) {
        this.isCollapsed = collapsed;
    }
}
