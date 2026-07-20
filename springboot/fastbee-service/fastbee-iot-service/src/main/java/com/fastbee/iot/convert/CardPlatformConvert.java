package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.CardPlatform;
import com.fastbee.iot.model.vo.CardPlatformVO;

/**
 * 物联卡平台Convert转换类
 *
 * @author fastbee
 * @date 2025-11-11
 */
@Mapper
public interface CardPlatformConvert
{
    /** 代码生成区域 可直接覆盖**/
    CardPlatformConvert INSTANCE = Mappers.getMapper(CardPlatformConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param cardPlatform
     * @return 物联卡平台集合
     */
    CardPlatformVO convertCardPlatformVO(CardPlatform cardPlatform);

    /**
     * VO类转换为实体类集合
     *
     * @param cardPlatformVO
     * @return 物联卡平台集合
     */
    CardPlatform convertCardPlatform(CardPlatformVO cardPlatformVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param cardPlatformList
     * @return 物联卡平台集合
     */
    List<CardPlatformVO> convertCardPlatformVOList(List<CardPlatform> cardPlatformList);

    /**
     * VO类转换为实体类
     *
     * @param cardPlatformVOList
     * @return 物联卡平台集合
     */
    List<CardPlatform> convertCardPlatformList(List<CardPlatformVO> cardPlatformVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param cardPlatformPage
     * @return 物联卡平台分页
     */
    Page<CardPlatformVO> convertCardPlatformVOPage(Page<CardPlatform> cardPlatformPage);

    /**
     * VO类转换为实体类
     *
     * @param cardPlatformVOPage
     * @return 物联卡平台分页
     */
    Page<CardPlatform> convertCardPlatformPage(Page<CardPlatformVO> cardPlatformVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
