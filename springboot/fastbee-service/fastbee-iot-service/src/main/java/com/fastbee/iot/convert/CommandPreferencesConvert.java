package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.CommandPreferences;
import com.fastbee.iot.model.vo.CommandPreferencesVO;

/**
 * 指令偏好设置Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */
@Mapper
public interface CommandPreferencesConvert
{

    CommandPreferencesConvert INSTANCE = Mappers.getMapper(CommandPreferencesConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param commandPreferences
     * @return 指令偏好设置集合
     */
    CommandPreferencesVO convertCommandPreferencesVO(CommandPreferences commandPreferences);

    /**
     * VO类转换为实体类集合
     *
     * @param commandPreferencesVO
     * @return 指令偏好设置集合
     */
    CommandPreferences convertCommandPreferences(CommandPreferencesVO commandPreferencesVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param commandPreferencesList
     * @return 指令偏好设置集合
     */
    List<CommandPreferencesVO> convertCommandPreferencesVOList(List<CommandPreferences> commandPreferencesList);

    /**
     * VO类转换为实体类
     *
     * @param commandPreferencesVOList
     * @return 指令偏好设置集合
     */
    List<CommandPreferences> convertCommandPreferencesList(List<CommandPreferencesVO> commandPreferencesVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param CommandPreferencesPage
     * @return 数据桥接分页
     */
    Page<CommandPreferencesVO> convertCommandPreferencesVOPage(Page<CommandPreferences> CommandPreferencesPage);

    /**
     * VO类转换为实体类
     *
     * @param CommandPreferencesVOPage
     * @return 数据桥接分页
     */
    Page<CommandPreferences> convertCommandPreferencesPage(Page<CommandPreferencesVO> CommandPreferencesVOPage);
}
