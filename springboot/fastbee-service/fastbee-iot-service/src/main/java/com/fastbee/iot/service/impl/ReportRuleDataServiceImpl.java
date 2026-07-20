package com.fastbee.iot.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.iot.convert.ReportRuleDataConvert;
import com.fastbee.iot.domain.ReportRuleData;
import com.fastbee.iot.mapper.ReportRuleDataMapper;
import com.fastbee.iot.model.vo.ReportRuleDataVO;
import com.fastbee.iot.service.IReportRuleDataService;

/**
 * 报表规则变量Service业务层处理
 *
 * @author zzy
 * @date 2025-07-10
 */
@Service
public class ReportRuleDataServiceImpl extends ServiceImpl<ReportRuleDataMapper,ReportRuleData> implements IReportRuleDataService {

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询报表规则变量
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 报表规则变量
     */
    @Override
    @Cacheable(cacheNames = "ReportRuleData", key = "#id")
    public ReportRuleData queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询报表规则变量
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 报表规则变量
     */
    @Override
    @Cacheable(cacheNames = "ReportRuleData", key = "#id")
    public ReportRuleData selectReportRuleDataById(Long id){
        return this.getById(id);
    }

    /**
     * 查询报表规则变量分页列表
     *
     * @param reportRuleData 报表规则变量
     * @return 报表规则变量
     */
    @Override
    public Page<ReportRuleDataVO> pageReportRuleDataVO(ReportRuleData reportRuleData) {
        LambdaQueryWrapper<ReportRuleData> lqw = buildQueryWrapper(reportRuleData);
        Page<ReportRuleData> reportRuleDataPage = baseMapper.selectPage(new Page<>(reportRuleData.getPageNum(), reportRuleData.getPageSize()), lqw);
        return ReportRuleDataConvert.INSTANCE.convertReportRuleDataVOPage(reportRuleDataPage);
    }

    /**
     * 查询报表规则变量列表
     *
     * @param reportRuleData 报表规则变量
     * @return 报表规则变量
     */
    @Override
    public List<ReportRuleDataVO> listReportRuleDataVO(ReportRuleData reportRuleData) {
        LambdaQueryWrapper<ReportRuleData> lqw = buildQueryWrapper(reportRuleData);
        List<ReportRuleData> reportRuleDataList = baseMapper.selectList(lqw);
        return ReportRuleDataConvert.INSTANCE.convertReportRuleDataVOList(reportRuleDataList);
    }

    private LambdaQueryWrapper<ReportRuleData> buildQueryWrapper(ReportRuleData query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ReportRuleData> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, ReportRuleData::getId, query.getId());
                    lqw.eq(query.getReportRuleId() != null, ReportRuleData::getReportRuleId, query.getReportRuleId());
                    lqw.eq(query.getCusDataId() != null, ReportRuleData::getCusDataId, query.getCusDataId());
        return lqw;
    }

    /**
     * 新增报表规则变量
     *
     * @param add 报表规则变量
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ReportRuleData add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改报表规则变量
     *
     * @param update 报表规则变量
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "ReportRuleData", key = "#update.id")
    public Boolean updateWithCache(ReportRuleData update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ReportRuleData entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除报表规则变量信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "ReportRuleData", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
