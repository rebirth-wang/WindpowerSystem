package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

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

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.AppVersionConvert;
import com.fastbee.iot.domain.AppVersion;
import com.fastbee.iot.mapper.AppVersionMapper;
import com.fastbee.iot.model.vo.AppVersionVO;
import com.fastbee.iot.service.IAppVersionService;

/**
 * APP版本Service业务层处理
 *
 * @author fastbee
 * @date 2025-08-11
 */
@Service
public class AppVersionServiceImpl extends ServiceImpl<AppVersionMapper,AppVersion> implements IAppVersionService {

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询APP版本
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return APP版本
     */
    @Override
    @Cacheable(cacheNames = "AppVersion", key = "#id")
    public AppVersion queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询APP版本
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return APP版本
     */
    @Override
    @Cacheable(cacheNames = "AppVersion", key = "#id")
    public AppVersion selectAppVersionById(Long id){
        return this.getById(id);
    }

    /**
     * 查询APP版本分页列表
     *
     * @param appVersion APP版本
     * @return APP版本
     */
    @Override
    public Page<AppVersionVO> pageAppVersionVO(AppVersion appVersion) {
        LambdaQueryWrapper<AppVersion> lqw = buildQueryWrapper(appVersion);
        lqw.orderByDesc(AppVersion::getCreateTime);
        Page<AppVersion> appVersionPage = baseMapper.selectPage(new Page<>(appVersion.getPageNum(), appVersion.getPageSize()), lqw);
        return AppVersionConvert.INSTANCE.convertAppVersionVOPage(appVersionPage);
    }

    /**
     * 查询APP版本列表
     *
     * @param appVersion APP版本
     * @return APP版本
     */
    @Override
    public List<AppVersionVO> listAppVersionVO(AppVersion appVersion) {
        LambdaQueryWrapper<AppVersion> lqw = buildQueryWrapper(appVersion);
        List<AppVersion> appVersionList = baseMapper.selectList(lqw);
        return AppVersionConvert.INSTANCE.convertAppVersionVOList(appVersionList);
    }

    private LambdaQueryWrapper<AppVersion> buildQueryWrapper(AppVersion query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<AppVersion> lqw = Wrappers.lambdaQuery();
                    lqw.eq(StringUtils.isNotBlank(query.getVersion()), AppVersion::getVersion, query.getVersion());
                    lqw.like(StringUtils.isNotBlank(query.getVersionName()), AppVersion::getVersionName, query.getVersionName());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(AppVersion::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        return lqw;
    }

    /**
     * 新增APP版本
     *
     * @param add APP版本
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(AppVersion add) {
        // 检查版本号是否重复
        LambdaQueryWrapper<AppVersion> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(AppVersion::getVersion, add.getVersion());
        if (this.count(queryWrapper) > 0) {
            throw new RuntimeException("版本号已存在，请使用其他版本号");
        }
        SysUser sysUser = getLoginUser().getUser();
        add.setCreateTime(DateUtils.getNowDate());
        add.setCreateBy(sysUser.getUserName());
        validEntityBeforeSave(add);

        return this.save(add);
    }

    /**
     * 修改APP版本
     *
     * @param update APP版本
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "AppVersion", key = "#update.id")
    public Boolean updateWithCache(AppVersion update) {
        SysUser sysUser = getLoginUser().getUser();
        update.setUpdateTime(DateUtils.getNowDate());
        update.setUpdateBy(sysUser.getUserName());
        validEntityBeforeSave(update);

        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AppVersion entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除APP版本信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "AppVersion", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

    /**
     * 查询最新版本的APP版本信息
     *
     * @return 最新版本的APP版本信息
     */
    @Override
    public AppVersion selectLatestAppVersion(AppVersion appVersion) {
        LambdaQueryWrapper<AppVersion> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.orderByDesc(AppVersion::getVersion);
        queryWrapper.last("limit 1");
        return this.getOne(queryWrapper);
    }

    /**
     * 查询最新APK版本的APP版本信息
     *
     * @param appVersion APP版本查询条件
     * @return 最新APK版本的APP版本信息
     */
    @Override
    public AppVersion selectLatestApkVersion(AppVersion appVersion) {
        LambdaQueryWrapper<AppVersion> queryWrapper = Wrappers.lambdaQuery();
        // 添加APK版本筛选条件：apk字段不为空
        queryWrapper.isNotNull(AppVersion::getApk);
        queryWrapper.orderByDesc(AppVersion::getVersion);
        queryWrapper.last("limit 1");
        return this.getOne(queryWrapper);
    }
}
