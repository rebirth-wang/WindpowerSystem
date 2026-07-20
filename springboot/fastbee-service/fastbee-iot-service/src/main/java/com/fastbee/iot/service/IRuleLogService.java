package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.rule.domain.RuleLog;
import com.fastbee.rule.domain.vo.RuleLogVO;

/**
 * 规则执行记录Service接口
 *
 * @author fastbee
 * @date 2025-10-21
 */
public interface IRuleLogService extends IService<RuleLog>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询规则执行记录列表
     *
     * @param ruleLog 规则执行记录
     * @return 规则执行记录分页集合
     */
    Page<RuleLogVO> pageRuleLogVO(RuleLog ruleLog);

    /**
     * 查询规则执行记录列表
     *
     * @param ruleLog 规则执行记录
     * @return 规则执行记录集合
     */
    List<RuleLogVO> listRuleLogVO(RuleLog ruleLog);

    /**
     * 查询规则执行记录
     *
     * @param id 主键
     * @return 规则执行记录
     */
     RuleLog selectRuleLogById(Long id);

    /**
     * 查询规则执行记录
     *
     * @param id 主键
     * @return 规则执行记录
     */
    RuleLog queryByIdWithCache(Long id);
    List<RuleLogVO> queryByRuleId(Long ruleId);
    List<RuleLogVO> queryByNodeId(Long ruleId, String nodeId);

    /**
     * 新增规则执行记录
     *
     * @param ruleLog 规则执行记录
     * @return 是否新增成功
     */
    Boolean insertWithCache(RuleLog ruleLog);

    /**
     * 修改规则执行记录
     *
     * @param ruleLog 规则执行记录
     * @return 是否修改成功
     */
    Boolean updateWithCache(RuleLog ruleLog);

    /**
     * 校验并批量删除规则执行记录信息
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
