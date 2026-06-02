package com.sunmax.device.util;

import com.google.common.collect.Maps;
import com.sunmax.device.dao.model.AssetTypeDao;
import com.sunmax.device.entity.model.AssetTypeEntity;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AssetTypeUtil {

    public static Map<String, String> getTypeNameMap(Set<String> typeIds, AssetTypeDao assetTypeDao) {
        //返回的对象map
        Map<String, String> resultMap = Maps.newHashMap();
        List<AssetTypeEntity> assetTypeList = assetTypeDao.findAllById(typeIds);
        if (CollectionUtils.isNotEmpty(assetTypeList)) {
            Set<String> parentIds = assetTypeList.stream().map(AssetTypeEntity::getParentId).collect(Collectors.toSet());
            Map<String, String> parentNameMap = assetTypeDao.findAllById(parentIds).stream().collect(Collectors.toMap(AssetTypeEntity::getId,
                    AssetTypeEntity::getTypeName));
            assetTypeList.forEach(assetType -> {
                if (parentNameMap.containsKey(assetType.getParentId())) {
                    resultMap.put(assetType.getId(), parentNameMap.get(assetType.getParentId()) + "-" + assetType.getTypeName());
                } else {
                    resultMap.put(assetType.getId(), assetType.getTypeName());
                }
            });
        }
        return resultMap;
    }

}
