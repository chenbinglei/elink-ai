package com.sunmax.crontab.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.crontab.dto.NodeAddRecordListDto;
import com.sunmax.crontab.vo.NodeAddRecordChangeVo;
import com.sunmax.crontab.vo.NodeAddRecordQueryVo;

public interface NodeAddRecordService {

    /**
     * 添加数据补录信息
     * @param nodeAddRecordChangeVo
     * @return
     */
    ResponseResult<String> saveNodeAddRecord(NodeAddRecordChangeVo nodeAddRecordChangeVo);

    /**
     * 分页查询数据补录信息
     * @param nodeAddRecordQueryVo
     * @return
     */
    ResponseResult<PageDto<NodeAddRecordListDto>> findNodeAddRecordListByPage(NodeAddRecordQueryVo nodeAddRecordQueryVo);
}
