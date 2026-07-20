package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.iot.domain.CardPlatform;
import com.fastbee.iot.model.vo.CardPlatformVO;

/**
 * 物联卡平台Service接口
 *
 * @author fastbee
 * @date 2025-11-11
 */
public interface ICardPlatformService extends IService<CardPlatform>
{

    /**
     * 查询物联卡平台列表
     *
     * @param cardPlatform 物联卡平台
     * @return 物联卡平台分页集合
     */
    Page<CardPlatformVO> pageCardPlatformVO(CardPlatform cardPlatform);

    /**
     * 查询物联卡平台列表
     *
     * @param cardPlatform 物联卡平台
     * @return 物联卡平台集合
     */
    List<CardPlatformVO> listCardPlatformVO(CardPlatform cardPlatform);

    /**
     * 查询物联卡平台
     *
     * @param id 主键
     * @return 物联卡平台
     */
     CardPlatform selectCardPlatformById(Long id);

    /**
     * 查询物联卡平台
     *
     * @param cardPlatform 卡平台
     * @return 物联卡平台
     */
    CardPlatform queryByIdWithCache(CardPlatform cardPlatform);

    /**
     * 新增物联卡平台
     *
     * @param cardPlatform 物联卡平台
     * @return 是否新增成功
     */
    Boolean insertWithCache(CardPlatform cardPlatform);

    /**
     * 修改物联卡平台
     *
     * @param cardPlatform 物联卡平台
     * @return 是否修改成功
     */
    Boolean updateWithCache(CardPlatform cardPlatform);

    /**
     * 校验并批量删除物联卡平台信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    AjaxResult deleteWithCacheByIds(Long[] ids, Boolean isValid);

}
