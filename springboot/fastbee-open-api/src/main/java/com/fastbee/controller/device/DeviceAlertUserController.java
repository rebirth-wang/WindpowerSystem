package com.fastbee.controller.device;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.iot.domain.DeviceAlertUser;
import com.fastbee.iot.model.vo.DeviceAlertUserVO;
import com.fastbee.iot.service.IDeviceAlertUserService;

/**
 * 设备告警用户Controller
 *
 * @author kerwincui
 * @date 2024-05-15
 */
@RestController
@RequestMapping("/iot/deviceAlertUser")
public class DeviceAlertUserController extends BaseController
{
    @Resource
    private IDeviceAlertUserService deviceAlertUserService;

    /**
     * 查询设备告警用户列表
     */
    @PreAuthorize("@ss.hasPermi('iot:device:alert:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceAlertUser deviceAlertUser)
    {
        Page<DeviceAlertUserVO> list = deviceAlertUserService.selectDeviceAlertUserList(deviceAlertUser);
        return getDataTable(list.getRecords(), list.getTotal());
    }

    /**
     * 获取设备告警用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:device:alert:user:query')")
    @GetMapping("/query")
    public TableDataInfo getUser(SysUser sysUser)
    {
        Page<SysUser> list = deviceAlertUserService.selectUserList(sysUser);
        return getDataTable(list.getRecords(), list.getTotal());
    }

    /**
     * 新增设备告警用户
     */
    @PreAuthorize("@ss.hasPermi('iot:device:alert:user:add')")
    @Log(title = "设备告警用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DeviceAlertUserVO deviceAlertUserVO)
    {

        return toAjax(deviceAlertUserService.insertDeviceAlertUser(deviceAlertUserVO));
    }

    /**
     * 删除设备告警用户
     */
    @PreAuthorize("@ss.hasPermi('iot:device:alert:user:remove')")
    @Log(title = "设备告警用户", businessType = BusinessType.DELETE)
	@DeleteMapping
    public AjaxResult remove(@RequestParam Long deviceId, @RequestParam Long userId)
    {
        return toAjax(deviceAlertUserService.deleteByDeviceIdAndUserId(deviceId, userId));
    }
}
