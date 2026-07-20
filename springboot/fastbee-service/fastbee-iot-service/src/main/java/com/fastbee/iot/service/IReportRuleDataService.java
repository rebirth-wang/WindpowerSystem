package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.ReportRuleData;
import com.fastbee.iot.model.vo.ReportRuleDataVO;

/**
 * 报表规则变量Service接口
 *
 * @author zzy
 * @date 2025-07-10
 */
public interface IReportRuleDataService extends IService<ReportRuleData>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询报表规则变量列表
     *
     * @param reportRuleData 报表规则变量
     * @return 报表规则变量分页集合
     */
    Page<ReportRuleDataVO> pageReportRuleDataVO(ReportRuleData reportRuleData);

    /**
     * 查询报表规则变量列表
     *
     * @param reportRuleData 报表规则变量
     * @return 报表规则变量集合
     */
    List<ReportRuleDataVO> listReportRuleDataVO(ReportRuleData reportRuleData);

    /**
     * 查询报表规则变量
     *
     * @param id 主键
     * @return 报表规则变量
     */
     ReportRuleData selectReportRuleDataById(Long id);

    /**
     * 查询报表规则变量
     *
     * @param id 主键
     * @return 报表规则变量
     */
    ReportRuleData queryByIdWithCache(Long id);

    /**
     * 新增报表规则变量
     *
     * @param reportRuleData 报表规则变量
     * @return 是否新增成功
     */
    Boolean insertWithCache(ReportRuleData reportRuleData);

    /**
     * 修改报表规则变量
     *
     * @param reportRuleData 报表规则变量
     * @return 是否修改成功
     */
    Boolean updateWithCache(ReportRuleData reportRuleData);

    /**
     * 校验并批量删除报表规则变量信息
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
