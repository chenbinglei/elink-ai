package com.sunmax.together.dao.asset;

import com.sunmax.together.entity.assets.ModuleLibraryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ModuleLibraryDao extends JpaRepository<ModuleLibraryEntity, String>, JpaSpecificationExecutor<ModuleLibraryEntity> {
    void deleteAllByIdIn(List<String> idList);

    List<ModuleLibraryEntity> findAllByModuleFactory(String moduleFactory);
}
