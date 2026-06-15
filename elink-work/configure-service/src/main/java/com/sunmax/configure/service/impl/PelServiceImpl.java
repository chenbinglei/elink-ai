package com.sunmax.configure.service.impl;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.local.LocalFileUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.util.oss.OssFileUtil;
import com.sunmax.common.vo.LocalParamVo;
import com.sunmax.configure.dao.PelDao;
import com.sunmax.configure.dto.PelDetailDto;
import com.sunmax.configure.dto.PelListDto;
import com.sunmax.configure.entity.PelEntity;
import com.sunmax.configure.service.PelService;
import com.sunmax.configure.vo.PelChangeVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PelServiceImpl implements PelService {

    @Resource
    private PelDao pelDao;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> savePel(PelChangeVo pelChangeVo, MultipartFile file, MultipartFile dataFile) {
        //定义图元实体类
        PelEntity pel;
        switch (pelChangeVo.getUpdateType()) {
            case 1: //新增图元
                pel = new PelEntity(pelChangeVo);
                if (file != null && !file.isEmpty()) { //文件
                    pel.setFileName(file.getOriginalFilename());
                    if (LocalParamVo.FILE_TYPE) { //文件类型
                        pel.setFilePath(OssFileUtil.checkFile(file));
                    } else {
                        pel.setFilePath(LocalFileUtil.checkFile(file));
                    }
                }
                if (dataFile != null && !dataFile.isEmpty()) { //数据文件
                    pel.setDataName(dataFile.getOriginalFilename());
                    if (LocalParamVo.FILE_TYPE) { //文件类型
                        pel.setDataPath(OssFileUtil.checkFile(dataFile));
                    } else {
                        pel.setDataPath(LocalFileUtil.checkFile(dataFile));
                    }
                }
                pel.setCreateId(pelChangeVo.getUserId());
                pel.setCreateTime(LocalDateTime.now());
                pel.setUpdateId(pelChangeVo.getUserId());
                pel.setUpdateTime(LocalDateTime.now());
                pelDao.save(pel);
                return ResponseResult.ok();
            case 2: //编辑图元
                //根据图元id查询图元数据
                Optional<PelEntity> optional = pelDao.findById(pelChangeVo.getId());
                if (optional.isPresent()) {
                    String dataName = optional.get().getDataName();
                    String dataPath = optional.get().getDataPath();
                    String fileName = optional.get().getFileName();
                    String filePath = optional.get().getFilePath();
                    pel = new PelEntity(pelChangeVo);
                    pel.setCreateId(optional.get().getCreateId());
                    pel.setCreateTime(optional.get().getCreateTime());
                    pel.setDataName(dataName);
                    pel.setDataPath(dataPath);
                    pel.setFileName(fileName);
                    pel.setFilePath(filePath);
                    if (file != null && !file.isEmpty()) {
                        pel.setFileName(file.getOriginalFilename());
                        if (LocalParamVo.FILE_TYPE) { //文件类型
                            if (StringUtil.isNotEmpty(filePath)) { //删除阿里云上面的图形
                                //校验有没有重复的图元路径
                                List<PelEntity> pelList = pelDao.findAll(Example.of(PelEntity.builder().filePath(filePath).build()))
                                        .stream().filter(p -> !Objects.equals(p.getId(), pelChangeVo.getId())).collect(Collectors.toList());
                                if (CollectionUtils.isEmpty(pelList)) {
                                    OssFileUtil.deleteFile(FileUtil.subString(filePath, FileUtil.SLASH, FileUtil.QUESTION));
                                }
                            }
                            pel.setFilePath(OssFileUtil.checkFile(file));
                        } else {
                            if (StringUtil.isNotEmpty(filePath)) { //删除本地服务器上面的图形
                                //校验有没有重复的图元路径
                                List<PelEntity> pelList = pelDao.findAll(Example.of(PelEntity.builder().filePath(filePath).build()))
                                        .stream().filter(p -> !Objects.equals(p.getId(), pelChangeVo.getId())).collect(Collectors.toList());
                                if (CollectionUtils.isEmpty(pelList)) {
                                    LocalFileUtil.deleteFile(optional.get().getFilePath());
                                }
                            }
                            pel.setFilePath(LocalFileUtil.checkFile(file));
                        }
                    }
                    if (dataFile != null && !dataFile.isEmpty()) { //数据文件
                        pel.setDataName(dataFile.getOriginalFilename());
                        if (LocalParamVo.FILE_TYPE) { //文件类型
                            if (StringUtil.isNotEmpty(dataPath)) { //删除阿里云上面的图形
                                //校验有没有重复的图元路径
                                List<PelEntity> pelList = pelDao.findAll(Example.of(PelEntity.builder().dataPath(dataPath).build()))
                                        .stream().filter(p -> !Objects.equals(p.getId(), pelChangeVo.getId())).collect(Collectors.toList());
                                if (CollectionUtils.isEmpty(pelList)) {
                                    OssFileUtil.deleteFile(FileUtil.subString(dataPath, FileUtil.SLASH, FileUtil.QUESTION));
                                }
                            }
                            pel.setDataPath(OssFileUtil.checkFile(dataFile));
                        } else {
                            if (StringUtil.isNotEmpty(dataPath)) { //删除本地服务器上面的图形
                                //校验有没有重复的图元路径
                                List<PelEntity> pelList = pelDao.findAll(Example.of(PelEntity.builder().dataPath(dataPath).build()))
                                        .stream().filter(p -> !Objects.equals(p.getId(), pelChangeVo.getId())).collect(Collectors.toList());
                                if (CollectionUtils.isEmpty(pelList)) {
                                    LocalFileUtil.deleteFile(dataPath);
                                }
                            }
                            pel.setDataPath(LocalFileUtil.checkFile(dataFile));
                        }
                    }
                    pel.setUpdateId(pelChangeVo.getUserId());
                    pelDao.save(pel);
                    return ResponseResult.ok();
                }
            case 3: //重命名
                //根据图元id查询图元数据
                Optional<PelEntity> nameOptional = pelDao.findById(pelChangeVo.getId());
                if (nameOptional.isPresent()) {
                    pel = nameOptional.get();
                    pel.setName(pelChangeVo.getName());
                    pel.setUpdateId(pelChangeVo.getUserId());
                    pelDao.save(pel);
                    return ResponseResult.ok();
                }
                break;
            case 4: //移动
                //根据图元id查询图元数据
                Optional<PelEntity> folderOptional = pelDao.findById(pelChangeVo.getId());
                if (folderOptional.isPresent()) {
                    pel = folderOptional.get();
                    pel.setParentId(pelChangeVo.getParentId());
                    pel.setUpdateId(pelChangeVo.getUserId());
                    pelDao.save(pel);
                    return ResponseResult.ok();
                }
                break;
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deletePelByIds(List<String> ids) {
        List<PelEntity> graphPelList = pelDao.findAllById(ids);
        if (CollectionUtils.isNotEmpty(graphPelList)) {
            pelDao.deleteAll(graphPelList);
            return ResponseResult.ok();
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<List<PelListDto>> queryPelList(String name) {
        //返回的集合
        List<PelListDto> resultList = Lists.newArrayList();
        //根据查询条件查询图元数据
        List<PelEntity> pelList = pelDao.findAll((Specification<PelEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtil.isNotEmpty(name)) {//关键词
                list.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
        if (CollectionUtils.isNotEmpty(pelList)) {
            resultList = pelList.stream().map(pel -> {
                PelListDto result = new PelListDto();
                BeanUtils.copyProperties(pel, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<PelDetailDto> findPelById(String id) {
        //返回的对象
        PelDetailDto result = new PelDetailDto();
        Optional<PelEntity> optional = pelDao.findById(id);
        if (optional.isPresent()) {
            PelEntity pelEntity = optional.get();
            BeanUtils.copyProperties(pelEntity, result);
            if (StringUtil.isNotEmpty(pelEntity.getDataPath())) { //绑定数据解析
                if (LocalParamVo.FILE_TYPE) { //文件类型
                    result.setDataData(OssFileUtil.parseFile(FileUtil.subString(pelEntity.getDataPath(), FileUtil.SLASH, FileUtil.QUESTION)));//文件数据
                } else {
                    result.setDataData(LocalFileUtil.parseFile(pelEntity.getDataPath()));//文件数据
                }
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<PelDetailDto>> findPelList() {
        //返回的集合
        List<PelDetailDto> resultList = Lists.newArrayList();
        //根据查询条件查询图元数据
        List<PelEntity> pelList = pelDao.findAll();
        if (CollectionUtils.isNotEmpty(pelList)) {
            resultList = pelList.stream().map(pel -> {
                PelDetailDto result = new PelDetailDto();
                BeanUtils.copyProperties(pel, result);
                if (StringUtil.isNotEmpty(pel.getDataPath())) { //绑定数据解析
                    if (LocalParamVo.FILE_TYPE) { //文件类型
                        result.setDataData(OssFileUtil.parseFile(FileUtil.subString(pel.getDataPath(), FileUtil.SLASH, FileUtil.QUESTION)));//文件数据
                    } else {
                        result.setDataData(LocalFileUtil.parseFile(pel.getDataPath()));//文件数据
                    }
                }
//                if (StringUtil.isNotEmpty(pel.getFilePath())) { //绑定数据解析
//                    if (LocalParamVo.FILE_TYPE) { //文件类型
//                        result.setFileData(OssFileUtil.parseFile(FileUtil.subString(pel.getFilePath(), FileUtil.SLASH, FileUtil.QUESTION)));//文件数据
//                    } else {
//                        result.setFileData(LocalFileUtil.parseFile(pel.getFilePath()));//文件数据
//                    }
//                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

}
