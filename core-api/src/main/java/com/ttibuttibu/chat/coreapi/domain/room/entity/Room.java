package com.ttibuttibu.chat.coreapi.domain.room.entity;

import com.ttibuttibu.chat.coreapi.common.entity.BaseTimeEntity;
import com.ttibuttibu.chat.coreapi.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Room extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_uid")
    private Long roomUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @Column(nullable = false)
    private String name;

    // TODO: 컴파일용, 추후 제거
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "branch_view", columnDefinition = "json", nullable = false)
    private String branchView;

    // TODO: 컴파일용, 추후 제거
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "chat_info", columnDefinition = "json", nullable = false)
    private String chatInfo;

    @Builder.Default
    @Column(name = "graph_version", nullable = false)
    private long graphVersion = 0L;

    /**
     * 새 채팅방 생성
     */
    public static Room create(Member owner, String name) {
        return Room.builder()
                .owner(owner)
                .name(name != null ? name : "새 대화방")
                .branchView("{}")
                .chatInfo("{}")
                .graphVersion(0L)
                .build();
    }

    /**
     * 채팅방 이름 수정
     */
    public void updateName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    public void bumpGraphVersion() {
        this.graphVersion++;
    }
}
