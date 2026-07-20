package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.rule.domain.RuleExecutor;
import com.fastbee.rule.domain.vo.RuleExecutorVO;

/**
 * 规则执行器Service接口
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
public interface IRuleExecutorService extends IService<RuleExecutor>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询规则执行器列表
     *
     * @param ruleExecutor 规则执行器
     * @return 规则执行器分页集合
     */
    Page<RuleExecutorVO> pageRuleExecutorVO(RuleExecutor ruleExecutor);

    /**
     * 查询规则执行器列表
     *
     * @param ruleExecutor 规则执行器
     * @return 规则执行器集合
     */
    List<RuleExecutorVO> listRuleExecutorVO(RuleExecutor ruleExecutor);

    /**
     * 查询规则执行器
     *
     * @param id 主键
     * @return 规则执行器
     */
     RuleExecutor selectRuleExecutorById(Long id);

    /**
     * 查询规则执行器
     *
     * @param id 主键
     * @return 规则执行器
     */
    RuleExecutor queryByIdWithCache(Long id);

    /**
     * 新增规则执行器
     *
     * @param ruleExecutor 规则执行器
     * @return 是否新增成功
     */
    Boolean insertWithCache(RuleExecutor ruleExecutor);

    /**
     * 修改规则执行器
     *
     * @param ruleExecutor 规则执行器
     * @return 是否修改成功
     */
    Boolean updateWithCache(RuleExecutor ruleExecutor);

    /**
     * 校验并批量删除规则执行器信息
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
