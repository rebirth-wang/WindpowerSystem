package com.fastbee.onvif.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.onvif.domain.OnvifPlatform;
import com.fastbee.onvif.domain.vo.OnvifPlatformVO;

/**
 * onvif平台Service接口
 *
 * @author fastbee
 * @date 2026-01-06
 */
public interface IOnvifPlatformService extends IService<OnvifPlatform>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询onvif平台列表
     *
     * @param onvifPlatform onvif平台
     * @return onvif平台分页集合
     */
    Page<OnvifPlatformVO> pageOnvifPlatformVO(OnvifPlatform onvifPlatform);

    /**
     * 查询onvif平台列表
     *
     * @param onvifPlatform onvif平台
     * @return onvif平台集合
     */
    List<OnvifPlatformVO> listOnvifPlatformVO(OnvifPlatform onvifPlatform);

    /**
     * 查询onvif平台
     *
     * @param id 主键
     * @return onvif平台
     */
     OnvifPlatform selectOnvifPlatformById(Long id);

    /**
     * 查询onvif平台
     *
     * @param id 主键
     * @return onvif平台
     */
    OnvifPlatform queryByIdWithCache(Long id);

    /**
     * 新增onvif平台
     *
     * @param onvifPlatform onvif平台
     * @return 是否新增成功
     */
    Boolean insertWithCache(OnvifPlatform onvifPlatform);

    /**
     * 修改onvif平台
     *
     * @param onvifPlatform onvif平台
     * @return 是否修改成功
     */
    Boolean updateWithCache(OnvifPlatform onvifPlatform);

    /**
     * 校验并批量删除onvif平台信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/

}
