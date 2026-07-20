package com.fastbee.iot.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.ThingsModelTemplateTranslateConvert;
import com.fastbee.iot.domain.ThingsModelTemplateTranslate;
import com.fastbee.iot.mapper.ThingsModelTemplateTranslateMapper;
import com.fastbee.iot.model.vo.ThingsModelTemplateTranslateVO;
import com.fastbee.iot.service.IThingsModelTemplateTranslateService;

/**
 * 物模型模板翻译Service业务层处理
 *
 * @author fastbee
 * @date 2025-12-26
 */
@Service
public class ThingsModelTemplateTranslateServiceImpl extends ServiceImpl<ThingsModelTemplateTranslateMapper,ThingsModelTemplateTranslate> implements IThingsModelTemplateTranslateService {

    /**
     * 查询物模型模板翻译
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 物模型模板翻译
     */
    @Override
    @Cacheable(cacheNames = "ThingsModelTemplateTranslate", key = "#id")
    public ThingsModelTemplateTranslate queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询物模型模板翻译
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 物模型模板翻译
     */
    @Override
    @Cacheable(cacheNames = "ThingsModelTemplateTranslate", key = "#id")
    public ThingsModelTemplateTranslate selectThingsModelTemplateTranslateById(Long id){
        return this.getById(id);
    }

    /**
     * 查询物模型模板翻译分页列表
     *
     * @param thingsModelTemplateTranslate 物模型模板翻译
     * @return 物模型模板翻译
     */
    @Override
    public Page<ThingsModelTemplateTranslateVO> pageThingsModelTemplateTranslateVO(ThingsModelTemplateTranslate thingsModelTemplateTranslate) {
        LambdaQueryWrapper<ThingsModelTemplateTranslate> lqw = buildQueryWrapper(thingsModelTemplateTranslate);
        Page<ThingsModelTemplateTranslate> thingsModelTemplateTranslatePage = baseMapper.selectPage(new Page<>(thingsModelTemplateTranslate.getPageNum(), thingsModelTemplateTranslate.getPageSize()), lqw);
        return ThingsModelTemplateTranslateConvert.INSTANCE.convertThingsModelTemplateTranslateVOPage(thingsModelTemplateTranslatePage);
    }

    /**
     * 查询物模型模板翻译列表
     *
     * @param thingsModelTemplateTranslate 物模型模板翻译
     * @return 物模型模板翻译
     */
    @Override
    public List<ThingsModelTemplateTranslateVO> listThingsModelTemplateTranslateVO(ThingsModelTemplateTranslate thingsModelTemplateTranslate) {
        LambdaQueryWrapper<ThingsModelTemplateTranslate> lqw = buildQueryWrapper(thingsModelTemplateTranslate);
        List<ThingsModelTemplateTranslate> thingsModelTemplateTranslateList = baseMapper.selectList(lqw);
        return ThingsModelTemplateTranslateConvert.INSTANCE.convertThingsModelTemplateTranslateVOList(thingsModelTemplateTranslateList);
    }

    private LambdaQueryWrapper<ThingsModelTemplateTranslate> buildQueryWrapper(ThingsModelTemplateTranslate query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ThingsModelTemplateTranslate> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, ThingsModelTemplateTranslate::getId, query.getId());
        lqw.eq(StringUtils.isNotBlank(query.getZhCn()), ThingsModelTemplateTranslate::getZhCn, query.getZhCn());
        lqw.eq(StringUtils.isNotBlank(query.getEnUs()), ThingsModelTemplateTranslate::getEnUs, query.getEnUs());

        return lqw;
    }

    /**
     * 新增物模型模板翻译
     *
     * @param add 物模型模板翻译
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ThingsModelTemplateTranslate add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改物模型模板翻译
     *
     * @param update 物模型模板翻译
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "ThingsModelTemplateTranslate", key = "#update.id")
    public Boolean updateWithCache(ThingsModelTemplateTranslate update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ThingsModelTemplateTranslate entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除物模型模板翻译信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "ThingsModelTemplateTranslate", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

}
