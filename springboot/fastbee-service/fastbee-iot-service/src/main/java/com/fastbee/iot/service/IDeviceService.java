package com.fastbee.iot.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.quartz.SchedulerException;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelValuesInput;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceGroup;
import com.fastbee.iot.model.*;
import com.fastbee.iot.model.ThingsModels.ThingsModelShadow;
import com.fastbee.iot.model.dto.ThingsModelDTO;
import com.fastbee.iot.model.gateWay.ProductSubDeviceAddVO;
import com.fastbee.iot.model.vo.*;

/**
 * 设备Service接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
public interface IDeviceService extends IService<Device>
{
    /**
     * 查询设备列表
     *
     * @param deviceVO 设备
     * @return 设备分页集合
     */
    Page<DeviceVO> pageDeviceVO(DeviceVO deviceVO);

    /**
     * 查询设备
     *
     * @param deviceId 设备id
     * @return 设备
     */
    public DeviceVO selectDeviceByDeviceId(Long deviceId);

    /**
     * 查询设备属性、功能、事件
     *
     * @return 设备
     */
    public DeviceStatistic selectDeviceStatistic();

    /**
     * 获取设备、产品和告警数量
     *
     * @return 设备
     */
    public DeviceStatistic selectDeviceProductAlertCount();

    /**
     * 获取操作记录统计
     *
     * @return 设备
     */
    public Long selectFunctionLogCount();

    /**
     * 根据设备编号查询设备
     *
     * @param serialNumber 设备主键
     * @return 设备
     */
    public Device selectDeviceBySerialNumber(String serialNumber);

    /**
     * 根据设备编号查询协议编号
     * @param serialNumber 设备编号
     * @return 协议编号
     */
    public ProductCode getProtocolBySerialNumber(String serialNumber);

    /**
     * 根据设备编号查询简洁设备
     *
     * @param serialNumber 设备主键
     * @return 设备
     */
    public Device selectShortDeviceBySerialNumber(String serialNumber);

    /**
     * 根据设备编号查询设备认证信息
     *
     * @param model 设备编号和产品ID
     * @return 设备
     */
    public ProductAuthenticateModel selectProductAuthenticate(AuthenticateInputModel model);

    /**
     * 查询设备和运行状态
     *
     * @param deviceId 设备主键
     * @return 设备
     */
    public DeviceShortOutput selectDeviceRunningStatusByDeviceId(Long deviceId);

    /**
     * 上报设备的物模型
     * @param input
     * @return
     */
    public List<ThingsModelSimpleItem> reportDeviceThingsModelValue(ThingsModelValuesInput input, Integer type, boolean isShadow);

    /**
     * 查询未分配授权码设备列表
     *
     * @param deviceVO 设备
     * @return 设备集合
     */
    public Page<Device> selectUnAuthDeviceList(DeviceVO deviceVO);

    /**
     * 查询分组可添加设备分页列表
     *
     * @param deviceVO 设备
     * @return 设备集合
     */
    public Page<DeviceVO> selectDeviceListByGroup(DeviceVO deviceVO);

    /**
     * 查询所有设备简短列表
     *
     * @return 设备
     */
    public List<DeviceAllShortOutput> selectAllDeviceShortList(DeviceVO deviceVO);

    /**
     * 查询设备简短列表
     *
     * @param deviceVO 设备
     * @return 设备集合
     */
    public Page<DeviceShortOutput> selectDeviceShortList(DeviceVO deviceVO);

    /**
     * 新增设备
     *
     * @param deviceVO 设备
     * @return 结果
     */
    public DeviceVO insertDevice(DeviceVO deviceVO);

    /**
     * 设备关联用户
     *
     * @param deviceRelateUserInput 设备
     * @return 结果
     */
    public AjaxResult deviceRelateUser(DeviceRelateUserInput deviceRelateUserInput);

    /**
     * 设备认证后自动添加设备
     *
     * @return 结果
     */
    public int insertDeviceAuto(String serialNumber,Long userId,Long productId, Integer status);

    /**
     * 获取设备设置的影子
     * @param device
     * @return
     */
    public ThingsModelShadow getDeviceShadowThingsModel(Device device);

    /**
     * 上报设备信息
     * @param deviceVO 设备
     * @return 结果
     */
    public int reportDevice(DeviceVO deviceVO,Device deviceentity);

    /**
     * 删除设备
     *
     * @param deviceId 需要删除的设备主键集合
     * @return 结果
     */
    public int deleteDeviceByDeviceId(Long deviceId) throws SchedulerException;

    /**
     * 生成设备唯一编号
     * @return 结果
     */
    public String generationDeviceNum(Integer type);

    /**
     * 重置设备状态
     * @return 结果
     */
    public int resetDeviceStatus(String deviceNum);

    /**
     * 获取设备MQTT连接参数
     * @param deviceId 设备id
     * @return
     */
    public DeviceMqttConnectVO getMqttConnectData(Long deviceId);

    public DeviceHttpAuthVO getHttpAuthData(Long deviceId);
    /**
     * 根据产品ID获取产品下所有编号
     * @param productId
     * @return
     */
    public String[] getDeviceNumsByProductId(Long productId);

    /**
     * 获取所有已经激活并不是禁用的设备
     * @return
     */
    List<DeviceStatusVO> selectDeviceActive();

    /**
     * 获取所有TCP设备
     * @return
     */
    List<DeviceStatusVO> selectModbusTcpDevice();

    /**
     * 批量导入设备
     * @param deviceImportVOList 模板数据
     * @param: productId 产品id
     * @return java.lang.String
     */
    String importDevice(List<DeviceImportVO> deviceImportVOList, Long productId);

    /**
     * 批量导入分配设备
     * @param deviceAssignmentVOS 设备集合
     * @param: productId
     * @param: deptId
     * @return java.lang.String
     */
    AjaxResult importAssignmentDevice(List<DeviceAssignmentVO> deviceAssignmentVOS, Long productId, Long deptId);

    /**
     * 分配设备
     * @param deptId 机构id
     * @param: deviceIds 设备id字符串
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    AjaxResult assignment(Long deptId, String deviceIds);

    /**
     * 回收设备
     * @param: deviceIds 设备id字符串
     * @return com.fastbee.common.core.domain.AjaxResult
     */
    AjaxResult recovery(Map<Long, Long> deviceMap);

    /**
     * 查询终端用户设备简短列表，主页列表数据
     */
    Page<DeviceShortOutput> listTerminalUser(DeviceVO deviceVO);

    List<DeviceVO> listTerminalUserByGroup(DeviceVO deviceVO);

    /**
     * 查询设备状态以及传输协议
     * @param serialNumber
     * @return
     */
    DeviceStatusVO selectDeviceStatusAndTransportStatus(String serialNumber);

    /**
     * 获取设备物模型概况
     * @param deviceId 设备id
     * @return java.util.List<com.fastbee.iot.model.ThingsModelItem.ThingsModelShortVO>
     */
    List<ThingsModelDTO> listThingsModel(Long deviceId);

    public void updateByOrder(Long userId,Long deviceId);

    /**
     * 查询产品下所有设备，返回设备编号
     *
     * @param productId 产品id
     * @return
     */
    public List<String> selectSerialNumbersByProductId(Long productId);

    /**
     * 查询ModbusTcp设备主站列表
     * @param deviceVO
     * @return
     */
    Page<DeviceVO> pageModbusTcpHost(DeviceVO deviceVO);

    AjaxResult restoreDeviceByDeviceId(Long deviceId);

    AjaxResult deleteDeviceByIds(Long[] deviceIds);

    Page<Device> pageDeleteDeviceVO(DeviceVO device);

    AjaxResult userAssignment(Long userId, String deviceIds);

    Device selectAndCheckBySerialNumber(Device device);

    void checkDeviceDataScope(Long deviceId);

    Device getDeviceByIp(String deviceIp);

    void handleSubProductDeviceAddBatch(ProductSubDeviceAddVO productSubDeviceAddVO);

    AjaxResult addBatchByProduct(ProductSubDeviceAddVO vo);

    /**
     * 根据分组id集合查询设备分组
     *
     * @param groupIds 设备分组主键
     * @return 设备分组
     */
    List<DeviceGroup> listDeviceGroupByGroupIds(List<Long> groupIds);
}
