package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.rule.domain.RuleTrigger;
import com.fastbee.rule.domain.vo.RuleTriggerVO;

/**
 * 规则触发条件Service接口
 *
 * @author fastbee
 * @date 2025-10-21
 */
public interface IRuleTriggerService extends IService<RuleTrigger>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询规则触发条件列表
     *
     * @param ruleTrigger 规则触发条件
     * @return 规则触发条件分页集合
     */
    Page<RuleTriggerVO> pageRuleTriggerVO(RuleTrigger ruleTrigger);

    /**
     * 查询规则触发条件列表
     *
     * @param ruleTrigger 规则触发条件
     * @return 规则触发条件集合
     */
    List<RuleTriggerVO> listRuleTriggerVO(RuleTrigger ruleTrigger);

    /**
     * 查询规则触发条件
     *
     * @param id 主键
     * @return 规则触发条件
     */
     RuleTrigger selectRuleTriggerById(Long id);

    /**
     * 查询规则触发条件
     *
     * @param id 主键
     * @return 规则触发条件
     */
    RuleTrigger queryByIdWithCache(Long id);

    /**
     * 新增规则触发条件
     *
     * @param ruleTrigger 规则触发条件
     * @return 是否新增成功
     */
    Boolean insertWithCache(RuleTrigger ruleTrigger);

    /**
     * 修改规则触发条件
     *
     * @param ruleTrigger 规则触发条件
     * @return 是否修改成功
     */
    Boolean updateWithCache(RuleTrigger ruleTrigger);

    /**
     * 校验并批量删除规则触发条件信息
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
