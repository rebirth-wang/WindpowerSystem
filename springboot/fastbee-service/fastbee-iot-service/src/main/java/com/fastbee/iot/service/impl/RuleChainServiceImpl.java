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
import com.fastbee.iot.service.IRuleChainService;
import com.fastbee.rule.convert.RuleChainConvert;
import com.fastbee.rule.domain.RuleChain;
import com.fastbee.rule.domain.vo.RuleChainVO;
import com.fastbee.rule.mapper.RuleChainMapper;

/**
 * 规则链Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@Service
public class RuleChainServiceImpl extends ServiceImpl<RuleChainMapper,RuleChain> implements IRuleChainService {

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询规则链
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 规则链
     */
    @Override
    @Cacheable(cacheNames = "RuleChain", key = "#id")
    public RuleChain queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询规则链
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 规则链
     */
    @Override
    @Cacheable(cacheNames = "RuleChain", key = "#id")
    public RuleChain selectRuleChainById(Long id){
        return this.getById(id);
    }

    /**
     * 查询规则链分页列表
     *
     * @param ruleChain 规则链
     * @return 规则链
     */
    @Override
    @DataScope()
    public Page<RuleChainVO> pageRuleChainVO(RuleChain ruleChain) {
        LambdaQueryWrapper<RuleChain> lqw = buildQueryWrapper(ruleChain);
        Page<RuleChain> ruleChainPage = baseMapper.selectPage(new Page<>(ruleChain.getPageNum(), ruleChain.getPageSize()), lqw);
        return RuleChainConvert.INSTANCE.convertRuleChainVOPage(ruleChainPage);
    }

    /**
     * 查询规则链列表
     *
     * @param ruleChain 规则链
     * @return 规则链
     */
    @Override
    public List<RuleChainVO> listRuleChainVO(RuleChain ruleChain) {
        LambdaQueryWrapper<RuleChain> lqw = buildQueryWrapper(ruleChain);
        List<RuleChain> ruleChainList = baseMapper.selectList(lqw);
        return RuleChainConvert.INSTANCE.convertRuleChainVOList(ruleChainList);
    }

    private LambdaQueryWrapper<RuleChain> buildQueryWrapper(RuleChain query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<RuleChain> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getTenantId() != null, RuleChain::getTenantId, query.getTenantId());
                    lqw.eq(query.getId() != null, RuleChain::getId, query.getId());
                    lqw.like(StringUtils.isNotBlank(query.getApplicationName()), RuleChain::getApplicationName, query.getApplicationName());
                    lqw.eq(StringUtils.isNotBlank(query.getChainId()), RuleChain::getChainId, query.getChainId());
                    lqw.like(StringUtils.isNotBlank(query.getChainName()), RuleChain::getChainName, query.getChainName());
                    lqw.eq(StringUtils.isNotBlank(query.getElData()), RuleChain::getElData, query.getElData());
                    lqw.eq(query.getEnable() != null, RuleChain::getEnable, query.getEnable());
                    lqw.eq(StringUtils.isNotBlank(query.getContextBeans()), RuleChain::getContextBeans, query.getContextBeans());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), RuleChain::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, RuleChain::getCreateTime, query.getCreateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), RuleChain::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, RuleChain::getUpdateTime, query.getUpdateTime());
                    lqw.eq(query.getDelFlag() != null, RuleChain::getDelFlag, query.getDelFlag());
                    lqw.eq(StringUtils.isNotBlank(query.getRemark()), RuleChain::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(RuleChain::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增规则链
     *
     * @param add 规则链
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(RuleChain add) {
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
     * 修改规则链
     *
     * @param update 规则链
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleChain", key = "#update.id")
    public Boolean updateWithCache(RuleChain update) {
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
    private void validEntityBeforeSave(RuleChain entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除规则链信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleChain", keyGenerator = "deleteKeyGenerator" )
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
