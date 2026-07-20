package com.fastbee.onvif.service.impl;

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
import com.fastbee.onvif.convert.OnvifPlatformConvert;
import com.fastbee.onvif.domain.OnvifPlatform;
import com.fastbee.onvif.domain.vo.OnvifPlatformVO;
import com.fastbee.onvif.mapper.OnvifPlatformMapper;
import com.fastbee.onvif.service.IOnvifPlatformService;

/**
 * onvif平台Service业务层处理
 *
 * @author fastbee
 * @date 2026-01-06
 */
@Service
public class OnvifPlatformServiceImpl extends ServiceImpl<OnvifPlatformMapper, OnvifPlatform> implements IOnvifPlatformService {

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询onvif平台
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return onvif平台
     */
    @Override
    @Cacheable(cacheNames = "OnvifPlatform", key = "#id")
    public OnvifPlatform queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询onvif平台
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return onvif平台
     */
    @Override
    @Cacheable(cacheNames = "OnvifPlatform", key = "#id")
    public OnvifPlatform selectOnvifPlatformById(Long id){
        return this.getById(id);
    }

    /**
     * 查询onvif平台分页列表
     *
     * @param onvifPlatform onvif平台
     * @return onvif平台
     */
    @Override
    public Page<OnvifPlatformVO> pageOnvifPlatformVO(OnvifPlatform onvifPlatform) {
        LambdaQueryWrapper<OnvifPlatform> lqw = buildQueryWrapper(onvifPlatform);
        Page<OnvifPlatform> onvifPlatformPage = baseMapper.selectPage(new Page<>(onvifPlatform.getPageNum(), onvifPlatform.getPageSize()), lqw);
        return OnvifPlatformConvert.INSTANCE.convertOnvifPlatformVOPage(onvifPlatformPage);
    }

    /**
     * 查询onvif平台列表
     *
     * @param onvifPlatform onvif平台
     * @return onvif平台
     */
    @Override
    public List<OnvifPlatformVO> listOnvifPlatformVO(OnvifPlatform onvifPlatform) {
        LambdaQueryWrapper<OnvifPlatform> lqw = buildQueryWrapper(onvifPlatform);
        List<OnvifPlatform> onvifPlatformList = baseMapper.selectList(lqw);
        return OnvifPlatformConvert.INSTANCE.convertOnvifPlatformVOList(onvifPlatformList);
    }

    private LambdaQueryWrapper<OnvifPlatform> buildQueryWrapper(OnvifPlatform query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<OnvifPlatform> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, OnvifPlatform::getId, query.getId());
                    lqw.eq(query.getChannelId() != null, OnvifPlatform::getChannelId, query.getChannelId());
                    lqw.eq(StringUtils.isNotBlank(query.getPlatformId()), OnvifPlatform::getPlatformId, query.getPlatformId());
                    lqw.eq(StringUtils.isNotBlank(query.getCatalogId()), OnvifPlatform::getCatalogId, query.getCatalogId());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), OnvifPlatform::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, OnvifPlatform::getCreateTime, query.getCreateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), OnvifPlatform::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, OnvifPlatform::getUpdateTime, query.getUpdateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getRemark()), OnvifPlatform::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(OnvifPlatform::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增onvif平台
     *
     * @param add onvif平台
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(OnvifPlatform add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改onvif平台
     *
     * @param update onvif平台
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "OnvifPlatform", key = "#update.id")
    public Boolean updateWithCache(OnvifPlatform update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(OnvifPlatform entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除onvif平台信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "OnvifPlatform", keyGenerator = "deleteKeyGenerator" )
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
