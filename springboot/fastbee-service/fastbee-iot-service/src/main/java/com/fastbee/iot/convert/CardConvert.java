package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.Card;
import com.fastbee.iot.model.vo.CardVO;

/**
 * 物联网卡Convert转换类
 *
 * @author fastbee
 * @date 2025-11-12
 */
@Mapper
public interface CardConvert
{
    /** 代码生成区域 可直接覆盖**/
    CardConvert INSTANCE = Mappers.getMapper(CardConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param card
     * @return 物联网卡集合
     */
    CardVO convertCardVO(Card card);

    /**
     * VO类转换为实体类集合
     *
     * @param cardVO
     * @return 物联网卡集合
     */
    Card convertCard(CardVO cardVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param cardList
     * @return 物联网卡集合
     */
    List<CardVO> convertCardVOList(List<Card> cardList);

    /**
     * VO类转换为实体类
     *
     * @param cardVOList
     * @return 物联网卡集合
     */
    List<Card> convertCardList(List<CardVO> cardVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param cardPage
     * @return 物联网卡分页
     */
    Page<CardVO> convertCardVOPage(Page<Card> cardPage);

    /**
     * VO类转换为实体类
     *
     * @param cardVOPage
     * @return 物联网卡分页
     */
    Page<Card> convertCardPage(Page<CardVO> cardVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
