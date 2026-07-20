package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.service.IRuleExecutorService;
import com.fastbee.rule.convert.RuleExecutorConvert;
import com.fastbee.rule.domain.RuleExecutor;
import com.fastbee.rule.domain.vo.RuleExecutorVO;
import com.fastbee.rule.mapper.RuleExecutorMapper;

/**
 * 规则执行器Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@Service
public class RuleExecutorServiceImpl extends ServiceImpl<RuleExecutorMapper,RuleExecutor> implements IRuleExecutorService {

    /**
     * 查询规则执行器
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 规则执行器
     */
    @Override
    @Cacheable(cacheNames = "RuleExecutor", key = "#id")
    public RuleExecutor queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询规则执行器
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 规则执行器
     */
    @Override
    @Cacheable(cacheNames = "RuleExecutor", key = "#id")
    public RuleExecutor selectRuleExecutorById(Long id){
        return this.getById(id);
    }

    /**
     * 查询规则执行器分页列表
     *
     * @param ruleExecutor 规则执行器
     * @return 规则执行器
     */
    @Override
    public Page<RuleExecutorVO> pageRuleExecutorVO(RuleExecutor ruleExecutor) {
        LambdaQueryWrapper<RuleExecutor> lqw = buildQueryWrapper(ruleExecutor);
        Page<RuleExecutor> ruleExecutorPage = baseMapper.selectPage(new Page<>(ruleExecutor.getPageNum(), ruleExecutor.getPageSize()), lqw);
        return RuleExecutorConvert.INSTANCE.convertRuleExecutorVOPage(ruleExecutorPage);
    }

    /**
     * 查询规则执行器列表
     *
     * @param ruleExecutor 规则执行器
     * @return 规则执行器
     */
    @Override
    @DataScope()
    public List<RuleExecutorVO> listRuleExecutorVO(RuleExecutor ruleExecutor) {
        LambdaQueryWrapper<RuleExecutor> lqw = buildQueryWrapper(ruleExecutor);
        List<RuleExecutor> ruleExecutorList = baseMapper.selectList(lqw);
        return RuleExecutorConvert.INSTANCE.convertRuleExecutorVOList(ruleExecutorList);
    }

    private LambdaQueryWrapper<RuleExecutor> buildQueryWrapper(RuleExecutor query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<RuleExecutor> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getTenantId() != null, RuleExecutor::getTenantId, query.getTenantId());
                    lqw.eq(query.getId() != null, RuleExecutor::getId, query.getId());
                    lqw.eq(StringUtils.isNotBlank(query.getExecutorId()), RuleExecutor::getExecutorId, query.getExecutorId());
                    lqw.like(StringUtils.isNotBlank(query.getExecutorName()), RuleExecutor::getExecutorName, query.getExecutorName());
                    lqw.eq(query.getIvyConfigId() != null, RuleExecutor::getIvyConfigId, query.getIvyConfigId());
                    lqw.eq(StringUtils.isNotBlank(query.getExecutorType()), RuleExecutor::getExecutorType, query.getExecutorType());
                    lqw.eq(StringUtils.isNotBlank(query.getContextBeans()), RuleExecutor::getContextBeans, query.getContextBeans());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), RuleExecutor::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, RuleExecutor::getCreateTime, query.getCreateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), RuleExecutor::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, RuleExecutor::getUpdateTime, query.getUpdateTime());
                    lqw.eq(query.getDelFlag() != null, RuleExecutor::getDelFlag, query.getDelFlag());
                    lqw.eq(StringUtils.isNotBlank(query.getRemark()), RuleExecutor::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(RuleExecutor::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增规则执行器
     *
     * @param add 规则执行器
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(RuleExecutor add) {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            throw new ServiceException(MessageUtils.message("only.allow.tenant.config"));
        }
        validEntityBeforeSave(add);
        add.setTenantId(user.getDept().getDeptUserId());
        add.setCreateTime(DateUtils.getNowDate());
        add.setCreateBy(user.getUserName());
        return this.save(add);
    }

    /**
     * 修改规则执行器
     *
     * @param update 规则执行器
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleExecutor", key = "#update.id")
    public Boolean updateWithCache(RuleExecutor update) {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            throw new ServiceException(MessageUtils.message("only.allow.tenant.config"));
        }
        validEntityBeforeSave(update);
        update.setUpdateTime(DateUtils.getNowDate());
        update.setUpdateBy(user.getUserName());
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RuleExecutor entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除规则执行器信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleExecutor", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

}
