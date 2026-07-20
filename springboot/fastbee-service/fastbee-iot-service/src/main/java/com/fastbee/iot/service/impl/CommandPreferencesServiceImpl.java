package com.fastbee.iot.service.impl;

import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.CommandPreferencesConvert;
import com.fastbee.iot.domain.CommandPreferences;
import com.fastbee.iot.mapper.CommandPreferencesMapper;
import com.fastbee.iot.model.vo.CommandPreferencesVO;
import com.fastbee.iot.service.ICommandPreferencesService;

/**
 * 指令偏好设置Service业务层处理
 *
 * @author kerwincui
 * @date 2024-06-29
 */
@Service
public class CommandPreferencesServiceImpl extends ServiceImpl<CommandPreferencesMapper,CommandPreferences> implements ICommandPreferencesService
{

    /**
     * 查询指令偏好设置
     *
     * @param id 主键
     * @return 指令偏好设置
     */
    @Override
    @Cacheable(cacheNames = "CommandPreferences", key = "#id")
    // 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
    public CommandPreferences queryByIdWithCache(Long id){
        CommandPreferences commandPreferences = this.getById(id);
        JSONObject jsonObject = JSONObject.parseObject(commandPreferences.getCommand());
        String command = jsonObject.getString("command");
        commandPreferences.setCommand(command);
        return commandPreferences;
    }

    @Override
    public Page<CommandPreferencesVO> pageCommandPreferencesVO(CommandPreferences commandPreferences) {
        LambdaQueryWrapper<CommandPreferences> lqw = buildQueryWrapper(commandPreferences);
        Page<CommandPreferences> commandPreferencesPage = baseMapper.selectPage(new Page<>(commandPreferences.getPageNum(), commandPreferences.getPageSize()), lqw);
        for (CommandPreferences preferences : commandPreferencesPage.getRecords()) {
            JSONObject jsonObject = JSONObject.parseObject(preferences.getCommand());
            String command = jsonObject.getString("command");
            preferences.setCommand(command);
        }
        return CommandPreferencesConvert.INSTANCE.convertCommandPreferencesVOPage(commandPreferencesPage);
    }

    /**
     * 查询指令偏好设置列表
     *
     * @param commandPreferences 指令偏好设置
     * @return 指令偏好设置
     */
    @Override
    public List<CommandPreferencesVO> selectCommandPreferencesVOList(CommandPreferences commandPreferences)
    {
        LambdaQueryWrapper<CommandPreferences> lqw = buildQueryWrapper(commandPreferences);
        List<CommandPreferences> commandPreferencesList = baseMapper.selectList(lqw);
        for (CommandPreferences preferences : commandPreferencesList) {
            JSONObject jsonObject = JSONObject.parseObject(preferences.getCommand());
            String command = jsonObject.getString("command");
            preferences.setCommand(command);
        }
        return CommandPreferencesConvert.INSTANCE.convertCommandPreferencesVOList(commandPreferencesList);
    }

    private LambdaQueryWrapper<CommandPreferences> buildQueryWrapper(CommandPreferences query) {
        LambdaQueryWrapper<CommandPreferences> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(query.getName()), CommandPreferences::getName, query.getName());
        lqw.eq(StringUtils.isNotBlank(query.getCommand()), CommandPreferences::getCommand, query.getCommand());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), CommandPreferences::getSerialNumber, query.getSerialNumber());
        return lqw;
    }

    /**
     * 新增指令偏好设置
     *
     * @param commandPreferences 指令偏好设置
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(CommandPreferences commandPreferences) {
        validEntityBeforeSave(commandPreferences);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command",commandPreferences.getCommand());
        commandPreferences.setCommand(JSONObject.toJSONString(jsonObject));
        return this.save(commandPreferences);
    }

    /**
     * 修改指令偏好设置
     *
     * @param commandPreferences 指令偏好设置
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "CommandPreferences", key = "#update.id")
    public Boolean updateWithCache(CommandPreferences commandPreferences) {
        validEntityBeforeSave(commandPreferences);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("command",commandPreferences.getCommand());
        commandPreferences.setCommand(JSONObject.toJSONString(jsonObject));
        return this.updateById(commandPreferences);
    }

    /**
     * 校验并批量删除指令偏好设置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "CommandPreferences", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CommandPreferences entity){
        //TODO 做一些数据校验,如唯一约束
    }
}
