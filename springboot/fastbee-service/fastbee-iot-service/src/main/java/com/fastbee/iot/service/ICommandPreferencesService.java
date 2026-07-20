package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.fastbee.iot.domain.CommandPreferences;
import com.fastbee.iot.model.vo.CommandPreferencesVO;

/**
 * 指令偏好设置Service接口
 *
 * @author kerwincui
 * @date 2024-06-29
 */
public interface ICommandPreferencesService
{

    Page<CommandPreferencesVO> pageCommandPreferencesVO(CommandPreferences commandPreferences);

    /**
     * 查询指令偏好设置列表
     *
     * @param commandPreferences 指令偏好设置
     * @return 指令偏好设置集合
     */
    List<CommandPreferencesVO> selectCommandPreferencesVOList(CommandPreferences commandPreferences);

    /**
     * 查询指令偏好设置
     *
     * @param id 主键
     * @return 指令偏好设置
     */
    CommandPreferences queryByIdWithCache(Long id);

    /**
     * 新增指令偏好设置
     *
     * @param commandPreferences 指令偏好设置
     * @return 是否新增成功
     */
    Boolean insertWithCache(CommandPreferences commandPreferences);

    /**
     * 修改指令偏好设置
     *
     * @param commandPreferences 指令偏好设置
     * @return 是否修改成功
     */
    Boolean updateWithCache(CommandPreferences commandPreferences);

    /**
     * 校验并批量删除指令偏好设置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
}
