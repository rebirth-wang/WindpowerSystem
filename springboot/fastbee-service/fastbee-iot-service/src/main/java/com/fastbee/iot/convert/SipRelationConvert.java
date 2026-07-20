package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.SipRelation;
import com.fastbee.iot.model.vo.SipRelationVO;

/**
 * 监控设备关联Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */
@Mapper
public interface SipRelationConvert
{

    SipRelationConvert INSTANCE = Mappers.getMapper(SipRelationConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sipRelation
     * @return 监控设备关联集合
     */
    SipRelationVO convertSipRelationVO(SipRelation sipRelation);

    /**
     * VO类转换为实体类集合
     *
     * @param sipRelationVO
     * @return 监控设备关联集合
     */
    SipRelation convertSipRelation(SipRelationVO sipRelationVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sipRelationList
     * @return 监控设备关联集合
     */
    List<SipRelationVO> convertSipRelationVOList(List<SipRelation> sipRelationList);

    /**
     * VO类转换为实体类
     *
     * @param sipRelationVOList
     * @return 监控设备关联集合
     */
    List<SipRelation> convertSipRelationList(List<SipRelationVO> sipRelationVOList);
}
