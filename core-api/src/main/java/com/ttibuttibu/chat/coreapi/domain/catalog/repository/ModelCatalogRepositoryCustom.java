package com.ttibuttibu.chat.coreapi.domain.catalog.repository;

public interface ModelCatalogRepositoryCustom {

    void upsert(Long providerUid, String modelCode, String modelName);
}
