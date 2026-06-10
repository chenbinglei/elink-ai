package com.sunmax.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.TreeCommonDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.*;
import com.sunmax.common.dto.system.dynamic.HttpForwardDto;
import com.sunmax.common.dto.system.dynamic.MqttConfigDto;
import com.sunmax.common.dto.system.dynamic.MqttForwardDto;
import com.sunmax.common.entity.BaseTimeEntity;
import com.sunmax.common.enums.ProtocolEnum;
import com.sunmax.common.util.CommonUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.system.MqttClientVo;
import com.sunmax.system.dao.*;
import com.sunmax.system.dto.*;
import com.sunmax.system.entity.*;
import com.sunmax.system.service.ConfigureCenterService;
import com.sunmax.system.service.feign.*;
import com.sunmax.system.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.localDateTimeToStr;

@Slf4j
@Service
public class ConfigureCenterServiceImpl implements ConfigureCenterService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private ChargePlatformInfoDao chargePlatformInfoDao;

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private DataForwardDao dataForwardDao;

    @Autowired
    private DataConfigDao dataConfigDao;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CrontabService crontabService;

    @Autowired
    private OperatorInfoDao operatorInfoDao;

    @Autowired
    private ConfigureService configureService;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateProduct(ProductChangeVo productVo) {
        if (productVo != null) {
            //新增产品信息
            if (StringUtil.isEmpty(productVo.getId())) {
                //根据产品名称查询产品数据
                Optional<ProductEntity> optional = productDao.findOne(
                        Example.of(ProductEntity.builder().productName(productVo.getProductName()).isDelete(0).build()));
                if (optional.isPresent()) {
                    return ResponseResult.paramShow(productVo.getProductName(), ResponseResult.PARAM_EXIST);
                }

                ProductEntity product = new ProductEntity();
                product.setId(StringUtil.getUUID()); //产品id
                product.setProductName(productVo.getProductName()); //产品名称
                product.setClientId(productVo.getClientId());//客户端id
                product.setParentId("0");//根节点没有，存0
                product.setIsDelete(0);
                product.setCreateTime(LocalDateTime.now()); //创建时间
                product.setUpdateTime(LocalDateTime.now());//修改时间
                productDao.save(product);
            } else {
                //修改产品信息
                Optional<ProductEntity> optional = productDao.findById(productVo.getId());
                if (optional.isPresent()) {
                    ProductEntity product = optional.get();
                    product.setIsDelete(optional.get().getIsDelete());//伪删除状态
                    if (StringUtil.isNotEmpty(productVo.getProductName())) {
                        product.setProductName(productVo.getProductName()); //产品名称
                    }
                    if (StringUtil.isNotEmpty(productVo.getClientId())) {
                        product.setClientId(productVo.getClientId()); //客户端id
                    }
                    product.setUpdateTime(LocalDateTime.now());//修改时间
                    productDao.save(product);
                }

            }
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<List<ProductListDto>> queryProductList(String productId, String keyWords) {
        //返回的集合
        List<ProductListDto> resultList;

        //查询所有产品数据
        if (StringUtil.isEmpty(productId)) {
            resultList = productDao.findAll().stream().filter(p -> Objects.equals(0, p.getIsDelete())).map(ProductListDto::new).collect(Collectors.toList());
        } else {
            //查询该产品下面的模块数据
            resultList = productDao.findAll(Example.of(ProductEntity.builder().parentId(productId).build()))
                    .stream().filter(p -> Objects.equals(0, p.getIsDelete())).map(ProductListDto::new).collect(Collectors.toList());
        }
        //查询条件 关键词 产品名称
        if (StringUtil.isNotEmpty(keyWords)) {
            resultList = resultList.stream().filter(p -> p.getProductName().contains(keyWords)).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteModuleById(String id) {
        //根据模块id获取模块数据
        Optional<ProductEntity> optional = productDao.findById(id);
        if (optional.isPresent()) {
            ProductEntity product = optional.get();
            //根据产品id查询下面的子节点
            List<ProductEntity> productList = productDao.findAll(Example.of(ProductEntity.builder().isDelete(0).build()));

            //获取所有权限数据
            List<TreeCommonDto> childrenList = productList.stream().map(p -> {
                TreeCommonDto treeCommon = new TreeCommonDto();
                treeCommon.setId(p.getId());
                treeCommon.setName(p.getProductName());
                treeCommon.setParentId(p.getParentId());
                return treeCommon;
            }).collect(Collectors.toList());

            TreeCommonDto parent = new TreeCommonDto();
            parent.setId(id);
            parent.setName(product.getProductName());
            List<TreeCommonDto> updateList = new ArrayList<>(CommonUtil.setChild(parent, childrenList));
            updateList.add(parent);
            List<String> productIds = updateList.stream().map(TreeCommonDto::getId).collect(Collectors.toList());
            List<ProductEntity> productUpdateList = productDao.findAllById(productIds);
            //批量修改产品的状态
            productDao.saveAll(productUpdateList.stream().peek(p -> p.setIsDelete(1)).collect(Collectors.toList()));

            //根据模块id查询该模块下面的权限数据
            List<PermissionEntity> permissionList = permissionDao.findAllByModuleIdIn(productIds);
            if (!permissionList.isEmpty()) {
                permissionList = permissionList.stream().peek(p -> p.setIsDelete(1)).collect(Collectors.toList());
            }
            //批量更新该模块下面的权限数据状态
            permissionDao.saveAll(permissionList);
            return ResponseResult.ok(ResponseResult.SUCCESS);

        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdatePermission(PermissionChangeVo permissionVo) {
        if (permissionVo != null) {
            //新增权限信息
            if (StringUtil.isEmpty(permissionVo.getId())) {
                //根据权限编号查询权限数据
                Optional<PermissionEntity> optionalCode = permissionDao.findOne(Example.of(PermissionEntity.builder()
                        .moduleId(permissionVo.getModuleId()).permissionCode(permissionVo.getPermissionCode()).isDelete(0).build()));
                if (optionalCode.isPresent()) {
                    return ResponseResult.paramShow(permissionVo.getPermissionCode(), ResponseResult.PARAM_EXIST);
                }
//                //根据权限名称查询权限数据
//                Optional<PermissionEntity> optionalName = permissionDao.findOne(Example.of(PermissionEntity.builder()
//                        .permissionName(permissionVo.getPermissionName()).isDelete(0).build()));
//                if (optionalName.isPresent()) {
//                    return ResponseResult.paramShow(permissionVo.getPermissionName(), ResponseResult.PARAM_EXIST);
//                }

                PermissionEntity permission = new PermissionEntity(permissionVo);
                permission.setId(StringUtil.getUUID()); //权限id
                permission.setIsDelete(0);//正常数据
                permission.setCreateTime(LocalDateTime.now()); //创建时间
                permission.setUpdateTime(LocalDateTime.now());//修改时间
                permissionDao.save(permission);
            } else {
                //修改权限信息
                //根据权限id查询权限数据
                Optional<PermissionEntity> optional = permissionDao.findById(permissionVo.getId());
                if (optional.isPresent()) {
                    PermissionEntity permission = new PermissionEntity(permissionVo);
                    permission.setPermissionCode(optional.get().getPermissionCode());//权限编号
                    permission.setIsDelete(optional.get().getIsDelete());//是否删除
                    permission.setCreateTime(optional.get().getCreateTime()); //创建时间
                    permission.setUpdateTime(LocalDateTime.now());//修改时间
                    permissionDao.save(permission);
                }
            }
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<List<PermissionListDto>> findPermissionByModuleId(String moduleId) {
        //返回的集合
        List<PermissionListDto> resultList = new ArrayList<>();

        //根据模块id查询权限数据
        List<PermissionEntity> permissionList = permissionDao.findAll(
                Example.of(PermissionEntity.builder().moduleId(moduleId).build()));
        if (!permissionList.isEmpty()) {
            resultList = permissionList.stream().filter(p -> Objects.equals(0, p.getIsDelete())).map(PermissionListDto::new).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deletePermissionById(String id) {
        //根据权限id删除权限数据
        Optional<PermissionEntity> optional = permissionDao.findById(id);
        if (optional.isPresent()) {
            //修改的权限集合
            PermissionEntity permission = optional.get();

            //根据权限id查询下面的子节点
            List<PermissionEntity> permissionList = permissionDao.findAll(Example.of(PermissionEntity.builder().isDelete(0).build()));

            //获取所有权限数据
            List<TreeCommonDto> childrenList = permissionList.stream().map(p -> {
                TreeCommonDto treeCommon = new TreeCommonDto();
                treeCommon.setId(p.getId());
                treeCommon.setName(p.getPermissionName());
                treeCommon.setParentId(p.getParentId());
                return treeCommon;
            }).collect(Collectors.toList());

            TreeCommonDto parent = new TreeCommonDto();
            parent.setId(id);
            parent.setName(permission.getPermissionName());
            List<TreeCommonDto> updateList = new ArrayList<>(CommonUtil.setChild(parent, childrenList));
            updateList.add(parent);
            List<String> permissionIds = updateList.stream().map(TreeCommonDto::getId).collect(Collectors.toList());
            List<PermissionEntity> permissionUpdateList = permissionDao.findAllById(permissionIds);
            //批量修改权限的状态
            permissionDao.saveAll(permissionUpdateList.stream().peek(p -> p.setIsDelete(1)).collect(Collectors.toList()));
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdatePlatformInfo(ChargePlatformInfoVo chargePlatformInfoVo) {
        if (StringUtil.isNotEmpty(chargePlatformInfoVo)) {
            ChargePlatformInfoEntity chargePlatformInfoEntity = new ChargePlatformInfoEntity();
            //新增平台信息
            if (StringUtil.isEmpty(chargePlatformInfoVo.getId())) {
                //根据平台标识查询信息
                Optional<ChargePlatformInfoEntity> platformLogo = chargePlatformInfoDao.findOne(
                        Example.of(ChargePlatformInfoEntity.builder().platformLogo(chargePlatformInfoVo.getPlatformLogo()).build()));
                if (platformLogo.isPresent()) {
                    return ResponseResult.paramShow(chargePlatformInfoVo.getPlatformLogo(), ResponseResult.PARAM_EXIST);
                }
                //根据平台名称查询信息
                Optional<ChargePlatformInfoEntity> platformName = chargePlatformInfoDao.findOne(
                        Example.of(ChargePlatformInfoEntity.builder().platformName(chargePlatformInfoVo.getPlatformName()).build()));
                if (platformName.isPresent()) {
                    return ResponseResult.paramShow(chargePlatformInfoVo.getPlatformName(), ResponseResult.PARAM_EXIST);
                }
            }
            BeanUtils.copyProperties(chargePlatformInfoVo, chargePlatformInfoEntity);
            chargePlatformInfoDao.save(chargePlatformInfoEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 根据平台id删除平台信息
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deletePlatformInfoById(String id) {
        chargePlatformInfoDao.deleteById(id);
        //删除网关和平台关联关系并且重新下发
        togetherService.deleteAndGatewayPlatformSet(id);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 分页查询充电平台列表信息
     *
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult<?> findPlatformInfoListByPage(String keyword, Integer page, Integer size) {
        List<ChargePlatformInfoDto> resultList = Lists.newArrayList();
        //查询全部平台信息
        List<ChargePlatformInfoEntity> chargePlatformInfoEntities = chargePlatformInfoDao.findAll((Specification<ChargePlatformInfoEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtil.isNotEmpty(keyword)) {
                predicates.add(cb.or(cb.like(root.get("platformLogo"), "%" + keyword + "%"), cb.like(root.get("platformName"), "%" + keyword + "%")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(chargePlatformInfoEntities)) {
            resultList = chargePlatformInfoEntities.stream().map(chargePlatformInfoEntity -> {
                ChargePlatformInfoDto chargePlatformInfoDto = new ChargePlatformInfoDto();
                BeanUtils.copyProperties(chargePlatformInfoEntity, chargePlatformInfoDto);
                return chargePlatformInfoDto;
            }).collect(Collectors.toList());
        }
        if (size > 0) {
            return ResponseResult.ok(new PageDto<>(resultList, page, size));
        } else {
            return ResponseResult.ok(resultList);
        }
    }

    /**
     * 根据多个平台标识查询平台信息
     *
     * @param platformLogoList
     * @return
     */
    @Override
    public ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByLogos(List<String> platformLogoList) {
        Map<String, ChargePlatformInfoDto> resultMap = Maps.newHashMap();
        //根据多个平台标识查询数据
        List<ChargePlatformInfoEntity> chargePlatformInfoEntities = chargePlatformInfoDao.findAllByPlatformLogoIn(platformLogoList);
        if (CollectionUtils.isNotEmpty(chargePlatformInfoEntities)) {
            Map<String, ChargePlatformInfoEntity> chargePlatformInfoEntityMap = chargePlatformInfoEntities.stream().collect(Collectors.toMap(ChargePlatformInfoEntity::getPlatformLogo, chargePlatformInfoEntity -> chargePlatformInfoEntity, (k1, k2) -> k1));
            chargePlatformInfoEntityMap.forEach((k, v) -> {
                ChargePlatformInfoDto chargePlatformInfoDto = new ChargePlatformInfoDto();
                BeanUtils.copyProperties(v, chargePlatformInfoDto);
                resultMap.put(k, chargePlatformInfoDto);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据多个平台id查询平台信息
     *
     * @param platformIdList
     * @return
     */
    @Override
    public ResponseResult<Map<String, ChargePlatformInfoDto>> findChargePlatformInfoByIds(List<String> platformIdList) {
        Map<String, ChargePlatformInfoDto> resultMap = Maps.newHashMap();
        //根据多个平台标识查询数据
        List<ChargePlatformInfoEntity> chargePlatformInfoEntities = chargePlatformInfoDao.findAllById(platformIdList);
        if (CollectionUtils.isNotEmpty(chargePlatformInfoEntities)) {
            Map<String, ChargePlatformInfoEntity> chargePlatformInfoEntityMap = chargePlatformInfoEntities.stream().collect(Collectors.toMap(ChargePlatformInfoEntity::getId, chargePlatformInfoEntity -> chargePlatformInfoEntity, (k1, k2) -> k1));
            chargePlatformInfoEntityMap.forEach((k, v) -> {
                ChargePlatformInfoDto chargePlatformInfoDto = new ChargePlatformInfoDto();
                BeanUtils.copyProperties(v, chargePlatformInfoDto);
                resultMap.put(k, chargePlatformInfoDto);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<List<ProtocolListDto>> getProtocolList() {
        return ResponseResult.ok(Arrays.stream(ProtocolEnum.values()).map(protocolEnum -> {
            ProtocolListDto result = new ProtocolListDto();
            result.setCode(protocolEnum.getCode());
            result.setName(protocolEnum.getName());
            result.setType(protocolEnum.getType());
            return result;
        }).collect(Collectors.toList()));
    }

    @Override
    public ResponseResult<List<ProtocolFieldDto>> findProtocolFieldByCode(String code) {
        //返回的集合
        List<ProtocolFieldDto> resultList = Lists.newArrayList();
        if (Objects.equals(code, ProtocolEnum.HD.getCode())) {
            resultList.add(ProtocolFieldDto.builder().fieldCode("clientId").fieldName("客户端id").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("vendor").fieldName("厂商标识").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("gwSn").fieldName("网关编码").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("username").fieldName("用户名").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("password").fieldName("密码").required(true).build());
        }
        List<String> pciCode = Arrays.asList(ProtocolEnum.PROVINCE.getCode(), ProtocolEnum.CITY.getCode(), ProtocolEnum.INTERFLOW.getCode());
        if (pciCode.contains(code)) {
            resultList.add(ProtocolFieldDto.builder().fieldCode("platformId").fieldName("平台运营商ID").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("platformSecret").fieldName("平台运营商密钥").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("dataSecret").fieldName("数据消息密钥").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("dataSecretIv").fieldName("消息密钥初始化向量").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("sigSecret").fieldName("签名密钥").required(true).build());
        }
        if (Objects.equals(code, ProtocolEnum.PLATFORM.getCode())) {
            resultList.add(ProtocolFieldDto.builder().fieldCode("platformId").fieldName("平台运营商ID").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("dataSecret").fieldName("数据消息密钥").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("dataSecretIv").fieldName("消息密钥初始化向量").required(true).build());
        }
        if (Objects.equals(code, ProtocolEnum.STORAGE.getCode())) {
            resultList.add(ProtocolFieldDto.builder().fieldCode("appId").fieldName("应用ID").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("publicKey").fieldName("公钥").required(true).build());
            resultList.add(ProtocolFieldDto.builder().fieldCode("privateKey").fieldName("私钥").required(true).build());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveDataForward(DataForwardChangeVo dataForwardVo) {
        List<DataForwardEntity> dataForwardList = Lists.newArrayList();
        if (StringUtil.isEmpty(dataForwardVo.getAddress())) {
            return ResponseResult.paramError("地址不能为空");
        }
        if (Objects.equals(dataForwardVo.getProtocolType(), 1) && StringUtil.isNotEmpty(dataForwardVo.getDynamicFields())) {
            MqttForwardDto mqttForward = JSON.parseObject(dataForwardVo.getDynamicFields(), MqttForwardDto.class);
            if (StringUtil.isEmpty(mqttForward.getClientId())) {
                return ResponseResult.paramError("客户端id不能为空");
            }
            if (StringUtil.isEmpty(mqttForward.getVendor())) {
                return ResponseResult.paramError("厂商标识不能为空");
            }
            if (StringUtil.isEmpty(mqttForward.getGwSn())) {
                return ResponseResult.paramError("网关编码不能为空");
            }
            if (StringUtil.isEmpty(mqttForward.getUsername())) {
                return ResponseResult.paramError("用户名不能为空");
            }
            if (StringUtil.isEmpty(mqttForward.getPassword())) {
                return ResponseResult.paramError("密码不能为空");
            }
            //根据客户端id查询通道转发数据
            dataForwardList = dataForwardDao.findAll().stream().filter(f -> f.getProtocolType() == 1 && StringUtil.isNotEmpty(f.getDynamicFields()))
                    .filter(d -> Objects.equals(JSON.parseObject(d.getDynamicFields(), MqttForwardDto.class).getClientId(), mqttForward.getClientId()))
                    .collect(Collectors.toList());
        }
        if (StringUtil.isEmpty(dataForwardVo.getId())) {
            if (CollectionUtils.isNotEmpty(dataForwardList)) {
                return ResponseResult.paramError("客户端id已存在");
            }
            DataForwardEntity dataForward = new DataForwardEntity();
            BeanUtils.copyProperties(dataForwardVo, dataForward);
            //MQTT类型 创建MQTT客户端连接
            boolean result = true;
            if (dataForwardVo.getProtocolType() == 1) {
                if (dataForwardVo.getStatus() == 1) {
                    result = updateMqttClient(dataForward);
                }
            }
            if (result) {
                dataForwardDao.save(dataForward);
                return ResponseResult.ok();
            }
            return ResponseResult.paramError("创建数据转发失败");
        } else {
            Optional<DataForwardEntity> optional = dataForwardDao.findById(dataForwardVo.getId());
            if (optional.isPresent()) {
                DataForwardEntity dataForward = new DataForwardEntity();
                BeanUtils.copyProperties(dataForwardVo, dataForward);
                dataForward.setCreateTime(optional.get().getCreateTime());
                //修改mqtt通道主题
                boolean result = true;
                if (dataForward.getProtocolType() == 1 && !Objects.equals(dataForwardVo.getStatus(), optional.get().getStatus())) {
                    result = this.updateMqttClient(dataForward);
                }
                //修改http缓存配置
                if (dataForward.getProtocolType() != 1) {
                    result = this.updateHttpCache(dataForward, null);
                }
                if (result) {
                    dataForwardDao.save(dataForward);
                    return ResponseResult.ok();
                }
                return ResponseResult.paramError("修改数据转发失败");
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<PageDto<DataForwardListDto>> queryDataForwardList(String keyword, Integer page, Integer size) {
        //返回的集合
        List<DataForwardListDto> resultList = Lists.newArrayList();
        //查询数据转发数据
        List<DataForwardEntity> dataForwardList = dataForwardDao.findAll((Specification<DataForwardEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtil.isNotEmpty(keyword)) {
                predicates.add(cb.or(cb.like(root.get("channelName"), "%" + keyword + "%"),
                        cb.like(root.get("protocolCode"), "%" + keyword + "%")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(dataForwardList)) {
            resultList = dataForwardList.stream().map(dataForward -> {
                DataForwardListDto result = new DataForwardListDto();
                BeanUtils.copyProperties(dataForward, result);
                if (StringUtil.isNotEmpty(dataForward.getProtocolCode())) {
                    result.setProtocolName(Objects.requireNonNull(ProtocolEnum.getByCode(dataForward.getProtocolCode())).getName());
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList, page, size));
    }

    @Override
    public ResponseResult<DataForwardDetailDto> findDataForwardById(String id) {
        //返回的对象
        DataForwardDetailDto result = new DataForwardDetailDto();
        //根据主键id查询数据转发数据
        Optional<DataForwardEntity> optional = dataForwardDao.findById(id);
        if (optional.isPresent()) {
            DataForwardEntity dataForward = optional.get();
            BeanUtils.copyProperties(dataForward, result);
        }
        return ResponseResult.ok(result);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> updateForwardStatus(String id, Integer status) {
        //根据主键id查询数据转发数据
        Optional<DataForwardEntity> optional = dataForwardDao.findById(id);
        if (optional.isPresent()) {
            DataForwardEntity dataForward = optional.get();
            dataForward.setStatus(status);
            Boolean result;
            if (dataForward.getProtocolType() == 1) {
                //修改客户端连接
                result = this.updateMqttClient(dataForward);
            } else {
                //修改http站点缓存配置
                result = this.updateHttpCache(dataForward, null);
            }
            if (result) {
                dataForwardDao.save(dataForward);
                return ResponseResult.ok();
            }
            return ResponseResult.paramError("修改数据转发状态失败");
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteDataForwardById(String id) {
        Optional<DataForwardEntity> optional = dataForwardDao.findById(id);
        if (optional.isPresent()) {
            DataForwardEntity dataForward = optional.get();
            dataForward.setStatus(2);
            Boolean result;
            if (dataForward.getProtocolType() == 1) {
                //关闭mqtt客户端连接
                result = this.updateMqttClient(dataForward);
            } else {
                //删除http站点缓存配置
                result = this.updateHttpCache(dataForward, null);
            }
            if (result) {
                //删除数据转发
                dataForwardDao.delete(dataForward);
                //删除数据配置
                dataConfigDao.deleteAll(dataConfigDao.findAll(Example.of(DataConfigEntity.builder().forwardId(id).build())));
                return ResponseResult.ok();
            }
            return ResponseResult.paramError("删除数据转发失败");
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<DataConfigListDto>> findDataConfigByForwardId(String forwardId) {
        //返回的集合
        List<DataConfigListDto> resultList = Lists.newArrayList();
        List<DataConfigEntity> dataConfigList = dataConfigDao.findAll(Example.of(DataConfigEntity.builder().forwardId(forwardId).build()));
        if (CollectionUtils.isNotEmpty(dataConfigList)) {
            //根据多个站点id查询站点名称
            List<String> siteIds = dataConfigList.stream().map(DataConfigEntity::getSiteId).distinct().collect(Collectors.toList());
            Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
            resultList = dataConfigList.stream().map(dataConfig -> {
                DataConfigListDto result = new DataConfigListDto();
                BeanUtils.copyProperties(dataConfig, result);
                if (siteInfoMap.containsKey(dataConfig.getSiteId())) {
                    result.setSiteName(siteInfoMap.get(dataConfig.getSiteId()).getSiteName());
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> batchUpdateDataConfig(String forwardId, List<DataConfigChangeVo> dataConfigVos) {
        //根据数据转发id查询数据配置
        Optional<DataForwardEntity> optional = dataForwardDao.findById(forwardId);
        if (optional.isPresent()) {
            DataForwardEntity dataForward = optional.get();
            //根据转发id查询数据配置
            List<DataConfigEntity> deleteDataConfigList = dataConfigDao.findAll(Example.of(DataConfigEntity.builder().forwardId(forwardId).build()));
            if (CollectionUtils.isNotEmpty(deleteDataConfigList)) {
                //删除该数据转发配置的数据
                dataConfigDao.deleteAll(deleteDataConfigList);
            }

            //把新的数据配置插入数据库
            //根据多个站点id和协议编码查询数据配置
            String protocolCode = dataForward.getProtocolCode();
            List<String> siteIds = dataConfigVos.stream().map(DataConfigChangeVo::getSiteId).distinct().collect(Collectors.toList());
            Map<String, DataConfigEntity> dataConfigMap = dataConfigDao.findAllByProtocolCodeAndSiteIdIn(protocolCode, siteIds).stream()
                    .collect(Collectors.toMap(DataConfigEntity::getSiteId, Function.identity(), (k1, k2) -> k1));

            List<DataConfigEntity> dataConfigList = dataConfigVos.stream().map(dataConfigVo -> {
                DataConfigEntity dataConfig = new DataConfigEntity();
                BeanUtils.copyProperties(dataConfigVo, dataConfig);
                dataConfig.setForwardId(forwardId);
                dataConfig.setProtocolCode(protocolCode);
                if (dataConfigMap.containsKey(dataConfigVo.getSiteId())) {
                    dataConfig.setId(dataConfigMap.get(dataConfigVo.getSiteId()).getId());
                }
                return dataConfig;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dataConfigList)) {
                Boolean result = true;
                if (dataForward.getProtocolType() != 1) {
                    //修改HTTP站点缓存配置
                    result = this.updateHttpCache(dataForward, dataConfigList);
                }
                if (result) {
                    dataConfigDao.saveAll(dataConfigList);
                }
            }
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<DataForwardDto>> getDataForwardList(Integer protocolType, Integer status) {
        //返回的集合
        List<DataForwardDto> resultList = Lists.newArrayList();
        //获取数据转发数据
        List<DataForwardEntity> dataForwardList;
        if (StringUtil.isNotEmpty(status)) {
            dataForwardList = dataForwardDao.findAll(Example.of(DataForwardEntity.builder().protocolType(protocolType).status(status).build()));
        } else {
            dataForwardList = dataForwardDao.findAll();
        }
        if (CollectionUtils.isNotEmpty(dataForwardList)) {
            //根据多个数据转发id查询数据配置
            List<String> forwardIds = dataForwardList.stream().map(BaseTimeEntity::getId).collect(Collectors.toList());
            Map<String, List<DataConfigEntity>> dataConfigMap = dataConfigDao.findAllByForwardIdIn(forwardIds)
                    .stream().collect(Collectors.groupingBy(DataConfigEntity::getForwardId));

            resultList = dataForwardList.stream().filter(s -> StringUtil.isNotEmpty(s.getDynamicFields())).map(dataForward -> {
                DataForwardDto result = new DataForwardDto();
                BeanUtils.copyProperties(dataForward, result);
                //根据数据转发id查询数据配置
                if (dataConfigMap.containsKey(dataForward.getId())) {
                    result.setDataConfigList(dataConfigMap.get(dataForward.getId()).stream().map(dataConfig -> {
                        DataConfigDto dataConfigDto = new DataConfigDto();
                        BeanUtils.copyProperties(dataConfig, dataConfigDto);
                        if (StringUtil.isNotEmpty(dataConfig.getDynamicConfigs())) {
                            dataConfigDto.setDynamicMqttConfigs(JSON.parseObject(dataConfig.getDynamicConfigs(), MqttConfigDto.class));
                        }
                        return dataConfigDto;
                    }).collect(Collectors.toList()));
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateOperatorInfo(OperatorInfoVo operatorInfoVo) {
        if (StringUtil.isNotEmpty(operatorInfoVo)) {
            //存储运营商信息
            OperatorInfoEntity operatorInfoEntity = new OperatorInfoEntity();
            String id = operatorInfoVo.getId();
            //查询运营商id字段是否有重复
            Optional<OperatorInfoEntity> operatorInfo = operatorInfoDao.findOne(Example.of(OperatorInfoEntity.builder().operatorId(operatorInfoVo.getOperatorId()).build()));
            if (operatorInfo.isPresent()) {
                //新增运营商
                if (StringUtil.isEmpty(operatorInfoVo.getId())) {
                    return ResponseResult.paramShow(operatorInfoVo.getOperatorId(), ResponseResult.PARAM_EXIST);
                } else {
                    //如果和查询到的id不重复，则说明名称重复
                    if (!operatorInfoVo.getId().equals(operatorInfo.get().getId())) {
                        return ResponseResult.paramShow(operatorInfoVo.getOperatorId(), ResponseResult.PARAM_EXIST);
                    }
                }
            }
            //新增
            if (StringUtil.isEmpty(id)) {
                operatorInfoEntity = new OperatorInfoEntity(operatorInfoVo);
                LocalDateTime localDateTime = LocalDateTime.now();
                operatorInfoEntity.setCreateTime(localDateTime);
                operatorInfoEntity.setUpdateTime(localDateTime);
            } else {
                Optional<OperatorInfoEntity> operatorDaoById = operatorInfoDao.findById(id);
                if (operatorDaoById.isPresent()) {
                    operatorInfoEntity = new OperatorInfoEntity(operatorInfoVo);
                    operatorInfoEntity.setCreateTime(operatorDaoById.get().getCreateTime());
                    operatorInfoEntity.setUpdateTime(LocalDateTime.now());
                }
            }
            operatorInfoDao.save(operatorInfoEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.ok(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<OperatorInfoDto> findOperatorDetailsById(String operatorId) {
        OperatorInfoDto operatorInfoDto = new OperatorInfoDto();
        Optional<OperatorInfoEntity> optional = operatorInfoDao.findById(operatorId);
        if (optional.isPresent()) {
            OperatorInfoEntity operatorInfoEntity = optional.get();
            BeanUtils.copyProperties(operatorInfoEntity, operatorInfoDto);
            if (StringUtil.isNotEmpty(operatorInfoEntity.getCreateTime())) {
                operatorInfoDto.setCreateTime(localDateTimeToStr(operatorInfoEntity.getCreateTime()));
            }
            if (StringUtil.isNotEmpty(operatorInfoEntity.getUpdateTime())) {
                operatorInfoDto.setUpdateTime(localDateTimeToStr(operatorInfoEntity.getUpdateTime()));
            }
        }
        return ResponseResult.ok(operatorInfoDto);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteOperatorInfoById(String operatorId) {
        operatorInfoDao.deleteById(operatorId);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    @Override
    public ResponseResult<PageDto<OperatorInfoDto>> findOperatorInfoByPage(OperatorListQueryVo operatorListQueryVo) {
        List<OperatorInfoDto> operatorInfoDtoList = Lists.newArrayList();
        List<OperatorInfoEntity> operatorInfoEntityList = operatorInfoDao.findAll(
                (Specification<OperatorInfoEntity>) (root, cq, cb) -> {
                    List<Predicate> list = new ArrayList<>();
                    //运营商名称
                    if (StringUtil.isNotEmpty(operatorListQueryVo.getOperatorName())) {
                        list.add(cb.like(root.get("operatorName"), "%" + operatorListQueryVo.getOperatorName() + "%"));
                    }
                    return cb.and(list.toArray(new Predicate[0]));
                });
        if (CollectionUtils.isNotEmpty(operatorInfoEntityList)) {
            operatorInfoDtoList = operatorInfoEntityList.stream().map(operatorInfoEntity -> {
                OperatorInfoDto operatorInfoDto = new OperatorInfoDto();
                BeanUtils.copyProperties(operatorInfoEntity, operatorInfoDto);
                if (StringUtil.isNotEmpty(operatorInfoEntity.getCreateTime())) {
                    operatorInfoDto.setCreateTime(localDateTimeToStr(operatorInfoEntity.getCreateTime()));
                }
                if (StringUtil.isNotEmpty(operatorInfoEntity.getUpdateTime())) {
                    operatorInfoDto.setUpdateTime(localDateTimeToStr(operatorInfoEntity.getUpdateTime()));
                }
                return operatorInfoDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(operatorInfoDtoList, operatorListQueryVo.getPage(), operatorListQueryVo.getSize()));
    }

    @Override
    public ResponseResult<List<OperatorInfoDto>> findAllOperatorInfoList() {
        List<OperatorInfoDto> operatorInfoDtoList = Lists.newArrayList();
        List<OperatorInfoEntity> operatorDaoAll = operatorInfoDao.findAll();
        if (CollectionUtils.isNotEmpty(operatorDaoAll)) {
            operatorInfoDtoList = operatorDaoAll.stream().map(operatorInfoEntity -> {
                OperatorInfoDto operatorInfoDto = new OperatorInfoDto();
                BeanUtils.copyProperties(operatorInfoEntity, operatorInfoDto);
                if (StringUtil.isNotEmpty(operatorInfoEntity.getCreateTime())) {
                    operatorInfoDto.setCreateTime(localDateTimeToStr(operatorInfoEntity.getCreateTime()));
                }
                if (StringUtil.isNotEmpty(operatorInfoEntity.getUpdateTime())) {
                    operatorInfoDto.setUpdateTime(localDateTimeToStr(operatorInfoEntity.getUpdateTime()));
                }
                return operatorInfoDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(operatorInfoDtoList);
    }

    @Override
    public ResponseResult<DataForwardDto> findDataForwardByClientId(String clientId) {
        //返回的对象
        DataForwardDto result = new DataForwardDto();
        //根据客户端id查询数据转发数据
        List<DataForwardEntity> dataForwardList = dataForwardDao.findAll(Example.of(DataForwardEntity.builder().protocolType(1).build()))
                .stream().filter(f -> StringUtil.isNotEmpty(f.getDynamicFields()))
                .filter(d -> Objects.equals(JSON.parseObject(d.getDynamicFields(), MqttForwardDto.class).getClientId(), clientId))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(dataForwardList)) {
            DataForwardEntity dataForward = dataForwardList.get(0);
            BeanUtils.copyProperties(dataForward, result);
            Map<String, List<DataConfigEntity>> dataConfigMap = dataConfigDao.findAllByForwardIdIn(Collections.singletonList(dataForward.getId()))
                    .stream().collect(Collectors.groupingBy(DataConfigEntity::getForwardId));
            //根据数据转发id查询数据配置
            if (dataConfigMap.containsKey(dataForward.getId())) {
                result.setDataConfigList(dataConfigMap.get(dataForward.getId()).stream().map(dataConfig -> {
                    DataConfigDto dataConfigDto = new DataConfigDto();
                    BeanUtils.copyProperties(dataConfig, dataConfigDto);
                    if (StringUtil.isNotEmpty(dataConfig.getDynamicConfigs())) {
                        dataConfigDto.setDynamicMqttConfigs(JSON.parseObject(dataConfig.getDynamicConfigs(), MqttConfigDto.class));
                    }
                    return dataConfigDto;
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(result);
    }

    //修改mqtt客户端连接
    private Boolean updateMqttClient(DataForwardEntity dataForward) {
        if (dataForward.getProtocolType() == 1 && dataForward.getStatus() == 1 && StringUtil.isNotEmpty(dataForward.getDynamicFields())) {
            MqttForwardDto mqttForward = JSON.parseObject(dataForward.getDynamicFields(), MqttForwardDto.class);
            return crontabService.createMqttClient(MqttClientVo.builder()
                    .protocolCode(dataForward.getProtocolCode())
                    .clientId(mqttForward.getClientId())
                    .vendor(mqttForward.getVendor())
                    .gwSn(mqttForward.getGwSn())
                    .address(dataForward.getAddress())
                    .username(mqttForward.getUsername())
                    .password(mqttForward.getPassword()).build()).isSuccess();
        }
        if (dataForward.getProtocolType() == 1 && dataForward.getStatus() == 2 && StringUtil.isNotEmpty(dataForward.getDynamicFields())) {
            MqttForwardDto mqttForward = JSON.parseObject(dataForward.getDynamicFields(), MqttForwardDto.class);
            return crontabService.deleteMqttClient(mqttForward.getClientId()).isSuccess();
        }
        return false;
    }

    //更新HTTP接入缓存
    private Boolean updateHttpCache(DataForwardEntity dataForward, List<DataConfigEntity> dataConfigList) {
        String protocolCode = dataForward.getProtocolCode();
        if (StringUtil.isEmpty(protocolCode)) {
            return false;
        }
        //站点及运营商相关信息
        if (CollectionUtils.isEmpty(dataConfigList)) {
            dataConfigList = dataConfigDao.findAllByForwardIdIn(Collections.singletonList(dataForward.getId()));
        }
        if (Objects.equals(protocolCode, ProtocolEnum.PROVINCE.getCode()) || Objects.equals(protocolCode, ProtocolEnum.CITY.getCode())) {
            return this.updateProvinceAndCityCache(dataForward, dataConfigList);
        }
        if (Objects.equals(protocolCode, ProtocolEnum.PLATFORM.getCode())) {
            return this.updateHttpPlatformCache(dataForward, dataConfigList);
        }
        return false;
    }

    //更新HTTP省市平台接入缓存
    private Boolean updateProvinceAndCityCache(DataForwardEntity dataForward, List<DataConfigEntity> dataConfigList) {
        //返回的对象
        PlatformDataForwardDto result = new PlatformDataForwardDto();
        HttpForwardDto httpForward = JSON.parseObject(dataForward.getDynamicFields(), HttpForwardDto.class);
        BeanUtils.copyProperties(dataForward, result);
        //平台运营商相关信息
        result.setPlatformId(httpForward.getPlatformId());
        result.setPlatformSecret(httpForward.getPlatformSecret());
        result.setDataSecret(httpForward.getDataSecret());
        result.setDataSecretIv(httpForward.getDataSecretIv());
        result.setSigSecret(httpForward.getSigSecret());
        //站点及运营商相关信息
        List<SiteOperateDto> siteOperateList = dataConfigList.stream().filter(d -> StringUtil.isNotEmpty(d.getDynamicConfigs())
                && StringUtil.isNotEmpty(JSON.parseObject(d.getDynamicConfigs()).getString("operatorId"))).map(d -> {
            SiteOperateDto siteOperate = new SiteOperateDto();
            siteOperate.setSiteId(d.getSiteId());
            siteOperate.setOperateId(JSON.parseObject(d.getDynamicConfigs()).getString("operatorId"));
            return siteOperate;
        }).collect(Collectors.toList());
        result.setSiteOperateList(siteOperateList);
        //根据多个运营商id查询运营商信息
        List<String> operateIds = siteOperateList.stream().map(SiteOperateDto::getOperateId).distinct().collect(Collectors.toList());
        result.setOperatorInfoList(operatorInfoDao.findAllByOperatorIdIn(operateIds).stream().map(o -> {
            OperatorInfoDto operatorInfoDto = new OperatorInfoDto();
            BeanUtils.copyProperties(o, operatorInfoDto);
            return operatorInfoDto;
        }).collect(Collectors.toList()));
        return configureService.updateHttpSiteForward(result).getData();
    }

    //更新HTTP平台V2G放电控制协议接入缓存
    private Boolean updateHttpPlatformCache(DataForwardEntity dataForward, List<DataConfigEntity> dataConfigList) {
        //暂时返回true 待定5分钟
        return true;
    }

}
