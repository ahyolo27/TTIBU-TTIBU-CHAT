package com.ttibuttibu.chat.coreapi.domain.chat.entity;

import com.ttibuttibu.chat.coreapi.common.entity.BaseTimeEntity;
import com.ttibuttibu.chat.coreapi.domain.room.entity.Room;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

@Entity
@Table(
        name = "chat_edge",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chat_edge_room_source_target",
                        columnNames = {"room_id", "source_chat_id", "target_chat_id"}
                )
        },
        indexes = {
                @Index(name = "idx_chat_edge_room", columnList = "room_id"),
                @Index(name = "idx_chat_edge_source", columnList = "source_chat_id"),
                @Index(name = "idx_chat_edge_target", columnList = "target_chat_id")
        }
)
@Check(constraints = "source_chat_id <> target_chat_id")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatEdge extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_edge_uid")
    private Long chatEdgeUid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_chat_id", nullable = false)
    private Chat sourceChat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_chat_id", nullable = false)
    private Chat targetChat;

    public static ChatEdge create(Room room, Chat sourceChat, Chat targetChat) {
        return ChatEdge.builder()
                .room(room)
                .sourceChat(sourceChat)
                .targetChat(targetChat)
                .build();
    }
}
