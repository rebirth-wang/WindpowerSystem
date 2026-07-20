package com.fastbee.iot.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.rule.domain.RuleCmp;
import com.fastbee.rule.domain.vo.RuleCmpVO;

/**
 * 规则组件Service接口
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
public interface IRuleCmpService extends IService<RuleCmp>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询规则组件列表
     *
     * @param ruleCmp 规则组件
     * @return 规则组件分页集合
     */
    Page<RuleCmpVO> pageRuleCmpVO(RuleCmp ruleCmp);

    /**
     * 查询规则组件列表
     *
     * @param ruleCmp 规则组件
     * @return 规则组件集合
     */
    List<RuleCmpVO> listRuleCmpVO(RuleCmp ruleCmp);

    /**
     * 查询规则组件
     *
     * @param id 主键
     * @return 规则组件
     */
     RuleCmp selectRuleCmpById(Long id);

    /**
     * 查询规则组件
     *
     * @param id 主键
     * @return 规则组件
     */
    RuleCmp queryByIdWithCache(Long id);

    /**
     * 新增规则组件
     *
     * @param ruleCmp 规则组件
     * @return 是否新增成功
     */
    Boolean insertWithCache(RuleCmp ruleCmp);

    /**
     * 修改规则组件
     *
     * @param ruleCmp 规则组件
     * @return 是否修改成功
     */
    Boolean updateWithCache(RuleCmp ruleCmp);

    /**
     * 校验并批量删除规则组件信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    Boolean exec(RuleCmp ruleCmp);
    Boolean options(Map<String,Object> map);
    Boolean cmpOptions(Map<String,Object> map);
    Boolean typeOptions(Map<String,Object> map);
    Boolean scriptOptions(Map<String,Object> map);

    /** 自定义代码区域 END**/

}
