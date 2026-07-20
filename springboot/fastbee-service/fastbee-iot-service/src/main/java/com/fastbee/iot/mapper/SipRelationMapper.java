package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.SipRelation;
import com.fastbee.iot.model.vo.SipRelationVO;

/**
 * 监控设备关联Mapper接口
 *
 * @author kerwincui
 * @date 2024-06-06
 */
public interface SipRelationMapper extends BaseMapperX<SipRelation>
{

    /**
     * 查询监控设备关联列表
     *
     * @param sipRelation 监控设备关联
     * @return 监控设备关联集合
     */
    public List<SipRelationVO> selectSipRelationList(@Param("sipRelation") SipRelation sipRelation);
    public Page<SipRelationVO> selectSipRelationList(Page<SipRelation> page, @Param("sipRelation") SipRelation sipRelation);

    /**
     * 根据channelId更新
     * @param sipRelation
     * @return
     */
    int updateByChannelId(SipRelation sipRelation);

}
