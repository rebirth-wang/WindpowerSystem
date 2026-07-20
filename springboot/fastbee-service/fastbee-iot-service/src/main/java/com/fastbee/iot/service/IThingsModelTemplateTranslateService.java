package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.ThingsModelTemplateTranslate;
import com.fastbee.iot.model.vo.ThingsModelTemplateTranslateVO;

/**
 * 物模型模板翻译Service接口
 *
 * @author fastbee
 * @date 2025-12-26
 */
public interface IThingsModelTemplateTranslateService extends IService<ThingsModelTemplateTranslate>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询物模型模板翻译列表
     *
     * @param thingsModelTemplateTranslate 物模型模板翻译
     * @return 物模型模板翻译分页集合
     */
    Page<ThingsModelTemplateTranslateVO> pageThingsModelTemplateTranslateVO(ThingsModelTemplateTranslate thingsModelTemplateTranslate);

    /**
     * 查询物模型模板翻译列表
     *
     * @param thingsModelTemplateTranslate 物模型模板翻译
     * @return 物模型模板翻译集合
     */
    List<ThingsModelTemplateTranslateVO> listThingsModelTemplateTranslateVO(ThingsModelTemplateTranslate thingsModelTemplateTranslate);

    /**
     * 查询物模型模板翻译
     *
     * @param id 主键
     * @return 物模型模板翻译
     */
    ThingsModelTemplateTranslate selectThingsModelTemplateTranslateById(Long id);

    /**
     * 查询物模型模板翻译
     *
     * @param id 主键
     * @return 物模型模板翻译
     */
    ThingsModelTemplateTranslate queryByIdWithCache(Long id);

    /**
     * 新增物模型模板翻译
     *
     * @param thingsModelTemplateTranslate 物模型模板翻译
     * @return 是否新增成功
     */
    Boolean insertWithCache(ThingsModelTemplateTranslate thingsModelTemplateTranslate);

    /**
     * 修改物模型模板翻译
     *
     * @param thingsModelTemplateTranslate 物模型模板翻译
     * @return 是否修改成功
     */
    Boolean updateWithCache(ThingsModelTemplateTranslate thingsModelTemplateTranslate);

    /**
     * 校验并批量删除物模型模板翻译信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/

}
