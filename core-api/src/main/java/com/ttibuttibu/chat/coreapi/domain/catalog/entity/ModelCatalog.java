package com.ttibuttibu.chat.coreapi.domain.catalog.entity;

import com.ttibuttibu.chat.coreapi.common.entity.BaseTimeEntity;
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
@Table(
        name = "model_catalog",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"provider_catalog_uid", "code"}
        )
)
public class ModelCatalog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_catalog_uid")
    private Long modelUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_catalog_uid", nullable = false)
    private ProviderCatalog provider;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
