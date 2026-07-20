package com.fastbee.iot.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.service.IRuleLogService;
import com.fastbee.rule.context.DebugStep;
import com.fastbee.rule.convert.RuleLogConvert;
import com.fastbee.rule.domain.RuleLog;
import com.fastbee.rule.domain.vo.RuleLogVO;
import com.fastbee.rule.mapper.RuleLogMapper;

/**
 * 规则执行记录Service业务层处理
 *
 * @author fastbee
 * @date 2025-10-21
 */
@Slf4j
@Service
public class RuleLogServiceImpl extends ServiceImpl<RuleLogMapper, RuleLog> implements IRuleLogService {


    /**
     * 查询规则执行记录
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param id 主键
     * @return 规则执行记录
     */
    @Override
    @Cacheable(cacheNames = "RuleLog", key = "#id")
    public RuleLog queryByIdWithCache(Long id) {
        return this.getById(id);
    }

    public List<RuleLogVO> queryByRuleId(Long ruleId) {
        LambdaQueryWrapper<RuleLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(ruleId != null, RuleLog::getRuleId, ruleId);
        List<RuleLog> ruleLogList = baseMapper.selectList(lqw);
        return RuleLogConvert.INSTANCE.convertRuleLogVOList(ruleLogList);
    }

    @Override
    public List<RuleLogVO> queryByNodeId(Long ruleId, String nodeId) {
        List<RuleLogVO> list = this.queryByRuleId(ruleId);
        List<RuleLogVO> newlist = new ArrayList<>();
        for (RuleLogVO ruleLog : list) {
            // 字符串转json对象
            if (ruleLog.getStepMsg()!= null ) {
                List<DebugStep> steps = JSON.parseArray(ruleLog.getStepMsg(), DebugStep.class);
                // 过滤tag 等于 nodeid的项
                List<DebugStep> stepitem = steps.stream().filter(step -> step.getTag() != null && Objects.equals(step.getTag(), nodeId)).collect(Collectors.toList());
                if(!stepitem.isEmpty()) {
                    ruleLog.setStepMsg(JSON.toJSONString(stepitem));
                    newlist.add(ruleLog);
                }
            }
        }
        return newlist;
    }

    /**
     * 查询规则执行记录
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param id 主键
     * @return 规则执行记录
     */
    @Override
    @Cacheable(cacheNames = "RuleLog", key = "#id")
    public RuleLog selectRuleLogById(Long id) {
        return this.getById(id);
    }

    /**
     * 查询规则执行记录分页列表
     *
     * @param ruleLog 规则执行记录
     * @return 规则执行记录
     */
    @Override
    public Page<RuleLogVO> pageRuleLogVO(RuleLog ruleLog) {
        LambdaQueryWrapper<RuleLog> lqw = buildQueryWrapper(ruleLog);
        Page<RuleLog> ruleLogPage = baseMapper.selectPage(new Page<>(ruleLog.getPageNum(), ruleLog.getPageSize()), lqw);
        return RuleLogConvert.INSTANCE.convertRuleLogVOPage(ruleLogPage);
    }

    /**
     * 查询规则执行记录列表
     *
     * @param ruleLog 规则执行记录
     * @return 规则执行记录
     */
    @Override
    public List<RuleLogVO> listRuleLogVO(RuleLog ruleLog) {
        LambdaQueryWrapper<RuleLog> lqw = buildQueryWrapper(ruleLog);
        List<RuleLog> ruleLogList = baseMapper.selectList(lqw);
        return RuleLogConvert.INSTANCE.convertRuleLogVOList(ruleLogList);
    }

    private LambdaQueryWrapper<RuleLog> buildQueryWrapper(RuleLog query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<RuleLog> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, RuleLog::getId, query.getId());
        lqw.eq(query.getTenantId() != null, RuleLog::getTenantId, query.getTenantId());
        lqw.eq(query.getRuleId() != null, RuleLog::getRuleId, query.getRuleId());
        lqw.eq(StringUtils.isNotBlank(query.getElId()), RuleLog::getElId, query.getElId());
        lqw.eq(query.getStatus() != null, RuleLog::getStatus, query.getStatus());
        lqw.eq(query.getTriggerType() != null, RuleLog::getTriggerType, query.getTriggerType());
        lqw.eq(StringUtils.isNotBlank(query.getRuleParams()), RuleLog::getRuleParams, query.getRuleParams());
        lqw.eq(StringUtils.isNotBlank(query.getStepMsg()), RuleLog::getStepMsg, query.getStepMsg());
        lqw.eq(StringUtils.isNotBlank(query.getResultMsg()), RuleLog::getResultMsg, query.getResultMsg());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), RuleLog::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, RuleLog::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), RuleLog::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, RuleLog::getUpdateTime, query.getUpdateTime());
        lqw.eq(query.getDelFlag() != null, RuleLog::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), RuleLog::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(RuleLog::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增规则执行记录
     *
     * @param add 规则执行记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(RuleLog add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改规则执行记录
     *
     * @param update 规则执行记录
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleLog", key = "#update.id")
    public Boolean updateWithCache(RuleLog update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RuleLog entity) {
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除规则执行记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleLog", keyGenerator = "deleteKeyGenerator")
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

}
