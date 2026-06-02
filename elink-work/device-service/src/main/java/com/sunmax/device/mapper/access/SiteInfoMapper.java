package com.sunmax.device.mapper.access;

import com.sunmax.common.vo.device.SiteInfoChangeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface SiteInfoMapper {

    void batchInsertSiteInfoList(@Param("siteInfoChangeVos") List<SiteInfoChangeVo> siteInfoChangeVos);
}
