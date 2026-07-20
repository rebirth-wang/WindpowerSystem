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

import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.service.IRuleTriggerService;
import com.fastbee.rule.convert.RuleTriggerConvert;
import com.fastbee.rule.domain.RuleTrigger;
import com.fastbee.rule.domain.vo.RuleTriggerVO;
import com.fastbee.rule.mapper.RuleTriggerMapper;

/**
 * 规则触发条件Service业务层处理
 *
 * @author fastbee
 * @date 2025-10-21
 */
@Service
public class RuleTriggerServiceImpl extends ServiceImpl<RuleTriggerMapper, RuleTrigger> implements IRuleTriggerService {

    /**
     * 查询规则触发条件
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 规则触发条件
     */
    @Override
    @Cacheable(cacheNames = "RuleTrigger", key = "#id")
    public RuleTrigger queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询规则触发条件
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 规则触发条件
     */
    @Override
    @Cacheable(cacheNames = "RuleTrigger", key = "#id")
    public RuleTrigger selectRuleTriggerById(Long id){
        return this.getById(id);
    }

    /**
     * 查询规则触发条件分页列表
     *
     * @param ruleTrigger 规则触发条件
     * @return 规则触发条件
     */
    @Override
    public Page<RuleTriggerVO> pageRuleTriggerVO(RuleTrigger ruleTrigger) {
        LambdaQueryWrapper<RuleTrigger> lqw = buildQueryWrapper(ruleTrigger);
        Page<RuleTrigger> ruleTriggerPage = baseMapper.selectPage(new Page<>(ruleTrigger.getPageNum(), ruleTrigger.getPageSize()), lqw);
        return RuleTriggerConvert.INSTANCE.convertRuleTriggerVOPage(ruleTriggerPage);
    }

    /**
     * 查询规则触发条件列表
     *
     * @param ruleTrigger 规则触发条件
     * @return 规则触发条件
     */
    @Override
    public List<RuleTriggerVO> listRuleTriggerVO(RuleTrigger ruleTrigger) {
        LambdaQueryWrapper<RuleTrigger> lqw = buildQueryWrapper(ruleTrigger);
        List<RuleTrigger> ruleTriggerList = baseMapper.selectList(lqw);
        return RuleTriggerConvert.INSTANCE.convertRuleTriggerVOList(ruleTriggerList);
    }

    private LambdaQueryWrapper<RuleTrigger> buildQueryWrapper(RuleTrigger query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<RuleTrigger> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, RuleTrigger::getId, query.getId());
                    lqw.eq(query.getTenantId() != null, RuleTrigger::getTenantId, query.getTenantId());
                    lqw.eq(query.getRuleId() != null, RuleTrigger::getRuleId, query.getRuleId());
                    lqw.eq(query.getTriggerType() != null, RuleTrigger::getTriggerType, query.getTriggerType());
                    lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), RuleTrigger::getSerialNumber, query.getSerialNumber());
                    lqw.eq(query.getProductId() != null, RuleTrigger::getProductId, query.getProductId());
                    lqw.eq(StringUtils.isNotBlank(query.getModelId()), RuleTrigger::getModelId, query.getModelId());
                    lqw.eq(StringUtils.isNotBlank(query.getOperator()), RuleTrigger::getOperator, query.getOperator());
                    lqw.eq(StringUtils.isNotBlank(query.getValue()), RuleTrigger::getValue, query.getValue());
                    lqw.eq(StringUtils.isNotBlank(query.getCronExpression()), RuleTrigger::getCronExpression, query.getCronExpression());
                    lqw.eq(StringUtils.isNotBlank(query.getCustomExpression()), RuleTrigger::getCustomExpression, query.getCustomExpression());
                    lqw.eq(query.getOrderNum() != null, RuleTrigger::getOrderNum, query.getOrderNum());
                    lqw.eq(query.getDeviceStatus() != null, RuleTrigger::getDeviceStatus, query.getDeviceStatus());
                    lqw.eq(StringUtils.isNotBlank(query.getTriggerParams()), RuleTrigger::getTriggerParams, query.getTriggerParams());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), RuleTrigger::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, RuleTrigger::getCreateTime, query.getCreateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), RuleTrigger::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, RuleTrigger::getUpdateTime, query.getUpdateTime());
                    lqw.eq(query.getDelFlag() != null, RuleTrigger::getDelFlag, query.getDelFlag());
                    lqw.eq(StringUtils.isNotBlank(query.getRemark()), RuleTrigger::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(RuleTrigger::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增规则触发条件
     *
     * @param add 规则触发条件
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(RuleTrigger add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改规则触发条件
     *
     * @param update 规则触发条件
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleTrigger", key = "#update.id")
    public Boolean updateWithCache(RuleTrigger update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RuleTrigger entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除规则触发条件信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleTrigger", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

}
