package com.fastbee.iot.service.impl;

import static org.apache.commons.lang3.SystemUtils.getUserName;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.protobuf.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.mapper.SubGatewayMapper;
import com.fastbee.iot.model.gateWay.GateSubDeviceVO;
import com.fastbee.iot.model.gateWay.ProductSubDeviceAddVO;
import com.fastbee.iot.model.gateWay.SubDeviceAddVO;
import com.fastbee.iot.model.gateWay.SubDeviceListVO;
import com.fastbee.iot.model.vo.SubGatewayVO;
import com.fastbee.iot.service.ISubGatewayService;

/**
 * 网关与子设备关联Service业务层处理
 *
 * @author gsb
 * @date 2024-05-27
 */
@Service
public class SubGatewayServiceImpl extends ServiceImpl<SubGatewayMapper,SubGateway> implements ISubGatewayService
{
    @Resource
    private SubGatewayMapper gatewayMapper;
    @Resource
    private IDeviceCache deviceCache;

    /**
     * 查询网关与子设备关联
     *
     * @param id 网关与子设备关联主键
     * @return 网关与子设备关联
     */
    @Override
    public SubGateway selectGatewayById(Long id)
    {
        return gatewayMapper.selectById(id);
    }

    /**
     * 查询网关与子设备关联列表
     *
     * @param gateway 网关与子设备关联
     * @return 网关与子设备关联
     */
    @Override
    public Page<SubDeviceListVO> selectGatewayList(SubGatewayVO gateway)
    {
        return gatewayMapper.selectGatewayList(new Page<>(gateway.getPageNum(),gateway.getPageSize()), gateway);
    }

    /**
     * 新增网关与子设备关联
     *
     * @param gateway 网关与子设备关联
     * @return 结果
     */
    @Override
    public int insertGateway(SubGateway gateway)
    {
        gateway.setCreateTime(DateUtils.getNowDate());
        return gatewayMapper.insert(gateway);
    }

    /**
     * 修改网关与子设备关联
     *
     * @param gateway 网关与子设备关联
     * @return 结果
     */
    @Override
    public int updateGateway(SubGateway gateway)
    {
        gateway.setUpdateTime(DateUtils.getNowDate());
        return gatewayMapper.updateById(gateway);
    }

    /**
     * 批量删除网关与子设备关联
     *
     * @param ids 需要删除的网关与子设备关联主键
     * @return 结果
     */
    @Override
    public int deleteGatewayByIds(Long[] ids)
    {
        List<SubGateway> subGatewayList = gatewayMapper.selectBatchIds(Arrays.asList(ids));
        for (SubGateway subGateway : subGatewayList) {
            // redis中删除设备协议缓存信息
            deviceCache.deleteDeviceProtocolDetailCache(subGateway.getSubClientId());
        }
        return gatewayMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除网关与子设备关联信息
     *
     * @param id 网关与子设备关联主键
     * @return 结果
     */
    @Override
    public int deleteGatewayById(Long id)
    {
        return gatewayMapper.deleteById(id);
    }

    /**
     * 获取可选的网关子设备列表
     * @return
     */
    @Override
    public Page<GateSubDeviceVO> getIsSelectGateSubDevice(GateSubDeviceVO subDeviceVO){
        return gatewayMapper.getIsSelectGateSubDevice(new Page<>(subDeviceVO.getPageNum(), subDeviceVO.getPageSize()), subDeviceVO);
    }

    /**
     * 批量添加子设备
     * @param subDeviceAddVO
     * @return
     */
    @Override
    public int insertSubDeviceBatch(SubDeviceAddVO subDeviceAddVO) throws ServiceException {
        this.checkBindRepeatAddress(subDeviceAddVO);
        List<SubDeviceAddVO.SubDeviceAddInfoVO> infoVOList = subDeviceAddVO.getSubDeviceAddInfoVOList();
        List<SubGateway> list = new ArrayList<>();
        for (SubDeviceAddVO.SubDeviceAddInfoVO subDeviceAddInfoVO : infoVOList) {
            SubGateway subGateway = new SubGateway();
            subGateway.setSubClientId(subDeviceAddInfoVO.getSubClientId());
            subGateway.setParentClientId(subDeviceAddVO.getParentClientId());
            subGateway.setParentProductId(subDeviceAddVO.getParentProductId());
            subGateway.setAddress(subDeviceAddInfoVO.getAddress());
            subGateway.setSubProductId(subDeviceAddInfoVO.getSubProductId());
            subGateway.setCreateBy(SecurityUtils.getUsername());
            subGateway.setCreateTime(new Date());
            list.add(subGateway);
            // redis中删除设备协议缓存信息
            deviceCache.deleteDeviceProtocolDetailCache(subDeviceAddInfoVO.getSubClientId());
        }
        // redis中删除设备协议缓存信息
        deviceCache.deleteDeviceProtocolDetailCache(subDeviceAddVO.getParentClientId());
        return gatewayMapper.insertBatch(list) ? 1 : 0;
    }

    /**
     * 批量更新子设备
     * @param list
     * @return
     */
    @Override
    public void updateSubDeviceBatch(List<SubGateway> list){
        assert !CollectionUtils.isEmpty(list) : "集合为空";
        for (SubGateway gateway : list) {
            // redis中删除设备协议缓存信息
            deviceCache.deleteDeviceProtocolDetailCache(gateway.getSubClientId());
            gateway.setUpdateBy(getUserName());
            gateway.setUpdateTime(new Date());
            this.updateGateway(gateway);
        }
        // redis中删除设备协议缓存信息
        deviceCache.deleteDeviceProtocolDetailCache(list.get(0).getParentClientId());
    }

    /**
     * 根据网关设备编号查询子设备列表
     * @param gwSerialNumber
     * @return
     */
    @Override
    public List<SubGateway> getSubDeviceListByGw(String gwSerialNumber){
        LambdaQueryWrapper<SubGateway> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubGateway::getParentClientId, gwSerialNumber);
        return gatewayMapper.selectList(queryWrapper);
    }

    @Override
    public void deleteByParentClientId(String serialNumber) {
        LambdaQueryWrapper<SubGateway> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubGateway::getParentClientId, serialNumber);
        List<SubGateway> subGatewayList = gatewayMapper.selectList(queryWrapper);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(subGatewayList)) {
            return;
        }
        gatewayMapper.delete(queryWrapper);
        for (SubGateway subGateway : subGatewayList) {
            // redis中删除设备协议缓存信息
            deviceCache.deleteDeviceProtocolDetailCache(subGateway.getSubClientId());
        }
        // redis中删除设备协议缓存信息
        deviceCache.deleteDeviceProtocolDetailCache(serialNumber);
    }

    @Override
    public void updateSubCliectId(String oldSerialNumber, String serialNumber) {
        // redis中删除设备协议缓存信息
        deviceCache.deleteDeviceProtocolDetailCache(oldSerialNumber);
        LambdaUpdateWrapper<SubGateway> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SubGateway::getSubClientId, oldSerialNumber);
        updateWrapper.set(SubGateway::getSubClientId, serialNumber);
        gatewayMapper.update(null, updateWrapper);
    }

    private LambdaQueryWrapper<SubGateway> buildQueryWrapper(SubGateway query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SubGateway> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getParentClientId() != null, SubGateway::getParentClientId, query.getParentClientId());
        lqw.eq(query.getSubClientId() != null, SubGateway::getSubClientId, query.getSubClientId());
        lqw.eq(StringUtils.isNotEmpty(query.getAddress()), SubGateway::getAddress, query.getAddress());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SubGateway::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SubGateway entity){
        //TODO 做一些数据校验,如唯一约束
    }

    private void checkBindRepeatAddress(SubDeviceAddVO subDeviceAddVO) throws ServiceException {
        // 校验是否存在相同的子设备地址
        List<String> addressList = subDeviceAddVO.getSubDeviceAddInfoVOList().stream()
                .map(SubDeviceAddVO.SubDeviceAddInfoVO::getAddress).distinct().collect(Collectors.toList());
        if (addressList.size() != subDeviceAddVO.getSubDeviceAddInfoVOList().size()){
            throw new ServiceException(MessageUtils.message("subGateway.subDeviceAddress.is.repeat"));
        }
        if (StringUtils.isNotEmpty(subDeviceAddVO.getParentClientId())) {
            LambdaQueryWrapper<SubGateway> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubGateway::getParentClientId, subDeviceAddVO.getParentClientId());
            List<SubGateway> subGatewayList = baseMapper.selectList(queryWrapper);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(subGatewayList)) {
                List<String> list = subGatewayList.stream().map(SubGateway::getAddress).distinct().collect(Collectors.toList());
                Set<String> strings = new HashSet<>(addressList);
                boolean b = list.stream().anyMatch(strings::contains);
                if (b) {
                    throw new ServiceException(MessageUtils.message("subGateway.subDeviceAddress.is.repeat"));
                }
            }
        }
        return;
    }

    @Override
    public String checkRepeatAddress(List<ProductSubDeviceAddVO.SubProduct> subProductList, String parentClientId) {
        // 校验是否存在相同的子设备地址
        List<ProductSubDeviceAddVO.SubDevice> subDeviceList = Optional.ofNullable(subProductList).orElseGet(ArrayList::new).stream()
                .flatMap(subProduct ->
                        Optional.ofNullable(subProduct.getSubDeviceList()).orElseGet(ArrayList::new).stream()
                ).collect(Collectors.toList());
        Set<String> addressList = subDeviceList.stream().map(ProductSubDeviceAddVO.SubDevice::getAddress).filter(Objects::nonNull).collect(Collectors.toSet());
        if (addressList.size() != subDeviceList.size()){
            return "存在相同的子设备地址，请修改后重试！";
        }
        if (StringUtils.isNotEmpty(parentClientId)) {
            LambdaQueryWrapper<SubGateway> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SubGateway::getParentClientId, parentClientId);
            List<SubGateway> subGatewayList = baseMapper.selectList(queryWrapper);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(subGatewayList)) {
                List<String> list = subGatewayList.stream().map(SubGateway::getAddress).distinct().collect(Collectors.toList());
                Set<String> strings = new HashSet<>(addressList);
                boolean b = list.stream().anyMatch(strings::contains);
                if (b) {
                    return "存在相同的子设备地址，请修改后重试！";
                }
            }
        }
        return null;
    }
}
