package com.fastbee.scada.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.StringUtils;
import com.fastbee.scada.convert.ScadaDeviceBindConvert;
import com.fastbee.scada.domain.ScadaDeviceBind;
import com.fastbee.scada.mapper.ScadaDeviceBindMapper;
import com.fastbee.scada.service.IScadaDeviceBindService;
import com.fastbee.scada.vo.ScadaDeviceBindVO;

/**
 * 组态设备关联Service业务层处理
 *
 * @author kerwincui
 * @date 2023-11-13
 */
@Service
public class ScadaDeviceBindServiceImpl extends ServiceImpl<ScadaDeviceBindMapper,ScadaDeviceBind> implements IScadaDeviceBindService
{
    @Resource
    private ScadaDeviceBindMapper scadaDeviceBindMapper;

    /**
     * 查询组态设备关联
     *
     * @param id 组态设备关联主键
     * @return 组态设备关联
     */
    @Override
    public ScadaDeviceBind selectScadaDeviceBindById(Long id)
    {
        return baseMapper.selectById(id);
    }

    /**
     * 查询组态设备关联列表
     *
     * @param scadaDeviceBind 组态设备关联
     * @return 组态设备关联
     */
    @Override
    public List<ScadaDeviceBindVO> listScadaDeviceBindVO(ScadaDeviceBind scadaDeviceBind) {
        LambdaQueryWrapper<ScadaDeviceBind> lqw = buildQueryWrapper(scadaDeviceBind);
        List<ScadaDeviceBind> scadaDeviceBindList = baseMapper.selectList(lqw);
        return ScadaDeviceBindConvert.INSTANCE.convertScadaDeviceBindVOList(scadaDeviceBindList);
    }

    /**
     * 新增组态设备关联
     *
     * @param scadaDeviceBind 组态设备关联
     * @return 结果
     */
    @Override
    public int insertScadaDeviceBind(ScadaDeviceBind scadaDeviceBind)
    {
        return baseMapper.insert(scadaDeviceBind);
    }

    /**
     * 修改组态设备关联
     *
     * @param scadaDeviceBind 组态设备关联
     * @return 结果
     */
    @Override
    public int updateScadaDeviceBind(ScadaDeviceBind scadaDeviceBind)
    {
        return baseMapper.updateById(scadaDeviceBind);
    }

    /**
     * 批量删除组态设备关联
     *
     * @param ids 需要删除的组态设备关联主键
     * @return 结果
     */
    @Override
    public int deleteScadaDeviceBindByIds(Long[] ids)
    {
        return baseMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public List<ScadaDeviceBind> listByGuidAndSerialNumber(String scadaGuid, List<String> serialNumberList) {
        return scadaDeviceBindMapper.listByGuidAndSerialNumber(scadaGuid, serialNumberList);
    }

    private LambdaQueryWrapper<ScadaDeviceBind> buildQueryWrapper(ScadaDeviceBind query) {
        LambdaQueryWrapper<ScadaDeviceBind> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, ScadaDeviceBind::getId, query.getId());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), ScadaDeviceBind::getSerialNumber, query.getSerialNumber());
        lqw.eq(StringUtils.isNotBlank(query.getScadaGuid()), ScadaDeviceBind::getScadaGuid, query.getScadaGuid());
        return lqw;
    }

}
