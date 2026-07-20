package com.fastbee.iot.service.impl;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.DateUtils;
import com.fastbee.iot.domain.SceneScript;
import com.fastbee.iot.mapper.SceneScriptMapper;
import com.fastbee.iot.service.ISceneScriptService;

/**
 * 场景脚本Service业务层处理
 *
 * @author kerwincui
 * @date 2023-12-27
 */
@Service
public class SceneScriptServiceImpl extends ServiceImpl<SceneScriptMapper,SceneScript> implements ISceneScriptService
{
    @Resource
    private SceneScriptMapper sceneScriptMapper;

    /**
     * 查询场景脚本
     *
     * @param scriptId 场景脚本主键
     * @return 场景脚本
     */
    @Override
    public SceneScript selectSceneScriptBySceneScriptId(String scriptId)
    {
        return sceneScriptMapper.selectById(scriptId);
    }

    /**
     * 查询场景脚本列表
     *
     * @param sceneScript 场景脚本
     * @return 场景脚本
     */
    @Override
    public List<SceneScript> selectSceneScriptList(SceneScript sceneScript)
    {
        return sceneScriptMapper.selectSceneScriptList(sceneScript);
    }

    /**
     * 新增场景脚本
     *
     * @param sceneScript 场景脚本
     * @return 结果
     */
    @Override
    public int insertSceneScript(SceneScript sceneScript)
    {
        sceneScript.setCreateTime(DateUtils.getNowDate());
        return sceneScriptMapper.insert(sceneScript);
    }

    /**
     * 修改场景脚本
     *
     * @param sceneScript 场景脚本
     * @return 结果
     */
    @Override
    public int updateSceneScript(SceneScript sceneScript)
    {
        return sceneScriptMapper.updateById(sceneScript);
    }

    /**
     * 批量删除场景脚本
     *
     * @param scriptIds 需要删除的场景脚本主键
     * @return 结果
     */
    @Override
    public int deleteSceneScriptBySceneScriptIds(String[] scriptIds)
    {
        return sceneScriptMapper.deleteBatchIds(Arrays.asList(scriptIds));
    }

    @Override
    public int deleteSceneScriptBySceneIds(Long[] sceneIds) {
        LambdaQueryWrapper<SceneScript> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SceneScript::getSceneId, Arrays.asList(sceneIds));
        return sceneScriptMapper.delete(queryWrapper);
    }

    @Override
    public int insertSceneScriptList(List<SceneScript> sceneScriptList) {
        return sceneScriptMapper.insertBatch(sceneScriptList) ? sceneScriptList.size() : 0;
    }

    @Override
    public List<SceneScript> listSceneScriptByPurpose(List<Long> sceneIdList, Integer scriptPurpose) {
        LambdaQueryWrapper<SceneScript> queryWrapper = new LambdaQueryWrapper<>();
        if (sceneIdList != null && !sceneIdList.isEmpty()) {
            queryWrapper.in(SceneScript::getSceneId, sceneIdList);
        }
        queryWrapper.eq(SceneScript::getScriptPurpose, scriptPurpose);
        return sceneScriptMapper.selectList(queryWrapper);
    }

    /**
     * 删除场景脚本信息
     *
     * @param scriptId 场景脚本主键
     * @return 结果
     */
    @Override
    public int deleteSceneScriptBySceneScriptId(String scriptId)
    {
        return sceneScriptMapper.deleteById(scriptId);
    }
}
