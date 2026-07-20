package com.fastbee.iot.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.ProtocolConvert;
import com.fastbee.iot.domain.Protocol;
import com.fastbee.iot.mapper.ProtocolMapper;
import com.fastbee.iot.model.vo.ProtocolVO;
import com.fastbee.iot.service.IProtocolService;

/**
 * 协议管理
 * @author gsb
 * @date 2022/10/19 17:02
 */
@Service
public class ProtocolServiceImpl extends ServiceImpl<ProtocolMapper,Protocol> implements IProtocolService {

    @Resource
    private ProtocolMapper protocolMapper;

    /**
     * 查询协议
     *
     * @param id 协议主键
     * @return 协议
     */
    @Override
    public Protocol selectProtocolById(Long id)
    {
        Protocol protocol = protocolMapper.selectById(id);
        if (null != protocol && null == protocol.getDataFormat()) {
            protocol.setDataFormat("");
        }
        return protocol;
    }

    /**
     * 查询协议分页列表
     *
     * @param protocol 协议
     * @return 协议
     */
    @Override
    public Page<ProtocolVO> pageProtocolVO(Protocol protocol) {
        LambdaQueryWrapper<Protocol> lqw = buildQueryWrapper(protocol);
        Page<Protocol> protocolPage = baseMapper.selectPage(new Page<>(protocol.getPageNum(), protocol.getPageSize()), lqw);
        return ProtocolConvert.INSTANCE.convertProtocolVOPage(protocolPage);
    }

    /**
     * 查询协议列表
     *
     * @param protocol 协议
     * @return 协议
     */
    @Override
    public List<ProtocolVO> listProtocolVO(Protocol protocol) {
        LambdaQueryWrapper<Protocol> lqw = buildQueryWrapper(protocol);
        List<Protocol> protocolList = baseMapper.selectList(lqw);
        return ProtocolConvert.INSTANCE.convertProtocolVOList(protocolList);
    }

    /**
     * 新增协议
     *
     * @param protocol 协议
     * @return 结果
     */
    @Override
    public int insertProtocol(Protocol protocol)
    {
        protocol.setCreateTime(DateUtils.getNowDate());
        if (protocol.getProtocolStatus() == null){
            protocol.setProtocolStatus(1);
        }
        return protocolMapper.insert(protocol);
    }

    /**
     * 修改协议
     *
     * @param protocol 协议
     * @return 结果
     */
    @Override
    public int updateProtocol(Protocol protocol)
    {
        protocol.setUpdateTime(DateUtils.getNowDate());
        return protocolMapper.updateById(protocol);
    }

    /**
     * 批量删除协议
     *
     * @param ids 需要删除的协议主键
     * @return 结果
     */
    @Override
    public int deleteProtocolByIds(Long[] ids)
    {
        return protocolMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除协议信息
     *
     * @param id 协议主键
     * @return 结果
     */
    @Override
    public int deleteProtocolById(Long id)
    {
        return protocolMapper.deleteById(id);
    }

    /**
     * 获取所有协议
     * @return
     */
    @Override
    public List<Protocol> selectAll(){
        return protocolMapper.selectAll(1,0);
    }

    /**
     *获取所有可用协议
     * @param protocol
     * @return
     */
    @Override
    public List<Protocol> selectByCondition(Protocol protocol){
        return protocolMapper.selectByUnion(protocol);
    }

    private LambdaQueryWrapper<Protocol> buildQueryWrapper(Protocol query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<Protocol> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, Protocol::getId, query.getId());
        lqw.eq(StringUtils.isNotBlank(query.getProtocolCode()), Protocol::getProtocolCode, query.getProtocolCode());
        lqw.like(StringUtils.isNotBlank(query.getProtocolName()), Protocol::getProtocolName, query.getProtocolName());
        lqw.eq(StringUtils.isNotBlank(query.getProtocolFileUrl()), Protocol::getProtocolFileUrl, query.getProtocolFileUrl());
        lqw.eq(query.getProtocolType() != null, Protocol::getProtocolType, query.getProtocolType());
        lqw.eq(StringUtils.isNotBlank(query.getJarSign()), Protocol::getJarSign, query.getJarSign());
        lqw.eq(query.getCreateTime() != null, Protocol::getCreateTime, query.getCreateTime());
        lqw.eq(query.getUpdateTime() != null, Protocol::getUpdateTime, query.getUpdateTime());
        lqw.eq(query.getProtocolStatus() != null, Protocol::getProtocolStatus, query.getProtocolStatus());
        lqw.eq(query.getDelFlag() != null, Protocol::getDelFlag, query.getDelFlag());
        lqw.eq(query.getDisplay() != null, Protocol::getDisplay, query.getDisplay());
        lqw.eq(StringUtils.isNotBlank(query.getDataFormat()), Protocol::getDataFormat, query.getDataFormat());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(Protocol::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }
}
