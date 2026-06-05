package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.CommonUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.ReaFieldParamVo;
import com.sunmax.together.dao.asset.ModuleLibraryDao;
import com.sunmax.together.dao.asset.SeriesConfigDao;
import com.sunmax.together.dto.operation.seriesInfo.ModuleLibraryDto;
import com.sunmax.together.dto.operation.seriesInfo.SeriesConfigInfoDto;
import com.sunmax.together.dto.operation.seriesInfo.SeriesDeviceListDto;
import com.sunmax.together.entity.assets.ModuleLibraryEntity;
import com.sunmax.together.entity.assets.SeriesConfigEntity;
import com.sunmax.together.service.operation.SeriesInfoService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.vo.operation.seriesInfo.ModuleLibraryChangeVo;
import com.sunmax.together.vo.operation.seriesInfo.ModuleLibraryQueryVo;
import com.sunmax.together.vo.operation.seriesInfo.SeriesConfigChangeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.localDateTimeToStr;

@Slf4j
@Service
public class SeriesInfoServiceImpl implements SeriesInfoService {

    @Autowired
    private ModuleLibraryDao moduleLibraryDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private SeriesConfigDao seriesConfigDao;

    @Autowired
    private DeviceService deviceService;

    /**
     * 新增或编辑组件库信息
     *
     * @param moduleChangeVo
     * @param userId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateModuleLibrary(ModuleLibraryChangeVo moduleChangeVo, String userId) {

        if (StringUtil.isNotEmpty(moduleChangeVo)) {
            //根据组件厂家和型号查询组件库数据
            List<ModuleLibraryEntity> moduleLibraryList = moduleLibraryDao.findAll(Example.of(ModuleLibraryEntity.builder()
                    .moduleFactory(moduleChangeVo.getModuleFactory()).moduleModel(moduleChangeVo.getModuleModel()).build()));

            ModuleLibraryEntity moduleLibraryEntity = new ModuleLibraryEntity();
            //新增组件库信息
            if (StringUtil.isEmpty(moduleChangeVo.getId())) {
                //校验组件厂家和组件型号是否重复
                if (CollectionUtils.isNotEmpty(moduleLibraryList)) {
                    return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
                }
                BeanUtils.copyProperties(moduleChangeVo, moduleLibraryEntity);
                moduleLibraryEntity.setCreateTime(LocalDateTime.now());
                moduleLibraryEntity.setCreateId(userId);
            } else {
                //校验组件厂家和组件型号是否重复
                moduleLibraryList = moduleLibraryList.stream().filter(m -> !Objects.equals(m.getId(), moduleChangeVo.getId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(moduleLibraryList)) {
                    return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
                }
                Optional<ModuleLibraryEntity> optional = moduleLibraryDao.findById(moduleChangeVo.getId());
                if (optional.isPresent()) {
                    ModuleLibraryEntity moduleLibrary = optional.get();
                    BeanUtils.copyProperties(moduleChangeVo, moduleLibraryEntity);
                    moduleLibraryEntity.setUpdateTime(LocalDateTime.now());
                    moduleLibraryEntity.setCreateTime(moduleLibrary.getCreateTime());
                    moduleLibraryEntity.setCreateId(moduleLibrary.getCreateId());
                }
            }
            moduleLibraryDao.save(moduleLibraryEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> batchDeleteModuleLibrary(String ids) {
        moduleLibraryDao.deleteAll(moduleLibraryDao.findAllById(JSON.parseArray(ids, String.class)));
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    @Override
    public ResponseResult<?> findModuleLibraryList(ModuleLibraryQueryVo moduleLibraryQueryVo) {
        List<ModuleLibraryDto> resultList = Lists.newArrayList();

        List<ModuleLibraryEntity> moduleLibraryEntityList = moduleLibraryDao.findAll((Specification<ModuleLibraryEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtil.isNotEmpty(moduleLibraryQueryVo.getModuleFactory())) { //组件厂家
                list.add(cb.like(root.get("moduleFactory"), "%" + moduleLibraryQueryVo.getModuleFactory() + "%"));
            }
            if (StringUtil.isNotEmpty(moduleLibraryQueryVo.getModuleModel())) { //组件型号
                list.add(cb.like(root.get("moduleModel"), "%" + moduleLibraryQueryVo.getModuleModel() + "%"));
            }
            //根据组件类型查询
            if (StringUtil.isNotEmpty(moduleLibraryQueryVo.getModuleType())) {
                list.add(cb.equal(root.get("moduleType"), moduleLibraryQueryVo.getModuleType()));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
        if (CollectionUtils.isNotEmpty(moduleLibraryEntityList)) {
            //根据多个创建者id查询用户信息
            Map<String, UserDto> userDtoMap = systemService.findUserInfoByIdsFeign(moduleLibraryEntityList.stream().map(ModuleLibraryEntity::getCreateId).distinct().collect(Collectors.toList())).getData();

            resultList = moduleLibraryEntityList.stream().map(moduleLibraryEntity -> {
                ModuleLibraryDto moduleLibraryDto = new ModuleLibraryDto();
                BeanUtils.copyProperties(moduleLibraryEntity, moduleLibraryDto);
                moduleLibraryDto.setCreateTime(localDateTimeToStr(moduleLibraryEntity.getCreateTime()));
                if (StringUtil.isNotEmpty(moduleLibraryEntity.getUpdateTime())) {
                    moduleLibraryDto.setUpdateTime(localDateTimeToStr(moduleLibraryEntity.getUpdateTime()));
                }
                if (MapUtils.isNotEmpty(userDtoMap) && userDtoMap.containsKey(moduleLibraryEntity.getCreateId())) {
                    moduleLibraryDto.setCreateName(userDtoMap.get(moduleLibraryEntity.getCreateId()).getFullName());
                }
                return moduleLibraryDto;
            }).collect(Collectors.toList());
        }
        //根据条数判断是否返回分页
        if (StringUtil.isNotEmpty(moduleLibraryQueryVo.getSize()) && moduleLibraryQueryVo.getSize() > 0) {
            return ResponseResult.ok(new PageDto<>(resultList, moduleLibraryQueryVo.getPage(), moduleLibraryQueryVo.getSize()));
        } else {
            return ResponseResult.ok(resultList);
        }
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> importModuleLibraryList(String userId, MultipartFile file) {
        List<ModuleLibraryEntity> moduleLibraryEntityList = Lists.newArrayList();

        //校验文件名是否存在
        String filename = file.getOriginalFilename();
        if (StringUtil.isEmpty(filename)) {
            return ResponseResult.paramError("文件名称为空");
        }
        //校验文件名称格式
        if (!filename.endsWith("xls") && !filename.endsWith("xlsx")) {
            return ResponseResult.paramError("文件不是Excel文件");
        }

        //解析文件内容
        try {
            //获取第一个shell
            Sheet sheet = WorkbookFactory.create(file.getInputStream()).getSheetAt(0);
            //获取Excel的行数
            int totalRows = sheet.getPhysicalNumberOfRows();
            // 得到Excel的列数(前提是有行数)
            if (totalRows <= 1 && StringUtil.isEmpty(sheet.getRow(0))) {
                return ResponseResult.paramError("没有解析到文件中的数据");
            }
            for (int i = 2; i < totalRows; i++) {
                //读取左上端单元格
                Row row = sheet.getRow(i);
                ModuleLibraryEntity moduleLibraryEntity = new ModuleLibraryEntity();
                //组件最大功率(Pmax)(W)
                String maxPower = CommonUtil.getCellValue(row.getCell(0));
                if (StringUtil.isEmpty(maxPower) || !StringUtil.isNumericAll(maxPower)) {
                    continue;
                }
                moduleLibraryEntity.setMaxPower(Double.parseDouble(maxPower));
                //组件最佳工作电压(Vmp) (V)
                String bestWorkVoltage = CommonUtil.getCellValue(row.getCell(1));
                if (StringUtil.isEmpty(bestWorkVoltage) || !StringUtil.isNumericAll(bestWorkVoltage)) {
                    continue;
                }
                moduleLibraryEntity.setBestWorkVoltage(Double.parseDouble(bestWorkVoltage));
                //组件最佳工作电流(Imp) (A)
                String bestWorkCurrent = CommonUtil.getCellValue(row.getCell(2));
                if (StringUtil.isEmpty(bestWorkCurrent) || !StringUtil.isNumericAll(bestWorkCurrent)) {
                    continue;
                }
                moduleLibraryEntity.setBestWorkCurrent(Double.parseDouble(bestWorkCurrent));
                //组件开路电压(Voc)(V)
                String openVoltage = CommonUtil.getCellValue(row.getCell(3));
                if (StringUtil.isEmpty(openVoltage) || !StringUtil.isNumericAll(openVoltage)) {
                    continue;
                }
                moduleLibraryEntity.setOpenVoltage(Double.parseDouble(openVoltage));
                //组件短路电流(Isc)(A)
                String shortCurrent = CommonUtil.getCellValue(row.getCell(4));
                if (StringUtil.isEmpty(shortCurrent) || !StringUtil.isNumericAll(shortCurrent)) {
                    continue;
                }
                moduleLibraryEntity.setShortCurrent(Double.parseDouble(shortCurrent));
                //最大功率(Pmax)的温度系数 (%/℃)
                String maxPowerTempCoeff = CommonUtil.getCellValue(row.getCell(5));
                if (StringUtil.isEmpty(maxPowerTempCoeff) || !StringUtil.isNumericAll(maxPowerTempCoeff)) {
                    continue;
                }
                moduleLibraryEntity.setMaxPowerTempCoeff(Double.parseDouble(maxPowerTempCoeff));
                //开路电压(Voc)的温度系数 (%/℃)
                String openVoltTempCoeff = CommonUtil.getCellValue(row.getCell(6));
                if (StringUtil.isEmpty(openVoltTempCoeff) || !StringUtil.isNumericAll(openVoltTempCoeff)) {
                    continue;
                }
                moduleLibraryEntity.setOpenVoltTempCoeff(Double.parseDouble(openVoltTempCoeff));
                //短路电流(Isc)的温度系数 (%/℃)
                String shortCurrTempCoeff = CommonUtil.getCellValue(row.getCell(7));
                if (StringUtil.isEmpty(shortCurrTempCoeff) || !StringUtil.isNumericAll(shortCurrTempCoeff)) {
                    continue;
                }
                moduleLibraryEntity.setShortCurrTempCoeff(Double.parseDouble(shortCurrTempCoeff));
                //组件类型
                String moduleType = CommonUtil.getCellValue(row.getCell(8));
                if (StringUtil.isEmpty(moduleType) || conversionModuleType(moduleType) == 0) {
                    continue;
                }
                moduleLibraryEntity.setModuleType(conversionModuleType(moduleType));
                //组件厂家
                String moduleFactory = CommonUtil.getCellValue(row.getCell(9));
                if (StringUtil.isEmpty(moduleFactory)) {
                    continue;
                }
                moduleLibraryEntity.setModuleFactory(moduleFactory);
                //组件电池片数(片/组件)
                String batteryPieces = CommonUtil.getCellValue(row.getCell(10));
                if (StringUtil.isEmpty(batteryPieces) || !StringUtil.isNumber(batteryPieces)) {
                    continue;
                }
                moduleLibraryEntity.setBatteryPieces(Integer.parseInt(batteryPieces));
                //组件首年衰减率(%/y)
                String firstDecayRate = CommonUtil.getCellValue(row.getCell(11));
                if (StringUtil.isEmpty(firstDecayRate) || !StringUtil.isNumericAll(firstDecayRate)) {
                    continue;
                }
                moduleLibraryEntity.setFirstDecayRate(Double.parseDouble(firstDecayRate));
                //组件逐年衰减率(%/y)
                String passingDecayRate = CommonUtil.getCellValue(row.getCell(12));
                if (StringUtil.isEmpty(passingDecayRate) || !StringUtil.isNumericAll(passingDecayRate)) {
                    continue;
                }
                moduleLibraryEntity.setPassingDecayRate(Double.parseDouble(passingDecayRate));
                //组件型号
                String moduleModel = CommonUtil.getCellValue(row.getCell(13));
                if (StringUtil.isEmpty(moduleModel)) {
                    continue;
                }
                moduleLibraryEntity.setModuleModel(moduleModel);
                //填充因子(%)
                String fillFactor = CommonUtil.getCellValue(row.getCell(14));
                if (StringUtil.isNotEmpty(fillFactor) && !StringUtil.isNumericAll(fillFactor)) {
                    moduleLibraryEntity.setFillFactor(Double.parseDouble(fillFactor));
                }
                //标称组件转换效率(%)
                String convertEffi = CommonUtil.getCellValue(row.getCell(15));
                if (StringUtil.isNotEmpty(convertEffi) && !StringUtil.isNumericAll(convertEffi)) {
                    moduleLibraryEntity.setConvertEffi(Double.parseDouble(convertEffi));
                }
                moduleLibraryEntityList.add(moduleLibraryEntity);
            }
            if (CollectionUtils.isNotEmpty(moduleLibraryEntityList)) {
                moduleLibraryDao.saveAll(moduleLibraryEntityList);
            }
            return ResponseResult.ok(ResponseResult.SUCCESS);
        } catch (IOException e) {
            log.error("解析导入告警事件异常", e);
            return ResponseResult.error(ResponseResult.PARAM_PARSE_ERROR);
        }
    }

    @Override
    public ResponseResult<List<String>> findModuleFactoryList() {

        return ResponseResult.ok(moduleLibraryDao.findAll().stream().map(ModuleLibraryEntity::getModuleFactory).distinct().collect(Collectors.toList()));
    }

    @Override
    public ResponseResult<List<ModuleLibraryDto>> findModuleModelList(String moduleFactory) {
        List<ModuleLibraryDto> resultList = Lists.newArrayList();
        if (StringUtil.isEmpty(moduleFactory)) {
            return ResponseResult.ok(resultList);
        }
        resultList = moduleLibraryDao.findAllByModuleFactory(moduleFactory).stream().map(moduleLibraryEntity -> {
            ModuleLibraryDto moduleLibraryDto = new ModuleLibraryDto();
            BeanUtils.copyProperties(moduleLibraryEntity, moduleLibraryDto);
            return moduleLibraryDto;
        }).collect(Collectors.toList());
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveSeriesConfigList(String deviceId, List<SeriesConfigChangeVo> seriesConfigChangeVos) {
        if (CollectionUtils.isNotEmpty(seriesConfigChangeVos)) {
            //根据设备id查询关联的所有组串配置列表
            List<SeriesConfigEntity> seriesConfigEntityList = seriesConfigDao.findAllByDeviceId(deviceId);
            if (CollectionUtils.isNotEmpty(seriesConfigEntityList)) {
                //先删除所有配置信息
                seriesConfigDao.deleteAll(seriesConfigEntityList);
            }
            //保存所有组串配置信息
            seriesConfigDao.saveAll(seriesConfigChangeVos.stream().map(seriesConfigChangeVo -> {
                SeriesConfigEntity seriesConfigEntity = new SeriesConfigEntity();
                BeanUtils.copyProperties(seriesConfigChangeVo, seriesConfigEntity);
                return seriesConfigEntity;
            }).collect(Collectors.toList()));
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.ok(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<List<SeriesConfigInfoDto>> findSeriesConfigInfo(String deviceId) {
        List<SeriesConfigInfoDto> resultList = Lists.newArrayList();

        //根据设备id查询组串配置列表
        List<SeriesConfigEntity> seriesConfigEntityList = seriesConfigDao.findAllByDeviceId(deviceId);
        if (CollectionUtils.isEmpty(seriesConfigEntityList)) {
            return ResponseResult.ok(resultList);
        }
        resultList = seriesConfigEntityList.stream().map(seriesConfigEntity -> {
            SeriesConfigInfoDto seriesConfigInfoDto = new SeriesConfigInfoDto();
            BeanUtils.copyProperties(seriesConfigEntity, seriesConfigInfoDto);
            return seriesConfigInfoDto;
        }).collect(Collectors.toList());
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> purgeSeriesConfigById(List<String> deviceIdList) {
        //根据多个设备id查询组串配置列表
        List<SeriesConfigEntity> seriesConfigEntityList = seriesConfigDao.findAllByDeviceIdIn(deviceIdList);
        if (CollectionUtils.isNotEmpty(seriesConfigEntityList)) {
            seriesConfigDao.deleteAll(seriesConfigEntityList);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.ok(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<List<SeriesDeviceListDto>> findInverterDeviceList(String siteId, Integer configStatus, String equipmentModel) {
        List<SeriesDeviceListDto> resultList = Lists.newArrayList();

        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && CollectionUtils.isNotEmpty(deviceBasicInfoBySiteIds.getData().get(siteId))) {
            List<DeviceBasicInfoDto> deviceBasicInfoList = deviceBasicInfoBySiteIds.getData().get(siteId).stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "20".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(deviceBasicInfoList)) {
                return ResponseResult.ok(resultList);
            }
            //根据多个设备id查询组串配置列表
            List<String> deviceIds = deviceBasicInfoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList());
            Map<String, List<SeriesConfigEntity>> seriesConfigMap = seriesConfigDao.findAllByDeviceIdIn(deviceIds).stream().collect(Collectors
                    .groupingBy(SeriesConfigEntity::getDeviceId));

            resultList = deviceBasicInfoList.stream().map(deviceBasicInfo -> {
                SeriesDeviceListDto result = new SeriesDeviceListDto();
                BeanUtils.copyProperties(deviceBasicInfo, result);
                if (MapUtils.isNotEmpty(seriesConfigMap) && seriesConfigMap.containsKey(deviceBasicInfo.getId()) && CollectionUtils.isNotEmpty(seriesConfigMap.get(deviceBasicInfo.getId())))  {
                    result.setConfigStatus(2);
                }
                Map<String, Object> reaMap = deviceBasicInfo.getReaMap();
                //设备型号
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                    result.setEquipmentModel(String.valueOf(reaMap.get(ReaFieldParamVo.MODEL)));
                }
                //mppt数量
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MPPT) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MPPT))) {
                    result.setMppt(Integer.parseInt(String.valueOf(reaMap.get(ReaFieldParamVo.MPPT))));
                }
                result.setTypeName(deviceBasicInfo.getTypeName());
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<String>> findInverterDeviceModelList(String siteId) {
        List<String> resultList = Lists.newArrayList();
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && CollectionUtils.isNotEmpty(deviceBasicInfoBySiteIds.getData().get(siteId))) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtos = deviceBasicInfoBySiteIds.getData().get(siteId).stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && "20".equals(d.getTypeId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(deviceBasicInfoDtos)) {
                return ResponseResult.ok(resultList);
            }

            resultList = deviceBasicInfoDtos.stream().map(deviceBasicInfoDto -> {
                Map<String, Object> reaMap = deviceBasicInfoDto.getReaMap();
                //设备型号
                if (!reaMap.isEmpty() && reaMap.containsKey(ReaFieldParamVo.MODEL) && StringUtil.isNotEmpty(reaMap.get(ReaFieldParamVo.MODEL))) {
                    return String.valueOf(reaMap.get(ReaFieldParamVo.MODEL));
                }
                return null;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 转换组件类型
     */
    private Integer conversionModuleType(String moduleType) {
        moduleType = moduleType.replace(" ","");
        int moduleTypeCode = 0;
        switch (moduleType) {
            case "多晶":
                moduleTypeCode = 1;
                break;
            case "单晶":
                moduleTypeCode = 2;
                break;
            case "叠瓦":
                moduleTypeCode = 3;
                break;
            case "P型双面":
                moduleTypeCode = 4;
                break;
            case "N型双面":
                moduleTypeCode = 5;
                break;
            default:
                break;
        }
        return moduleTypeCode;
    }
}
