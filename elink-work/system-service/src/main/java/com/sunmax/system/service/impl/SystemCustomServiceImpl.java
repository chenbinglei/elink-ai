package com.sunmax.system.service.impl;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.system.dao.CustomDao;
import com.sunmax.system.dto.CustomChangeDto;
import com.sunmax.system.entity.CustomEntity;
import com.sunmax.system.service.SystemCustomService;
import com.sunmax.system.vo.CustomChangeVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class SystemCustomServiceImpl implements SystemCustomService {

    @Autowired
    private CustomDao customDao;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveSystemCustom(CustomChangeVo customChangeVo, MultipartFile logoFile) {
        //新增系统自定义数据
        if (StringUtil.isEmpty(customChangeVo.getId())) {
            List<CustomEntity> customList = customDao.findAll();
            if (CollectionUtils.isNotEmpty(customList)) {
                return ResponseResult.paramError("系统自定义数据已存在,不允许新增");
            }
            CustomEntity customEntity = new CustomEntity();
            BeanUtils.copyProperties(customChangeVo, customEntity);
            customEntity.setUpdateId(customChangeVo.getUserId());
            customEntity.setPlatformLogo(FileUtil.getImagePath(logoFile, null));
            customDao.save(customEntity);
            return ResponseResult.ok();
        } else {
            //编辑系统自定义数据
            Optional<CustomEntity> optional = customDao.findById(customChangeVo.getId());
            if (optional.isPresent()) {
                CustomEntity customEntity = new CustomEntity();
                BeanUtils.copyProperties(optional.get(), customEntity);
                customEntity.setPlatformName(customChangeVo.getPlatformName());
                customEntity.setLargeTitle(customChangeVo.getLargeTitle());
                if (logoFile != null && !logoFile.isEmpty()) {
                    customEntity.setPlatformLogo(FileUtil.getImagePath(logoFile, optional.get().getPlatformLogo()));
                }
                customEntity.setUpdateId(customChangeVo.getUserId());
                customDao.save(customEntity);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<CustomChangeDto> querySystemCustom() {
        //返回的对象
        CustomChangeDto result = new CustomChangeDto();
        List<CustomEntity> customList = customDao.findAll();
        if (CollectionUtils.isNotEmpty(customList)) {
            BeanUtils.copyProperties(customList.get(0), result);
        }
        return ResponseResult.ok(result);
    }

}
