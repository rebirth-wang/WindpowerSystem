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
import com.fastbee.iot.service.IRuleScriptNodeService;
import com.fastbee.rule.convert.RuleScriptNodeConvert;
import com.fastbee.rule.domain.RuleScriptNode;
import com.fastbee.rule.domain.vo.RuleScriptNodeVO;
import com.fastbee.rule.mapper.RuleScriptNodeMapper;

/**
 * 规则脚本节点Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@Service
public class RuleScriptNodeServiceImpl extends ServiceImpl<RuleScriptNodeMapper,RuleScriptNode> implements IRuleScriptNodeService {

    /**
     * 查询规则脚本节点
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 规则脚本节点
     */
    @Override
    @Cacheable(cacheNames = "RuleScriptNode", key = "#id")
    public RuleScriptNode queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询规则脚本节点
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 规则脚本节点
     */
    @Override
    @Cacheable(cacheNames = "RuleScriptNode", key = "#id")
    public RuleScriptNode selectRuleScriptNodeById(Long id){
        return this.getById(id);
    }

    /**
     * 查询规则脚本节点分页列表
     *
     * @param ruleScriptNode 规则脚本节点
     * @return 规则脚本节点
     */
    @Override
    public Page<RuleScriptNodeVO> pageRuleScriptNodeVO(RuleScriptNode ruleScriptNode) {
        LambdaQueryWrapper<RuleScriptNode> lqw = buildQueryWrapper(ruleScriptNode);
        Page<RuleScriptNode> ruleScriptNodePage = baseMapper.selectPage(new Page<>(ruleScriptNode.getPageNum(), ruleScriptNode.getPageSize()), lqw);
        return RuleScriptNodeConvert.INSTANCE.convertRuleScriptNodeVOPage(ruleScriptNodePage);
    }

    /**
     * 查询规则脚本节点列表
     *
     * @param ruleScriptNode 规则脚本节点
     * @return 规则脚本节点
     */
    @Override
    @DataScope()
    public List<RuleScriptNodeVO> listRuleScriptNodeVO(RuleScriptNode ruleScriptNode) {
        LambdaQueryWrapper<RuleScriptNode> lqw = buildQueryWrapper(ruleScriptNode);
        List<RuleScriptNode> ruleScriptNodeList = baseMapper.selectList(lqw);
        return RuleScriptNodeConvert.INSTANCE.convertRuleScriptNodeVOList(ruleScriptNodeList);
    }

    private LambdaQueryWrapper<RuleScriptNode> buildQueryWrapper(RuleScriptNode query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<RuleScriptNode> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getTenantId() != null, RuleScriptNode::getTenantId, query.getTenantId());
                    lqw.eq(query.getId() != null, RuleScriptNode::getId, query.getId());
                    lqw.like(StringUtils.isNotBlank(query.getApplicationName()), RuleScriptNode::getApplicationName, query.getApplicationName());
                    lqw.eq(StringUtils.isNotBlank(query.getScriptNodeId()), RuleScriptNode::getScriptNodeId, query.getScriptNodeId());
                    lqw.like(StringUtils.isNotBlank(query.getScriptNodeName()), RuleScriptNode::getScriptNodeName, query.getScriptNodeName());
                    lqw.eq(StringUtils.isNotBlank(query.getScriptNodeType()), RuleScriptNode::getScriptNodeType, query.getScriptNodeType());
                    lqw.eq(StringUtils.isNotBlank(query.getScriptNodeData()), RuleScriptNode::getScriptNodeData, query.getScriptNodeData());
                    lqw.eq(StringUtils.isNotBlank(query.getScriptLanguage()), RuleScriptNode::getScriptLanguage, query.getScriptLanguage());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), RuleScriptNode::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, RuleScriptNode::getCreateTime, query.getCreateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), RuleScriptNode::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, RuleScriptNode::getUpdateTime, query.getUpdateTime());
                    lqw.eq(query.getDelFlag() != null, RuleScriptNode::getDelFlag, query.getDelFlag());
                    lqw.eq(StringUtils.isNotBlank(query.getRemark()), RuleScriptNode::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(RuleScriptNode::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增规则脚本节点
     *
     * @param add 规则脚本节点
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(RuleScriptNode add) {
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
     * 修改规则脚本节点
     *
     * @param update 规则脚本节点
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleScriptNode", key = "#update.id")
    public Boolean updateWithCache(RuleScriptNode update) {
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
    private void validEntityBeforeSave(RuleScriptNode entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除规则脚本节点信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "RuleScriptNode", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

}
