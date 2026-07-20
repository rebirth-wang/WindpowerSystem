package com.fastbee.iot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.Group;
import com.fastbee.iot.model.DeviceGroupInput;
import com.fastbee.iot.model.vo.GroupVO;

/**
 * 设备分组Service接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
public interface IGroupService extends IService<Group>
{
    /**
     * 查询设备分组
     *
     * @param group 设备分组主键
     * @return 设备分组
     */
    public Group selectGroupByGroupId(Group group);

    /**
     * 通过分组ID查询关联的设备ID数组
     * @param groupId
     * @return
     */
    public Long[] selectDeviceIdsByGroupId(Long groupId);

    /**
     * 查询设备分组列表
     *
     * @param groupVO 设备分组
     * @return 设备分组集合
     */
    public Page<GroupVO> selectGroupList(GroupVO groupVO);

    /**
     * 新增设备分组
     *
     * @param group 设备分组
     * @return 结果
     */
    public int insertGroup(Group group);

    /**
     * 分组下批量添加设备分组
     * @return
     */
    public int updateDeviceGroups(DeviceGroupInput input);

    /**
     * 修改设备分组
     *
     * @param group 设备分组
     * @return 结果
     */
    public int updateGroup(Group group);

    /**
     * 批量删除分组
     *
     * @param groupIds 需要删除的设备分组主键集合
     * @return 结果
     */
    public int deleteGroupByGroupIds(Long[] groupIds);

    /**
     * 删除分组信息
     *
     * @param groupId 设备分组主键
     * @return 结果
     */
    public int deleteGroupByGroupId(Long groupId);

}
