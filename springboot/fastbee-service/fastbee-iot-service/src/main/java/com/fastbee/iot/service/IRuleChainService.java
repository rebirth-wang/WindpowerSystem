package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.rule.domain.RuleChain;
import com.fastbee.rule.domain.vo.RuleChainVO;

/**
 * 规则链Service接口
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
public interface IRuleChainService extends IService<RuleChain>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询规则链列表
     *
     * @param ruleChain 规则链
     * @return 规则链分页集合
     */
    Page<RuleChainVO> pageRuleChainVO(RuleChain ruleChain);

    /**
     * 查询规则链列表
     *
     * @param ruleChain 规则链
     * @return 规则链集合
     */
    List<RuleChainVO> listRuleChainVO(RuleChain ruleChain);

    /**
     * 查询规则链
     *
     * @param id 主键
     * @return 规则链
     */
     RuleChain selectRuleChainById(Long id);

    /**
     * 查询规则链
     *
     * @param id 主键
     * @return 规则链
     */
    RuleChain queryByIdWithCache(Long id);

    /**
     * 新增规则链
     *
     * @param ruleChain 规则链
     * @return 是否新增成功
     */
    Boolean insertWithCache(RuleChain ruleChain);

    /**
     * 修改规则链
     *
     * @param ruleChain 规则链
     * @return 是否修改成功
     */
    Boolean updateWithCache(RuleChain ruleChain);

    /**
     * 校验并批量删除规则链信息
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
