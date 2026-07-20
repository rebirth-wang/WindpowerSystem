package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.ThingsModelTag;
import com.fastbee.iot.model.vo.ThingsModelTagVO;

/**
 * 物模型计算Service接口
 *
 * @author fastbee
 * @date 2025-06-24
 */
public interface IThingsModelTagService extends IService<ThingsModelTag>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询物模型计算列表
     *
     * @param thingsModelTag 物模型计算
     * @return 物模型计算分页集合
     */
    Page<ThingsModelTagVO> pageThingsModelTagVO(ThingsModelTag thingsModelTag);

    /**
     * 查询物模型计算列表
     *
     * @param thingsModelTag 物模型计算
     * @return 物模型计算集合
     */
    List<ThingsModelTagVO> listThingsModelTagVO(ThingsModelTag thingsModelTag);

    /**
     * 查询物模型计算
     *
     * @param id 主键
     * @return 物模型计算
     */
     ThingsModelTag selectThingsModelTagById(Long id);

    /**
     * 查询物模型计算
     *
     * @param id 主键
     * @return 物模型计算
     */
    ThingsModelTag queryByIdWithCache(Long id);

    /**
     * 新增物模型计算
     *
     * @param thingsModelTag 物模型计算
     * @return 是否新增成功
     */
    Boolean insertWithCache(ThingsModelTag thingsModelTag);

    /**
     * 修改物模型计算
     *
     * @param thingsModelTag 物模型计算
     * @return 是否修改成功
     */
    Boolean updateWithCache(ThingsModelTag thingsModelTag);

    /**
     * 校验并批量删除物模型计算信息
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
