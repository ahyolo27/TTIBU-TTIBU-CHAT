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
        name = "provider_catalog",
        uniqueConstraints = @UniqueConstraint (
                columnNames = "code"
        )
)
public class ProviderCatalog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_catalog_uid")
    private Long providerUid;

    @Column(name = "code", nullable = false)
    private String code;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
