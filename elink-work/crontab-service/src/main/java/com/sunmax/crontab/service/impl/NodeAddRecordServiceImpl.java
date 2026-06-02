package com.sunmax.crontab.service.impl;

import com.google.common.collect.Lists;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.crontab.dao.ComputeNodeDao;
import com.sunmax.crontab.dao.NodeAddRecordDao;
import com.sunmax.crontab.dto.NodeAddRecordListDto;
import com.sunmax.crontab.entity.ComputeNodeEntity;
import com.sunmax.crontab.entity.NodeAddRecordEntity;
import com.sunmax.crontab.service.NodeAddRecordService;
import com.sunmax.crontab.vo.NodeAddRecordChangeVo;
import com.sunmax.crontab.vo.NodeAddRecordQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.localDateTimeToStr;

@Slf4j
@Service
public class NodeAddRecordServiceImpl implements NodeAddRecordService {

    @Autowired
    private NodeAddRecordDao nodeAddRecordDao;

    @Autowired
    private ComputeNodeDao computeNodeDao;

    /**
     * 添加数据补录信息
     * @param nodeAddRecordChangeVo
     * @return
     */
    @Override
    public ResponseResult<String> saveNodeAddRecord(NodeAddRecordChangeVo nodeAddRecordChangeVo) {
        //新增数据补录信息
        if (StringUtil.isNotEmpty(nodeAddRecordChangeVo)) {
            NodeAddRecordEntity nodeAddRecordEntity = new NodeAddRecordEntity();
            BeanUtils.copyProperties(nodeAddRecordChangeVo, nodeAddRecordEntity);
            nodeAddRecordEntity.setCreateId(nodeAddRecordChangeVo.getUserId());
            nodeAddRecordEntity.setCreateTime(LocalDateTime.now());
            nodeAddRecordEntity.setUpdateTime(LocalDateTime.now());
            NodeAddRecordEntity addRecordEntity = nodeAddRecordDao.save(nodeAddRecordEntity);
            //
            return ResponseResult.ok(addRecordEntity.getId());
        }
        return ResponseResult.error(ResponseResult.PARAM_ERROR);
    }

    /**
     * 分页查询数据补录信息
     * @param nodeAddRecordQueryVo
     * @return
     */
    @Override
    public ResponseResult<PageDto<NodeAddRecordListDto>> findNodeAddRecordListByPage(NodeAddRecordQueryVo nodeAddRecordQueryVo) {
        //返回数据
        List<NodeAddRecordListDto> resultList = Lists.newArrayList();

        //根据设备id查询补录数据
        List<NodeAddRecordEntity> nodeAddRecordEntityList = nodeAddRecordDao.findAllByDeviceId(nodeAddRecordQueryVo.getDeviceId());
        if (CollectionUtils.isNotEmpty(nodeAddRecordEntityList)) {
            List<String> nodeIdList = nodeAddRecordEntityList.stream().map(NodeAddRecordEntity::getNodeId).distinct().collect(Collectors.toList());
            //根据的多个节点id查询节点数据
            List<ComputeNodeEntity> nodeDaoAllById = computeNodeDao.findAllById(nodeIdList);
            if (CollectionUtils.isNotEmpty(nodeDaoAllById)) {
                Map<String, ComputeNodeEntity> modelComputeNodeEntityMap = nodeDaoAllById.stream().collect(Collectors.toMap(ComputeNodeEntity::getId, ModelComputeNodeEntity -> ModelComputeNodeEntity, (k1, k2) -> k1));
                resultList = nodeAddRecordEntityList.stream().map(nodeAddRecordEntity -> {
                    NodeAddRecordListDto nodeAddRecordListDto = new NodeAddRecordListDto();
                    BeanUtils.copyProperties(nodeAddRecordEntity, nodeAddRecordListDto);
                    nodeAddRecordListDto.setCreateTime(localDateTimeToStr(nodeAddRecordEntity.getCreateTime()));
                    if (modelComputeNodeEntityMap.containsKey(nodeAddRecordEntity.getNodeId())) {
                        ComputeNodeEntity modelComputeNodeEntity = modelComputeNodeEntityMap.get(nodeAddRecordEntity.getNodeId());
                        nodeAddRecordListDto.setNodeName(modelComputeNodeEntity.getNodeName());
                        nodeAddRecordListDto.setNodeCode(modelComputeNodeEntity.getNodeCode());
                    }
                    return nodeAddRecordListDto;
                }).collect(Collectors.toList());

                if (StringUtil.isNotEmpty(nodeAddRecordQueryVo.getKeyType()) && StringUtil.isNotEmpty(nodeAddRecordQueryVo.getKeyValue())) {
                    //名称
                    if (nodeAddRecordQueryVo.getKeyType() == 1) {
                        resultList = resultList.stream().filter(n -> n.getNodeName().contains(nodeAddRecordQueryVo.getKeyValue())).collect(Collectors.toList());
                    } else if (nodeAddRecordQueryVo.getKeyType() == 2) {//编码
                        resultList = resultList.stream().filter(n -> n.getNodeCode().contains(nodeAddRecordQueryVo.getKeyValue())).collect(Collectors.toList());
                    }
                }
                if (CollectionUtils.isNotEmpty(resultList)) {
                    resultList = resultList.stream().sorted(Comparator.comparing(NodeAddRecordListDto::getCreateTime).reversed()).collect(Collectors.toList());
                }
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, nodeAddRecordQueryVo.getPage(), nodeAddRecordQueryVo.getSize()));
    }
}
