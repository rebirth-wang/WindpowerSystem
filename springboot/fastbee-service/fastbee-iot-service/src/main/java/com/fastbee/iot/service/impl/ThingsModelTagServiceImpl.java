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
import com.fastbee.iot.convert.ThingsModelTagConvert;
import com.fastbee.iot.domain.ThingsModelTag;
import com.fastbee.iot.mapper.ThingsModelTagMapper;
import com.fastbee.iot.model.vo.ThingsModelTagVO;
import com.fastbee.iot.service.IThingsModelTagService;

/**
 * 物模型计算Service业务层处理
 *
 * @author fastbee
 * @date 2025-06-24
 */
@Service
public class ThingsModelTagServiceImpl extends ServiceImpl<ThingsModelTagMapper,ThingsModelTag> implements IThingsModelTagService {

    /**
     * 查询物模型计算
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 物模型计算
     */
    @Override
    @Cacheable(cacheNames = "ThingsModelTag", key = "#id")
    public ThingsModelTag queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询物模型计算
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 物模型计算
     */
    @Override
    @Cacheable(cacheNames = "ThingsModelTag", key = "#id")
    public ThingsModelTag selectThingsModelTagById(Long id){
        return this.getById(id);
    }

    /**
     * 查询物模型计算分页列表
     *
     * @param thingsModelTag 物模型计算
     * @return 物模型计算
     */
    @Override
    public Page<ThingsModelTagVO> pageThingsModelTagVO(ThingsModelTag thingsModelTag) {
        LambdaQueryWrapper<ThingsModelTag> lqw = buildQueryWrapper(thingsModelTag);
        Page<ThingsModelTag> thingsModelTagPage = baseMapper.selectPage(new Page<>(thingsModelTag.getPageNum(), thingsModelTag.getPageSize()), lqw);
        return ThingsModelTagConvert.INSTANCE.convertThingsModelTagVOPage(thingsModelTagPage);
    }

    /**
     * 查询物模型计算列表
     *
     * @param thingsModelTag 物模型计算
     * @return 物模型计算
     */
    @Override
    public List<ThingsModelTagVO> listThingsModelTagVO(ThingsModelTag thingsModelTag) {
        LambdaQueryWrapper<ThingsModelTag> lqw = buildQueryWrapper(thingsModelTag);
        List<ThingsModelTag> thingsModelTagList = baseMapper.selectList(lqw);
        return ThingsModelTagConvert.INSTANCE.convertThingsModelTagVOList(thingsModelTagList);
    }

    private LambdaQueryWrapper<ThingsModelTag> buildQueryWrapper(ThingsModelTag query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ThingsModelTag> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, ThingsModelTag::getId, query.getId());
                    lqw.eq(StringUtils.isNotBlank(query.getIdentifier()), ThingsModelTag::getIdentifier, query.getIdentifier());
                    lqw.eq(StringUtils.isNotBlank(query.getAlias()), ThingsModelTag::getAlias, query.getAlias());
                    lqw.eq(query.getModelId() != null, ThingsModelTag::getModelId, query.getModelId());
                    lqw.eq(query.getOperation() != null, ThingsModelTag::getOperation, query.getOperation());
                    lqw.eq(query.getSourceModelId() != null, ThingsModelTag::getSourceModelId, query.getSourceModelId());
                    lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), ThingsModelTag::getDelFlag, query.getDelFlag());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), ThingsModelTag::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, ThingsModelTag::getCreateTime, query.getCreateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), ThingsModelTag::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, ThingsModelTag::getUpdateTime, query.getUpdateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getRemark()), ThingsModelTag::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(ThingsModelTag::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增物模型计算
     *
     * @param add 物模型计算
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ThingsModelTag add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改物模型计算
     *
     * @param update 物模型计算
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "ThingsModelTag", key = "#update.id")
    public Boolean updateWithCache(ThingsModelTag update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ThingsModelTag entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除物模型计算信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "ThingsModelTag", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

}
