package com.fastbee.scada.service.impl;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.StringUtils;
import com.fastbee.scada.domain.ScadaDeviceShare;
import com.fastbee.scada.mapper.ScadaDeviceShareMapper;
import com.fastbee.scada.service.IScadaDeviceShareService;
import com.fastbee.scada.vo.ScadaShareVO;

/**
 * shareService业务层处理
 *
 * @author zhuangpeng.li
 * @date 2024-08-20
 */
@Service
public class ScadaDeviceShareServiceImpl extends ServiceImpl<ScadaDeviceShareMapper, ScadaDeviceShare> implements IScadaDeviceShareService {

    /**
     * 查询share
     *
     * @param id 主键
     * @return share
     */
    @Override
    public ScadaDeviceShare queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询share列表
     *
     * @param scadaDeviceShare share
     * @return share
     */
    @Override
    public List<ScadaDeviceShare> selectScadaDeviceShareList(ScadaDeviceShare scadaDeviceShare) {
        LambdaQueryWrapper<ScadaDeviceShare> lqw = buildQueryWrapper(scadaDeviceShare);
        return baseMapper.selectList(lqw);
    }

    private LambdaQueryWrapper<ScadaDeviceShare> buildQueryWrapper(ScadaDeviceShare query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ScadaDeviceShare> lqw = Wrappers.lambdaQuery();
                    lqw.eq(StringUtils.isNotBlank(query.getGuid()), ScadaDeviceShare::getGuid, query.getGuid());
                    lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), ScadaDeviceShare::getSerialNumber, query.getSerialNumber());
                    lqw.eq(query.getIsShare() != null, ScadaDeviceShare::getIsShare, query.getIsShare());
                    lqw.eq(StringUtils.isNotBlank(query.getShareUrl()), ScadaDeviceShare::getShareUrl, query.getShareUrl());
                    lqw.eq(StringUtils.isNotBlank(query.getSharePass()), ScadaDeviceShare::getSharePass, query.getSharePass());
        return lqw;
    }

    /**
     * 新增share
     *
     * @param add share
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ScadaDeviceShare add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改share
     *
     * @param update share
     * @return 是否修改成功
     */
    @Override
//    @CacheEvict(cacheNames = "sql_cache:ScadaDeviceShare", key = "#update.id")
    public Boolean updateWithCache(ScadaDeviceShare update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ScadaDeviceShare entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除share信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "sql_cache:ScadaDeviceShare", keyGenerator = "DeleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return this.removeById(ids);
    }

    @Override
    public ScadaDeviceShare getByGuidAndSerialNumber(ScadaShareVO scadaShareVO) {
        LambdaQueryWrapper<ScadaDeviceShare> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScadaDeviceShare::getGuid, scadaShareVO.getGuid());
        queryWrapper.eq(ScadaDeviceShare::getSerialNumber, scadaShareVO.getSerialNumber());
        return this.getOne(queryWrapper);
    }

    @Override
    public void updateOneById(ScadaDeviceShare editScadaDeviceShare) {
        LambdaUpdateWrapper<ScadaDeviceShare> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ScadaDeviceShare::getId, editScadaDeviceShare.getId());
        updateWrapper.set(ScadaDeviceShare::getIsShare, editScadaDeviceShare.getIsShare());
        updateWrapper.set(ScadaDeviceShare::getShareUrl, editScadaDeviceShare.getShareUrl());
        updateWrapper.set(ScadaDeviceShare::getSharePass, editScadaDeviceShare.getSharePass());
        this.update(updateWrapper);
    }

}
