package com.sunmax.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.SiteSetUpDto;
import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.local.LocalImageUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.util.oss.OssImageUtil;
import com.sunmax.common.vo.LocalParamVo;

import com.sunmax.system.dao.AppletDao;
import com.sunmax.system.dao.OrganEmpowerDao;
import com.sunmax.system.dao.TenantManageDao;
import com.sunmax.system.dao.UserDao;
import com.sunmax.system.dto.AppletDetailDto;
import com.sunmax.system.dto.AppletListDto;
import com.sunmax.system.entity.AppletEntity;
import com.sunmax.system.entity.OrganEmpowerEntity;
import com.sunmax.system.entity.TenantInfoEntity;
import com.sunmax.system.entity.UserEntity;
import com.sunmax.system.service.AppletService;
import com.sunmax.system.service.feign.DeviceService;
import com.sunmax.system.vo.AppletChangeVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppletServiceImpl implements AppletService {

    @Autowired
    private AppletDao appletDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TenantManageDao tenantManageDao;

    @Autowired
    private OrganEmpowerDao organEmpowerDao;

    @Autowired
    private DeviceService deviceService;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveApplet(AppletChangeVo appletChangeVo, MultipartFile appletLogo, MultipartFile tencentImage) {
        //根据小程序id查询小程序数据
        List<AppletEntity> appletList = appletDao.findAll(Example.of(AppletEntity.builder().appletCode(appletChangeVo.getAppletCode()).build()));
        if (StringUtil.isEmpty(appletChangeVo.getId())) { //新增小程序数据
            if (CollectionUtils.isNotEmpty(appletList)) {
                return ResponseResult.paramShow(appletChangeVo.getAppletCode(), ResponseResult.PARAM_EXIST);
            }
            AppletEntity appletEntity = new AppletEntity();
            BeanUtils.copyProperties(appletChangeVo, appletEntity);
            appletEntity.setCreateId(appletChangeVo.getUserId());
            appletEntity.setUpdateId(appletChangeVo.getUserId());
            if (appletLogo != null && !appletLogo.isEmpty()) { //小程序logo
                if (LocalParamVo.FILE_TYPE) { //文件类型
                    appletEntity.setAppletLogo(OssImageUtil.checkImage(appletLogo));
                } else {
                    appletEntity.setAppletLogo(LocalImageUtil.checkImage(appletLogo));
                }
            }
            if (tencentImage != null && !tencentImage.isEmpty()) { //微信公众号二维码
                if (LocalParamVo.FILE_TYPE) { //文件类型
                    appletEntity.setTencentImage(OssImageUtil.checkImage(tencentImage));
                } else {
                    appletEntity.setTencentImage(LocalImageUtil.checkImage(tencentImage));
                }
            }
            appletDao.save(appletEntity);
            return ResponseResult.ok();
        } else {
            appletList = appletList.stream().filter(s -> !Objects.equals(s.getId(), appletChangeVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(appletList)) {
                return ResponseResult.paramShow(appletChangeVo.getAppletCode(), ResponseResult.PARAM_EXIST);
            }
            Optional<AppletEntity> optional = appletDao.findById(appletChangeVo.getId());
            if (optional.isPresent()) {
                AppletEntity appletEntity = new AppletEntity();
                BeanUtils.copyProperties(appletChangeVo, appletEntity);
//                appletEntity.setAppletSecret(optional.get().getAppletSecret());
//                appletEntity.setTencentSecret(optional.get().getTencentSecret());
                appletEntity.setCreateId(optional.get().getCreateId());
                appletEntity.setCreateTime(optional.get().getCreateTime());
                appletEntity.setUpdateId(appletChangeVo.getUserId());
                appletEntity.setAppletLogo(optional.get().getAppletLogo());
                appletEntity.setTencentImage(optional.get().getTencentImage());
                if (appletLogo != null && !appletLogo.isEmpty()) { //小程序logo
                    if (LocalParamVo.FILE_TYPE) { //文件类型
                        if (StringUtil.isNotEmpty(optional.get().getAppletLogo())) { //删除阿里云上面的小程序logo
                            OssImageUtil.deleteImage(FileUtil.subString(optional.get().getAppletLogo(), FileUtil.SLASH, FileUtil.QUESTION));
                        }
                        appletEntity.setAppletLogo(OssImageUtil.checkImage(appletLogo));
                    } else {
                        if (StringUtil.isNotEmpty(optional.get().getAppletLogo())) { //删除本地服务器上面的小程序logo
                            LocalImageUtil.deleteImage(optional.get().getAppletLogo());
                        }
                        appletEntity.setAppletLogo(LocalImageUtil.checkImage(appletLogo));
                    }
                }
                if (tencentImage != null && !tencentImage.isEmpty()) { //微信公众号二维码
                    if (LocalParamVo.FILE_TYPE) { //文件类型
                        if (StringUtil.isNotEmpty(optional.get().getTencentImage())) { //删除阿里云上面的微信公众号二维码
                            OssImageUtil.deleteImage(FileUtil.subString(optional.get().getTencentImage(), FileUtil.SLASH, FileUtil.QUESTION));
                        }
                        appletEntity.setTencentImage(OssImageUtil.checkImage(tencentImage));
                    } else {
                        if (StringUtil.isNotEmpty(optional.get().getTencentImage())) { //删除本地服务器上面的微信公众号二维码
                            LocalImageUtil.deleteImage(optional.get().getTencentImage());
                        }
                        appletEntity.setTencentImage(LocalImageUtil.checkImage(tencentImage));
                    }
                }
                appletDao.save(appletEntity);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<AppletListDto>> queryAppletList(String keyword) {
        //返回的集合
        List<AppletListDto> resultList = Lists.newArrayList();
        List<AppletEntity> appletList = appletDao.findAll((Specification<AppletEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtil.isNotEmpty(keyword)) { //关键字
                list.add(cb.or(cb.like(root.get("appletName"), "%" + keyword + "%"),
                        cb.like(root.get("appletCode"), "%" + keyword + "%")));//小程序编号和小程序名称
            }
            return cb.and(list.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(appletList)) {
            //根据多个用户id获取用户名称
            Set<String> userIds = appletList.stream().map(AppletEntity::getCreateId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet());
            userIds.addAll(appletList.stream().map(AppletEntity::getUpdateId).filter(StringUtil::isNotEmpty).collect(Collectors.toSet()));
            Map<String, String> userMap = userDao.findAllById(userIds).stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getFullName));
            //根据多个租户id查询租户名称
            Set<String> tenantIds = appletList.stream().filter(s -> StringUtil.isNotEmpty(s.getTenantIds()))
                    .map(s -> JSON.parseArray(s.getTenantIds(), String.class))
                    .flatMap(Collection::stream).collect(Collectors.toSet());
            Map<String, String> tenantMap = tenantManageDao.findAllById(tenantIds).stream().collect(Collectors.toMap(TenantInfoEntity::getId,
                    TenantInfoEntity::getTenantName));
            resultList = appletList.stream().map(applet -> {
                AppletListDto result = new AppletListDto();
                BeanUtils.copyProperties(applet, result);
                if (StringUtil.isNotEmpty(applet.getCreateId()) && userMap.containsKey(applet.getCreateId())) {
                    result.setCreateName(userMap.get(applet.getCreateId()));
                }
                if (StringUtil.isNotEmpty(applet.getUpdateId()) && userMap.containsKey(applet.getUpdateId())) {
                    result.setUpdateName(userMap.get(applet.getUpdateId()));
                }
                if (StringUtil.isNotEmpty(applet.getTenantIds())) {
                    result.setTenantNames(JSON.toJSONString(JSON.parseArray(applet.getTenantIds(), String.class).stream().map(tenantId -> {
                        if (tenantMap.containsKey(tenantId)) {
                            return tenantMap.get(tenantId);
                        }
                        return null;
                    }).collect(Collectors.toList())));
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<AppletDetailDto> findAppletDetailById(String id) {
        //返回的对象
        AppletDetailDto result = new AppletDetailDto();
        Optional<AppletEntity> optional = appletDao.findById(id);
        if (optional.isPresent()) {
            AppletEntity applet = optional.get();
            BeanUtils.copyProperties(applet, result);
            //获取创建人名称和修改人名称
            Set<String> userIds = Sets.newHashSet();
            if (StringUtil.isNotEmpty(applet.getCreateId())) {
                userIds.add(applet.getCreateId());
            }
            if (StringUtil.isNotEmpty(applet.getUpdateId())) {
                userIds.add(applet.getUpdateId());
            }
            Map<String, String> userMap = userDao.findAllById(userIds).stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getFullName));
            if (StringUtil.isNotEmpty(applet.getCreateId()) && userMap.containsKey(applet.getCreateId())) {
                result.setCreateName(userMap.get(applet.getCreateId()));
            }
            if (StringUtil.isNotEmpty(applet.getUpdateId()) && userMap.containsKey(applet.getUpdateId())) {
                result.setUpdateName(userMap.get(applet.getUpdateId()));
            }
            //获取多个租户名称
            if (StringUtil.isNotEmpty(applet.getTenantIds())) {
                List<String> tenantIds = JSON.parseArray(applet.getTenantIds(), String.class);
                Map<String, String> tenantMap = tenantManageDao.findAllById(tenantIds).stream().collect(Collectors.toMap(TenantInfoEntity::getId,
                        TenantInfoEntity::getTenantName));
                result.setTenantNames(JSON.toJSONString(tenantIds.stream().map(tenantId -> {
                    if (tenantMap.containsKey(tenantId)) {
                        return tenantMap.get(tenantId);
                    }
                    return null;
                }).collect(Collectors.toList())));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<AppletDto> findAppletById(String id) {
        //返回的对象
        AppletDto result = new AppletDto();
        Optional<AppletEntity> optional = appletDao.findById(id);
        return getAppletData(result, optional);
    }

    /**
     * 获取小程序数据
     *
     * @param result   结果
     * @param optional 小程序实体
     * @return 小程序数据
     */
    private ResponseResult<AppletDto> getAppletData(AppletDto result, Optional<AppletEntity> optional) {
        if (optional.isPresent()) {
            AppletEntity appletEntity = optional.get();
            BeanUtils.copyProperties(appletEntity, result);
            //根据多个租户id查询租户下面站点的id
            List<String> tenantIds;
            if (StringUtil.isNotEmpty(appletEntity.getTenantIds())) {
                tenantIds = JSON.parseArray(appletEntity.getTenantIds(), String.class).stream().filter(StringUtil::isNotEmpty)
                        .distinct().collect(Collectors.toList());
                tenantIds = tenantManageDao.findAllById(tenantIds).stream().filter(c -> c.getTenantState() == 1)
                        .map(TenantInfoEntity::getId).collect(Collectors.toList());
            } else { //没有则查询所有所有租户
                tenantIds = tenantManageDao.findAll().stream().filter(c -> c.getTenantState() == 1).map(TenantInfoEntity::getId).collect(Collectors.toList());
            }
            List<OrganEmpowerEntity> organEmpowerList = organEmpowerDao.findAllByTenantIdIn(tenantIds);
            if (CollectionUtils.isNotEmpty(organEmpowerList)) {
                //根据多个站点id查询站点设置数据
                List<String> siteIds = organEmpowerList.stream().map(OrganEmpowerEntity::getSiteId)
                        .filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                Map<String, SiteSetUpDto> siteSetUpMap = deviceService.findSiteSetUpBySiteIds(siteIds).getData();
                Map<String, Set<String>> tenantSiteIdMap = organEmpowerList.stream().filter(c -> {
                            if (StringUtil.isNotEmpty(c.getSiteId()) && siteSetUpMap.containsKey(c.getSiteId())) {
                                SiteSetUpDto siteSetUp = siteSetUpMap.get(c.getSiteId());
                                if (StringUtil.isEmpty(siteSetUp.getAppShow())) {
                                    return false;
                                }
                                return siteSetUp.getAppShow() == 1;
                            }
                            return false;
                        }).collect(Collectors.groupingBy(OrganEmpowerEntity::getTenantId, Collectors.mapping(OrganEmpowerEntity::getSiteId, Collectors.toSet())));
                result.setTenantSiteIdMap(tenantSiteIdMap);
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<AppletDto> findAppletByAppletCode(String appletCode) {
        //返回的对象
        AppletDto result = new AppletDto();
        Optional<AppletEntity> optional = appletDao.findOne(Example.of(AppletEntity.builder().appletCode(appletCode).build()));
        return getAppletData(result, optional);
    }

    /**
     * 根据小程序id和小程序类型查询小程序数据
     *
     * @param appletCode 小程序id
     * @param appletType 小程序类型 1-微信 2-支付宝
     * @return
     */
    @Override
    public ResponseResult<AppletDto> findByAppletCodeAndAppletType(String appletCode, Integer appletType) {
        //返回的对象
        AppletDto result = new AppletDto();
        Optional<AppletEntity> optional = appletDao.findOne(Example.of(AppletEntity.builder().appletCode(appletCode).appletType(appletType).build()));
        return getAppletData(result, optional);
    }

    @Override
    public ResponseResult<Map<String, AppletDto>> findAppletListByIds(Set<String> appletIds) {
        //返回的对象
        Map<String, AppletDto> resultMap = Maps.newHashMap();

        List<AppletEntity> appletList = appletDao.findAllById(appletIds);
        if (CollectionUtils.isNotEmpty(appletList)) {
            resultMap = appletList.stream().map(appletEntity -> {
                AppletDto result = new AppletDto();
                BeanUtils.copyProperties(appletEntity, result);
                return result;
            }).collect(Collectors.toMap(AppletDto::getId, a -> a));
        }
        return ResponseResult.ok(resultMap);
    }


}
