package com.ttibuttibu.chat.coreapi.domain.model.entity;

import com.ttibuttibu.chat.coreapi.common.entity.BaseTimeEntity;
import com.ttibuttibu.chat.coreapi.domain.catalog.entity.ModelCatalog;
import com.ttibuttibu.chat.coreapi.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_uid")
    private Long modelUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_uid", nullable = false)
    private Member member;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="model_catalog_uid", nullable = false)
    private ModelCatalog modelCatalog;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    public void toggleIsDefault() {
        this.isDefault = !this.isDefault;
    }
}