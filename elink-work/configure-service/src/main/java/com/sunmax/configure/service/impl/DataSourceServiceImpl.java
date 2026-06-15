package com.sunmax.configure.service.impl;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.configure.dao.DataSourceDao;
import com.sunmax.configure.dto.DataSourceDetailDto;
import com.sunmax.configure.dto.DataSourceListDto;
import com.sunmax.configure.entity.DataSourceEntity;
import com.sunmax.configure.service.DataSourceService;
import com.sunmax.configure.vo.DataSourceChangeVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DataSourceServiceImpl implements DataSourceService {

    @Resource
    private DataSourceDao dataSourceDao;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveDataSource(DataSourceChangeVo dataSourceChangeVo) {
        if (dataSourceChangeVo != null) {
            List<DataSourceEntity> dataSourceList = dataSourceDao.findAll(Example.of(DataSourceEntity.builder().name(dataSourceChangeVo.getName()).build()));
            //新增数据源
            if (StringUtil.isEmpty(dataSourceChangeVo.getId())) {
                if (CollectionUtils.isNotEmpty(dataSourceList)) {
                    return ResponseResult.paramShow(dataSourceChangeVo.getName(), ResponseResult.PARAM_EXIST);
                }
                DataSourceEntity dataSource = new DataSourceEntity();
                BeanUtils.copyProperties(dataSourceChangeVo, dataSource);
                dataSource.setCreateId(dataSourceChangeVo.getUserId());
                dataSource.setUpdateId(dataSourceChangeVo.getUserId());
                dataSourceDao.save(dataSource);
                return ResponseResult.ok();
            } else {
                //校验数据源名称
                dataSourceList = dataSourceList.stream().filter(s -> !Objects.equals(s.getId(), dataSourceChangeVo.getId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(dataSourceList)) {
                    return ResponseResult.paramShow(dataSourceChangeVo.getName(), ResponseResult.PARAM_EXIST);
                }
                Optional<DataSourceEntity> optional = dataSourceDao.findById(dataSourceChangeVo.getId());
                if (optional.isPresent()) {
                    DataSourceEntity dataSource = new DataSourceEntity();
                    BeanUtils.copyProperties(dataSourceChangeVo, dataSource);
                    dataSource.setCreateId(optional.get().getCreateId());
                    dataSource.setCreateTime(optional.get().getCreateTime());
                    dataSource.setUpdateId(dataSourceChangeVo.getUserId());
                    dataSourceDao.save(dataSource);
                    return ResponseResult.ok();
                }
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteDataSourceByIds(List<String> ids) {
        List<DataSourceEntity> dataSourceList = dataSourceDao.findAllById(ids);
        if (CollectionUtils.isNotEmpty(dataSourceList)) {
            dataSourceDao.deleteAll(dataSourceList);
            return ResponseResult.ok();
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<List<DataSourceListDto>> queryDataSourceList(String name) {
        //返回的集合
        List<DataSourceListDto> resultList = Lists.newArrayList();
        //根据查询条件查询数据源数据
        List<DataSourceEntity> dataSourceList = dataSourceDao.findAll((Specification<DataSourceEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtil.isNotEmpty(name)) {//关键词 名称
                list.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
        if (CollectionUtils.isNotEmpty(dataSourceList)) {
            resultList = dataSourceList.stream().map(pel -> {
                DataSourceListDto result = new DataSourceListDto();
                BeanUtils.copyProperties(pel, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<DataSourceDetailDto> findDataSourceById(String id) {
        //返回的对象
        DataSourceDetailDto result = new DataSourceDetailDto();
        Optional<DataSourceEntity> optional = dataSourceDao.findById(id);
        if (optional.isPresent()) {
            DataSourceEntity dataSource = optional.get();
            BeanUtils.copyProperties(dataSource, result);
        }
        return ResponseResult.ok(result);
    }

}
