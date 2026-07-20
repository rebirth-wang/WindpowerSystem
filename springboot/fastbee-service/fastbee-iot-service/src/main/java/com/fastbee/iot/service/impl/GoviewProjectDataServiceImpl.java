package com.fastbee.iot.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.uuid.IdUtils;
import com.fastbee.iot.domain.GoviewProjectData;
import com.fastbee.iot.mapper.GoviewProjectDataMapper;
import com.fastbee.iot.service.IGoviewProjectDataService;

/**
 * 项目数据关联Service业务层处理
 *
 * @author kami
 * @date 2022-10-27
 */
@Service
public class GoviewProjectDataServiceImpl extends ServiceImpl<GoviewProjectDataMapper,GoviewProjectData> implements IGoviewProjectDataService
{
    private static final Pattern PATTERN = Pattern.compile("^(?i)(\\s*)(select)(\\s+)(((?!(insert|delete|update)).)+)$");


    @Resource
    private GoviewProjectDataMapper goviewProjectDataMapper;

    /**
     * 查询项目数据关联
     *
     * @param id 项目数据关联主键
     * @return 项目数据关联
     */
    @Override
    public GoviewProjectData selectGoviewProjectDataById(String id)
    {
        return goviewProjectDataMapper.selectById(id);
    }

    /**
     * 查询项目数据关联列表
     *
     * @param goviewProjectData 项目数据关联
     * @return 项目数据关联
     */
    @Override
    public List<GoviewProjectData> selectGoviewProjectDataList(GoviewProjectData goviewProjectData)
    {
        return goviewProjectDataMapper.selectGoviewProjectDataList(goviewProjectData);
    }

    /**
     * 新增项目数据关联
     *
     * @param goviewProjectData 项目数据关联
     * @return 结果
     */
    @Override
    public int insertGoviewProjectData(GoviewProjectData goviewProjectData)
    {
        goviewProjectData.setId(IdUtils.simpleUUID());
        goviewProjectData.setCreateBy(SecurityUtils.getUserId().toString());
        goviewProjectData.setCreateTime(DateUtils.getNowDate());
        goviewProjectData.setUpdateTime(DateUtils.getNowDate());
        return goviewProjectDataMapper.insert(goviewProjectData);
    }

    /**
     * 修改项目数据关联
     *
     * @param goviewProjectData 项目数据关联
     * @return 结果
     */
    @Override
    public int updateGoviewProjectData(GoviewProjectData goviewProjectData)
    {
        goviewProjectData.setUpdateTime(DateUtils.getNowDate());
        return goviewProjectDataMapper.updateById(goviewProjectData);
    }

    /**
     * 批量删除项目数据关联
     *
     * @param ids 需要删除的项目数据关联主键
     * @return 结果
     */
    @Override
    public int deleteGoviewProjectDataByIds(String[] ids)
    {
        return goviewProjectDataMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除项目数据关联信息
     *
     * @param id 项目数据关联主键
     * @return 结果
     */
    @Override
    public int deleteGoviewProjectDataById(String id)
    {
        return goviewProjectDataMapper.deleteById(id);
    }

    @Override
    public GoviewProjectData selectGoviewProjectDataByProjectId(String projectId) {
        LambdaQueryWrapper<GoviewProjectData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoviewProjectData::getProjectId, projectId);
        return goviewProjectDataMapper.selectOne(queryWrapper);
    }

	@Override
	public int insertOrUpdateGoviewProjectData(GoviewProjectData data) {
        int i = 0;
        LambdaQueryWrapper<GoviewProjectData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoviewProjectData::getProjectId, data.getProjectId());
        GoviewProjectData goviewProjectData = goviewProjectDataMapper.selectOne(queryWrapper);
        if (goviewProjectData == null){
            i = insertGoviewProjectData(data);
        }else {
            data.setId(goviewProjectData.getId());
            i = updateGoviewProjectData(data);
        }
        return i;
	}

    @Override
    public AjaxResult executeSql(String sql) {
        // 校验sql todo
        Matcher matcher = PATTERN.matcher(sql);
        if (!matcher.matches()) {
            return AjaxResult.error(MessageUtils.message("only.allow.select.operate"));
        }
        return AjaxResult.success(goviewProjectDataMapper.executeSql(sql));
    }
}
