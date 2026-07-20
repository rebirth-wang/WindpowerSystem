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
import com.fastbee.iot.convert.SceneTagPointsConvert;
import com.fastbee.iot.domain.SceneTagPoints;
import com.fastbee.iot.mapper.SceneTagPointsMapper;
import com.fastbee.iot.model.vo.SceneTagPointsVO;
import com.fastbee.iot.service.ISceneTagPointsService;

/**
 * 运算型变量点Service业务层处理
 *
 * @author kerwincui
 * @date 2024-05-21
 */
@Service
public class SceneTagPointsServiceImpl extends ServiceImpl<SceneTagPointsMapper,SceneTagPoints> implements ISceneTagPointsService
{

    /**
     * 查询运算型变量点
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 运算型变量点
     */
    @Override
    @Cacheable(cacheNames = "SceneTagPoints", key = "#id")
    public SceneTagPoints queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询运算型变量点
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 运算型变量点
     */
    @Override
    @Cacheable(cacheNames = "SceneTagPoints", key = "#id")
    public SceneTagPoints selectSceneTagPointsById(Long id){
        return this.getById(id);
    }

    /**
     * 查询运算型变量点分页列表
     *
     * @param sceneTagPoints 运算型变量点
     * @return 运算型变量点
     */
    @Override
    public Page<SceneTagPointsVO> pageSceneTagPointsVO(SceneTagPoints sceneTagPoints) {
        LambdaQueryWrapper<SceneTagPoints> lqw = buildQueryWrapper(sceneTagPoints);
        Page<SceneTagPoints> sceneTagPointsPage = baseMapper.selectPage(new Page<>(sceneTagPoints.getPageNum(), sceneTagPoints.getPageSize()), lqw);
        return SceneTagPointsConvert.INSTANCE.convertSceneTagPointsVOPage(sceneTagPointsPage);
    }

    /**
     * 查询运算型变量点列表
     *
     * @param sceneTagPoints 运算型变量点
     * @return 运算型变量点
     */
    @Override
    public List<SceneTagPointsVO> listSceneTagPointsVO(SceneTagPoints sceneTagPoints) {
        LambdaQueryWrapper<SceneTagPoints> lqw = buildQueryWrapper(sceneTagPoints);
        List<SceneTagPoints> sceneTagPointsList = baseMapper.selectList(lqw);
        return SceneTagPointsConvert.INSTANCE.convertSceneTagPointsVOList(sceneTagPointsList);
    }

    private LambdaQueryWrapper<SceneTagPoints> buildQueryWrapper(SceneTagPoints query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SceneTagPoints> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, SceneTagPoints::getId, query.getId());
        lqw.like(StringUtils.isNotBlank(query.getName()), SceneTagPoints::getName, query.getName());
        lqw.eq(StringUtils.isNotBlank(query.getAlias()), SceneTagPoints::getAlias, query.getAlias());
        lqw.eq(query.getTagId() != null, SceneTagPoints::getTagId, query.getTagId());
        lqw.eq(query.getOperation() != null, SceneTagPoints::getOperation, query.getOperation());
        lqw.eq(query.getVariableType() != null, SceneTagPoints::getVariableType, query.getVariableType());
        lqw.eq(query.getSceneModelDataId() != null, SceneTagPoints::getSceneModelDataId, query.getSceneModelDataId());
        lqw.eq(StringUtils.isNotBlank(query.getDelFlag()), SceneTagPoints::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SceneTagPoints::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SceneTagPoints::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SceneTagPoints::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SceneTagPoints::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SceneTagPoints::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SceneTagPoints::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增运算型变量点
     *
     * @param add 运算型变量点
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(SceneTagPoints add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改运算型变量点
     *
     * @param update 运算型变量点
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "SceneTagPoints", key = "#update.id")
    public Boolean updateWithCache(SceneTagPoints update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SceneTagPoints entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除运算型变量点信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "SceneTagPoints", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }
}
