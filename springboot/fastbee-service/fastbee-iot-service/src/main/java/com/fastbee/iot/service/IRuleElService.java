package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.rule.domain.RuleEl;
import com.fastbee.rule.domain.vo.RuleElVO;

/**
 * 规则elService接口
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
public interface IRuleElService extends IService<RuleEl>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询规则el列表
     *
     * @param ruleEl 规则el
     * @return 规则el分页集合
     */
    Page<RuleElVO> pageRuleElVO(RuleEl ruleEl);

    /**
     * 查询规则el列表
     *
     * @param ruleEl 规则el
     * @return 规则el集合
     */
    List<RuleElVO> listRuleElVO(RuleEl ruleEl);

    /**
     * 查询规则el
     *
     * @param id 主键
     * @return 规则el
     */
     RuleEl selectRuleElById(Long id);

    /**
     * 查询规则el
     *
     * @param ruleEl 规则el
     * @return 规则el
     */
    RuleEl queryByIdWithCache(RuleEl ruleEl);

    /**
     * 新增规则el
     *
     * @param ruleEl 规则el
     * @return 是否新增成功
     */
    Boolean insertWithCache(RuleEl ruleEl);

    /**
     * 修改规则el
     *
     * @param ruleEl 规则el
     * @return 是否修改成功
     */
    RuleEl updateWithCache(RuleEl ruleEl);

    /**
     * 校验并批量删除规则el信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    Boolean exec(RuleEl ruleEl);
    Boolean execByid(Long id, Object... contextBeanArray);
    Boolean publish(RuleEl ruleEl);
    /** 自定义代码区域 END**/

}
