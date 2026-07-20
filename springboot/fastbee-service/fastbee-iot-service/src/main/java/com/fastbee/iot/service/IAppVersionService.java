package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.AppVersion;
import com.fastbee.iot.model.vo.AppVersionVO;

/**
 * APP版本Service接口
 *
 * @author fastbee
 * @date 2025-08-11
 */
public interface IAppVersionService extends IService<AppVersion>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询APP版本列表
     *
     * @param appVersion APP版本
     * @return APP版本分页集合
     */
    Page<AppVersionVO> pageAppVersionVO(AppVersion appVersion);

    /**
     * 查询APP版本列表
     *
     * @param appVersion APP版本
     * @return APP版本集合
     */
    List<AppVersionVO> listAppVersionVO(AppVersion appVersion);

    /**
     * 查询APP版本
     *
     * @param id 主键
     * @return APP版本
     */
     AppVersion selectAppVersionById(Long id);

    /**
     * 查询APP版本
     *
     * @param id 主键
     * @return APP版本
     */
    AppVersion queryByIdWithCache(Long id);

    /**
     * 新增APP版本
     *
     * @param appVersion APP版本
     * @return 是否新增成功
     */
    Boolean insertWithCache(AppVersion appVersion);

    /**
     * 修改APP版本
     *
     * @param appVersion APP版本
     * @return 是否修改成功
     */
    Boolean updateWithCache(AppVersion appVersion);

    /**
     * 校验并批量删除APP版本信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);

    /**
     * 查询最新版本的APP版本信息
     *
     * @return 最新版本的APP版本信息
     */
    AppVersion selectLatestAppVersion(AppVersion appVersion);

    AppVersion selectLatestApkVersion(AppVersion appVersion);
}
