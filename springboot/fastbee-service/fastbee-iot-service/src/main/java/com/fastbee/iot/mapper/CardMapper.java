package com.fastbee.iot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fastbee.common.extend.core.domin.model.GroupCount;
import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.model.vo.CardVO;

/**
 * 物联网卡Mapper接口
 *
 * @author fastbee
 * @date 2025-11-12
 */
public interface CardMapper extends BaseMapperX<Card>
{
    List<GroupCount> countCardOperatorGroup();
    List<GroupCount> countCardStatusGroup();
    List<GroupCount> countCardPlatformGroup();

    List<CardVO> selectCardAndPlatformList(@Param("cardVO") CardVO cardVO);
    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
