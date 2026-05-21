package com.ttibuttibu.chat.coreapi.domain.catalog.repository;

public interface ProviderCatalogRepositoryCustom {

    Long upsertReturningId(String providerCode);
}