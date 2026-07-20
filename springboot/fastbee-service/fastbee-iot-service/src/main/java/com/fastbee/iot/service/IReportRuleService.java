package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.ReportRule;
import com.fastbee.iot.model.vo.ReportRuleVO;

/**
 * 报表规则Service接口
 *
 * @author zzy
 * @date 2025-07-10
 */
public interface IReportRuleService extends IService<ReportRule>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询报表规则列表
     *
     * @param reportRule 报表规则
     * @return 报表规则分页集合
     */
    Page<ReportRuleVO> pageReportRuleVO(ReportRule reportRule);

    /**
     * 查询报表规则列表
     *
     * @param reportRule 报表规则
     * @return 报表规则集合
     */
    List<ReportRuleVO> listReportRuleVO(ReportRule reportRule);

    /**
     * 查询报表规则
     *
     * @param id 主键
     * @return 报表规则
     */
     ReportRule selectReportRuleById(Long id);

    /**
     * 查询报表规则
     *
     * @param id 主键
     * @return 报表规则
     */
    ReportRule queryByIdWithCache(Long id);

    /**
     * 新增报表规则
     *
     * @param reportRule 报表规则
     * @return 是否新增成功
     */
    Boolean insertWithCache(ReportRule reportRule);

    /**
     * 修改报表规则
     *
     * @param reportRule 报表规则
     * @return 是否修改成功
     */
    Boolean updateWithCache(ReportRule reportRule);

    /**
     * 校验并批量删除报表规则信息
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
