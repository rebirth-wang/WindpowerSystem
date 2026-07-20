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

import com.fastbee.iot.convert.ReportRuleConvert;
import com.fastbee.iot.domain.ReportRule;
import com.fastbee.iot.mapper.ReportRuleMapper;
import com.fastbee.iot.model.vo.ReportRuleVO;
import com.fastbee.iot.service.IReportRuleService;

/**
 * 报表规则Service业务层处理
 *
 * @author zzy
 * @date 2025-07-10
 */
@Service
public class ReportRuleServiceImpl extends ServiceImpl<ReportRuleMapper,ReportRule> implements IReportRuleService {

    /**
     * 查询报表规则
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 报表规则
     */
    @Override
    @Cacheable(cacheNames = "ReportRule", key = "#id")
    public ReportRule queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询报表规则
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 报表规则
     */
    @Override
    @Cacheable(cacheNames = "ReportRule", key = "#id")
    public ReportRule selectReportRuleById(Long id){
        return this.getById(id);
    }

    /**
     * 查询报表规则分页列表
     *
     * @param reportRule 报表规则
     * @return 报表规则
     */
    @Override
    public Page<ReportRuleVO> pageReportRuleVO(ReportRule reportRule) {
        LambdaQueryWrapper<ReportRule> lqw = buildQueryWrapper(reportRule);
        Page<ReportRule> reportRulePage = baseMapper.selectPage(new Page<>(reportRule.getPageNum(), reportRule.getPageSize()), lqw);
        return ReportRuleConvert.INSTANCE.convertReportRuleVOPage(reportRulePage);
    }

    /**
     * 查询报表规则列表
     *
     * @param reportRule 报表规则
     * @return 报表规则
     */
    @Override
    public List<ReportRuleVO> listReportRuleVO(ReportRule reportRule) {
        LambdaQueryWrapper<ReportRule> lqw = buildQueryWrapper(reportRule);
        List<ReportRule> reportRuleList = baseMapper.selectList(lqw);
        return ReportRuleConvert.INSTANCE.convertReportRuleVOList(reportRuleList);
    }

    private LambdaQueryWrapper<ReportRule> buildQueryWrapper(ReportRule query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ReportRule> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, ReportRule::getId, query.getId());
                    lqw.eq(query.getReportId() != null, ReportRule::getReportId, query.getReportId());
                    lqw.eq(query.getCusSourceId() != null, ReportRule::getCusSourceId, query.getCusSourceId());

        return lqw;
    }

    /**
     * 新增报表规则
     *
     * @param add 报表规则
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ReportRule add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改报表规则
     *
     * @param update 报表规则
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "ReportRule", key = "#update.id")
    public Boolean updateWithCache(ReportRule update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ReportRule entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除报表规则信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "ReportRule", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

}
