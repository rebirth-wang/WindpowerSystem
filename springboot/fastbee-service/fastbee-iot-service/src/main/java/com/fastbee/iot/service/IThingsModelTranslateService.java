package com.fastbee.iot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.ThingsModelTranslate;
import com.fastbee.iot.model.vo.ThingsModelTranslateVO;

/**
 * 物模型翻译Service接口
 *
 * @author zhuangpeng.li
 * @date 2024-12-24
 */
public interface IThingsModelTranslateService extends IService<ThingsModelTranslate>
{

    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询物模型翻译列表
     *
     * @param thingsModelTranslate 物模型翻译
     * @return 物模型翻译分页集合
     */
    Page<ThingsModelTranslateVO> pageThingsModelTranslateVO(ThingsModelTranslate thingsModelTranslate);

    /**
     * 查询物模型翻译
     *
     * @param id 主键
     * @return 物模型翻译
     */
     ThingsModelTranslate selectThingsModelTranslateById(Long id);

    /**
     * 查询物模型翻译
     *
     * @param id 主键
     * @return 物模型翻译
     */
    ThingsModelTranslate queryByIdWithCache(Long id);

    /**
     * 新增物模型翻译
     *
     * @param thingsModelTranslate 物模型翻译
     * @return 是否新增成功
     */
    Boolean insertWithCache(ThingsModelTranslate thingsModelTranslate);

    /**
     * 修改物模型翻译
     *
     * @param thingsModelTranslate 物模型翻译
     * @return 是否修改成功
     */
    Boolean updateWithCache(ThingsModelTranslate thingsModelTranslate);

    /**
     * 校验并批量删除物模型翻译信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);


}
