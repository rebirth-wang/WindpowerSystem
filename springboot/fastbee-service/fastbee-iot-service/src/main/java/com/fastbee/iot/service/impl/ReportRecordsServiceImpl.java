package com.fastbee.iot.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.ReportRecordsConvert;
import com.fastbee.iot.domain.ReportRecords;
import com.fastbee.iot.mapper.ReportRecordsMapper;
import com.fastbee.iot.model.vo.ReportRecordsVO;
import com.fastbee.iot.service.IReportRecordsService;

/**
 * 报表记录Service业务层处理
 *
 * @author zzy
 * @date 2025-07-09
 */
@Service
public class ReportRecordsServiceImpl extends ServiceImpl<ReportRecordsMapper,ReportRecords> implements IReportRecordsService {

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询报表记录
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 报表记录
     */
    @Override
    @Cacheable(cacheNames = "ReportRecords", key = "#id")
    public ReportRecords queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询报表记录
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 报表记录
     */
    @Override
    @Cacheable(cacheNames = "ReportRecords", key = "#id")
    public ReportRecords selectReportRecordsById(Long id){
        return this.getById(id);
    }

    /**
     * 查询报表记录分页列表
     *
     * @param reportRecordsVO 报表记录
     * @return 报表记录
     */
    @Override
    @DataScope(deptAlias = "r")
    public Page<ReportRecordsVO> pageReportRecordsVO(ReportRecordsVO reportRecordsVO) {
        return baseMapper.selectReportRecordsPage(new Page<>(reportRecordsVO.getPageNum(), reportRecordsVO.getPageSize()), reportRecordsVO);
    }

    /**
     * 查询报表记录列表
     *
     * @param reportRecords 报表记录
     * @return 报表记录
     */
    @Override
    public List<ReportRecordsVO> listReportRecordsVO(ReportRecords reportRecords) {
        LambdaQueryWrapper<ReportRecords> lqw = buildQueryWrapper(reportRecords);
        List<ReportRecords> reportRecordsList = baseMapper.selectList(lqw);
        return ReportRecordsConvert.INSTANCE.convertReportRecordsVOList(reportRecordsList);
    }

    private LambdaQueryWrapper<ReportRecords> buildQueryWrapper(ReportRecords query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ReportRecords> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, ReportRecords::getId, query.getId());
        lqw.eq(query.getReportId() != null, ReportRecords::getReportId, query.getReportId());
        lqw.eq(StringUtils.isNotBlank(query.getReportFilePath()), ReportRecords::getReportFilePath, query.getReportFilePath());
        lqw.eq(query.getState() != null, ReportRecords::getState, query.getState());
        lqw.eq(StringUtils.isNotBlank(query.getTimeCycle()), ReportRecords::getTimeCycle, query.getTimeCycle());
        lqw.eq(query.getCreateTime() != null, ReportRecords::getCreateTime, query.getCreateTime());
        lqw.eq(query.getDelFlag() != null, ReportRecords::getDelFlag, query.getDelFlag());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(ReportRecords::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增报表记录
     *
     * @param add 报表记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ReportRecords add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改报表记录
     *
     * @param update 报表记录
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "ReportRecords", key = "#update.id")
    public Boolean updateWithCache(ReportRecords update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ReportRecords entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除报表记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "ReportRecords", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

}
