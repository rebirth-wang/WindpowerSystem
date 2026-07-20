package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.protobuf.ServiceException;

import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.model.gateWay.GateSubDeviceVO;
import com.fastbee.iot.model.gateWay.ProductSubDeviceAddVO;
import com.fastbee.iot.model.gateWay.SubDeviceAddVO;
import com.fastbee.iot.model.gateWay.SubDeviceListVO;
import com.fastbee.iot.model.vo.SubGatewayVO;

/**
 * 网关与子设备关联Service接口
 *
 * @author gsb
 * @date 2024-05-27
 */
public interface ISubGatewayService extends IService<SubGateway>
{
    /**
     * 查询网关与子设备关联
     *
     * @param id 网关与子设备关联主键
     * @return 网关与子设备关联
     */
    public SubGateway selectGatewayById(Long id);

    /**
     * 查询网关与子设备关联列表
     *
     * @param gateway 网关与子设备关联
     * @return 网关与子设备关联集合
     */
    public Page<SubDeviceListVO> selectGatewayList(SubGatewayVO gateway);

    /**
     * 新增网关与子设备关联
     *
     * @param gateway 网关与子设备关联
     * @return 结果
     */
    public int insertGateway(SubGateway gateway);

    /**
     * 修改网关与子设备关联
     *
     * @param gateway 网关与子设备关联
     * @return 结果
     */
    public int updateGateway(SubGateway gateway);

    /**
     * 批量删除网关与子设备关联
     *
     * @param ids 需要删除的网关与子设备关联主键集合
     * @return 结果
     */
    public int deleteGatewayByIds(Long[] ids);

    /**
     * 删除网关与子设备关联信息
     *
     * @param id 网关与子设备关联主键
     * @return 结果
     */
    public int deleteGatewayById(Long id);

    /**
     * 获取可选的网关子设备列表
     * @return
     */
    Page<GateSubDeviceVO> getIsSelectGateSubDevice(GateSubDeviceVO subDeviceVO);

    /**
     * 批量添加子设备
     * @param subDeviceAddVO
     * @return
     */
    int insertSubDeviceBatch(SubDeviceAddVO subDeviceAddVO) throws ServiceException;

    /**
     * 批量更新子设备
     * @param list
     * @return
     */
    void updateSubDeviceBatch(List<SubGateway> list);

    /**
     * 根据网关设备编号查询子设备列表
     * @param gwSerialNumber
     * @return
     */
    List<SubGateway> getSubDeviceListByGw(String gwSerialNumber);

    void deleteByParentClientId(String serialNumber);

    void updateSubCliectId(String oldSerialNumber, String serialNumber);

    String checkRepeatAddress(List<ProductSubDeviceAddVO.SubProduct> subProductList, String parentClientId);
}
