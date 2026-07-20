package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.CardPlatformConvert;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.domain.CardPlatform;
import com.fastbee.iot.mapper.CardMapper;
import com.fastbee.iot.mapper.CardPlatformMapper;
import com.fastbee.iot.model.vo.CardPlatformVO;
import com.fastbee.iot.service.ICardPlatformService;

/**
 * 物联卡平台Service业务层处理
 *
 * @author fastbee
 * @date 2025-11-11
 */
@Service
public class CardPlatformServiceImpl extends ServiceImpl<CardPlatformMapper,CardPlatform> implements ICardPlatformService {

    @Resource
    private CardMapper cardMapper;

    /**
     * 查询物联卡平台
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param cardPlatform 卡平台
     * @return 物联卡平台
     */
    @Override
    @DataScope
    public CardPlatform queryByIdWithCache(CardPlatform cardPlatform){
        LambdaQueryWrapper<CardPlatform> queryWrapper = this.buildQueryWrapper(cardPlatform);
        return this.getOne(queryWrapper);
    }

    /**
     * 查询物联卡平台
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 物联卡平台
     */
    @Override
    @Cacheable(cacheNames = "CardPlatform", key = "#id")
    public CardPlatform selectCardPlatformById(Long id){
        return this.getById(id);
    }

    /**
     * 查询物联卡平台分页列表
     *
     * @param cardPlatform 物联卡平台
     * @return 物联卡平台
     */
    @Override
    @DataScope
    public Page<CardPlatformVO> pageCardPlatformVO(CardPlatform cardPlatform) {
        LambdaQueryWrapper<CardPlatform> lqw = buildQueryWrapper(cardPlatform);
        lqw.orderByDesc(CardPlatform::getCreateTime);
        Page<CardPlatform> cardPlatformPage = baseMapper.selectPage(new Page<>(cardPlatform.getPageNum(), cardPlatform.getPageSize()), lqw);
        return CardPlatformConvert.INSTANCE.convertCardPlatformVOPage(cardPlatformPage);
    }

    /**
     * 查询物联卡平台列表
     *
     * @param cardPlatform 物联卡平台
     * @return 物联卡平台
     */
    @Override
    public List<CardPlatformVO> listCardPlatformVO(CardPlatform cardPlatform) {
        LambdaQueryWrapper<CardPlatform> lqw = buildQueryWrapper(cardPlatform);
        List<CardPlatform> cardPlatformList = baseMapper.selectList(lqw);
        return CardPlatformConvert.INSTANCE.convertCardPlatformVOList(cardPlatformList);
    }

    private LambdaQueryWrapper<CardPlatform> buildQueryWrapper(CardPlatform query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<CardPlatform> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, CardPlatform::getId, query.getId());
                    lqw.like(StringUtils.isNotBlank(query.getName()), CardPlatform::getName, query.getName());
                    lqw.eq(StringUtils.isNotBlank(query.getPlatform()), CardPlatform::getPlatform, query.getPlatform());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(CardPlatform::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增物联卡平台
     *
     * @param add 物联卡平台
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(CardPlatform add) {
        SysUser sysUser = getLoginUser().getUser();
        // 归属为机构管理员
        add.setTenantId(sysUser.getDept().getDeptUserId());
        add.setTenantName(sysUser.getDept().getDeptName());
        add.setCreateBy(sysUser.getUserName());
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改物联卡平台
     *
     * @param update 物联卡平台
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "CardPlatform", key = "#update.id")
    public Boolean updateWithCache(CardPlatform update) {
        LoginUser loginUser = getLoginUser();
        update.setUpdateBy(loginUser.getUsername());
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CardPlatform entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除物联卡平台信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "CardPlatform", keyGenerator = "deleteKeyGenerator" )
    public AjaxResult deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Card::getCardPlatformId, Arrays.asList(ids));
        List<Card> cardList = cardMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(cardList)) {
            return AjaxResult.error("平台下还有SIM卡，请先删除SIM卡再进行操作！");
        }
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result ? AjaxResult.success() : AjaxResult.error();
    }

}
