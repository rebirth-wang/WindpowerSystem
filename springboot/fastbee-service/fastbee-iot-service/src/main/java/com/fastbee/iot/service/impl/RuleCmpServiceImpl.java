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
import com.fastbee.iot.service.IRuleCmpService;
import com.fastbee.rule.convert.RuleCmpConvert;
import com.fastbee.rule.domain.RuleCmp;
import com.fastbee.rule.domain.vo.RuleCmpVO;
import com.fastbee.rule.mapper.RuleCmpMapper;

/**
 * 规则组件Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@Service
public class RuleCmpServiceImpl extends ServiceImpl<RuleCmpMapper,RuleCmp> implements IRuleCmpService {

    /**
     * 查询规则组件
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 规则组件
     */
    @Override
    @Cacheable(cacheNames = "RuleCmp", key = "#id")
    public RuleCmp queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询规则组件
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 规则组件
     */
    @Override
    @Cacheable(cacheNames = "RuleCmp", key = "#id")
    public RuleCmp selectRuleCmpById(Long id){
        return this.getById(id);
    }

    /**
     * 查询规则组件分页列表
     *
     * @param ruleCmp 规则组件
     * @return 规则组件
     */
    @Override
    @DataScope()
    public Page<RuleCmpVO> pageRuleCmpVO(RuleCmp ruleCmp) {
        LambdaQueryWrapper<RuleCmp> lqw = buildQueryWrapper(ruleCmp);
        Page<RuleCmp> ruleCmpPage = baseMapper.selectPage(new Page<>(ruleCmp.getPageNum(), ruleCmp.getPageSize()), lqw);
        return RuleCmpConvert.INSTANCE.convertRuleCmpVOPage(ruleCmpPage);
    }

    /**
     * 查询规则组件列表
     *
     * @param ruleCmp 规则组件
     * @return 规则组件
     */
    @Override
    public List<RuleCmpVO> listRuleCmpVO(RuleCmp ruleCmp) {
        LambdaQueryWrapper<RuleCmp> lqw = buildQueryWrapper(ruleCmp);
        List<RuleCmp> ruleCmpList = baseMapper.selectList(lqw);
        return RuleCmpConvert.INSTANCE.convertRuleCmpVOList(ruleCmpList);
    }

    private LambdaQueryWrapper<RuleCmp> buildQueryWrapper(RuleCmp query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<RuleCmp> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getTenantId() != null, RuleCmp::getTenantId, query.getTenantId());
                    lqw.eq(query.getId() != null, RuleCmp::getId, query.getId());
                    lqw.eq(StringUtils.isNotBlank(query.getComponentId()), RuleCmp::getComponentId, query.getComponentId());
                    lqw.like(StringUtils.isNotBlank(query.getComponentName()), RuleCmp::getComponentName, query.getComponentName());
                    lqw.eq(StringUtils.isNotBlank(query.getType()), RuleCmp::getType, query.getType());
                    lqw.eq(StringUtils.isNotBlank(query.getScript()), RuleCmp::getScript, query.getScript());
                    lqw.eq(StringUtils.isNotBlank(query.getLanguage()), RuleCmp::getLanguage, query.getLanguage());
                    lqw.eq(StringUtils.isNotBlank(query.getClazz()), RuleCmp::getClazz, query.getClazz());
                    lqw.eq(StringUtils.isNotBlank(query.getEl()), RuleCmp::getEl, query.getEl());
                    lqw.eq(StringUtils.isNotBlank(query.getCmpPre()), RuleCmp::getCmpPre, query.getCmpPre());
                    lqw.eq(StringUtils.isNotBlank(query.getCmpFinallyOpt()), RuleCmp::getCmpFinallyOpt, query.getCmpFinallyOpt());
                    lqw.eq(StringUtils.isNotBlank(query.getCmpId()), RuleCmp::getCmpId, query.getCmpId());
                    lqw.eq(StringUtils.isNotBlank(query.getCmpTag()), RuleCmp::getCmpTag, query.getCmpTag());
                    lqw.eq(query.getCmpMaxWaitSeconds() != null, RuleCmp::getCmpMaxWaitSeconds, query.getCmpMaxWaitSeconds());
                    lqw.eq(StringUtils.isNotBlank(query.getElFormat()), RuleCmp::getElFormat, query.getElFormat());
                    lqw.eq(StringUtils.isNotBlank(query.getCmpDefaultOpt()), RuleCmp::getCmpDefaultOpt, query.getCmpDefaultOpt());
                    lqw.eq(StringUtils.isNotBlank(query.getCmpTo()), RuleCmp::getCmpTo, query.getCmpTo());
                    lqw.eq(StringUtils.isNotBlank(query.getCmpTrueOpt()), RuleCmp::getCmpTrueOpt, query.getCmpTrueOpt());
                    lqw.eq(StringUtils.isNotBlank(query.getCmpFalseOpt()), RuleCmp::getCmpFalseOpt, query.getCmpFalseOpt());
                    lqw.eq(query.getCmpParallel() != null, RuleCmp::getCmpParallel, query.getCmpParallel());
                    lqw.eq(StringUtils.isNotBlank(query.getCmpDoOpt()), RuleCmp::getCmpDoOpt, query.getCmpDoOpt());
                    lqw.eq(StringUtils.isNotBlank(query.getCmpBreakOpt()), RuleCmp::getCmpBreakOpt, query.getCmpBreakOpt());
                    lqw.eq(StringUtils.isNotBlank(query.getCmpData()), RuleCmp::getCmpData, query.getCmpData());
                    lqw.like(StringUtils.isNotBlank(query.getCmpDataName()), RuleCmp::getCmpDataName, query.getCmpDataName());
                    lqw.eq(query.getFallbackId() != null, RuleCmp::getFallbackId, query.getFallbackId());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), RuleCmp::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, RuleCmp::getCreateTime, query.getCreateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), RuleCmp::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, RuleCmp::getUpdateTime, query.getUpdateTime());
                    lqw.eq(query.getDelFlag() != null, RuleCmp::getDelFlag, query.getDelFlag());
                    lqw.eq(StringUtils.isNotBlank(query.getRemark()), RuleCmp::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(RuleCmp::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增规则组件
     *
     * @param add 规则组件
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(RuleCmp add) {
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
     * 修改规则组件
     *
     * @param update 规则组件
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleCmp", key = "#update.id")
    public Boolean updateWithCache(RuleCmp update) {
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
    private void validEntityBeforeSave(RuleCmp entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除规则组件信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleCmp", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    @Override
    public Boolean exec(RuleCmp ruleCmp) {
        return null;
    }

    @Override
    public Boolean options(Map<String, Object> map) {
        return null;
    }

    @Override
    public Boolean cmpOptions(Map<String, Object> map) {
        return null;
    }

    @Override
    public Boolean typeOptions(Map<String, Object> map) {
        return null;
    }

    @Override
    public Boolean scriptOptions(Map<String, Object> map) {
        return null;
    }

}
