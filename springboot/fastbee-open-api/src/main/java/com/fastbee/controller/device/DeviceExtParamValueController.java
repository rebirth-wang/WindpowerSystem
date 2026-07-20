package com.fastbee.controller.device;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.DeviceExtParamValue;
import com.fastbee.iot.model.vo.ProductExtParamVO;
import com.fastbee.iot.service.IDeviceExtParamValueService;

/**
 * 设备扩展参数值Controller
 *
 * @author fastbee
 * @date 2026-03-18
 */
@RestController
@RequestMapping("/iot/deviceExtValue")
@Api(tags = "设备扩展参数值")
public class DeviceExtParamValueController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IDeviceExtParamValueService deviceExtParamValueService;

    /**
     * 查询设备扩展参数值列表
     */
    @PreAuthorize("@ss.hasPermi('iot:deviceExtValue:list')")
    @GetMapping("/list")
    @ApiOperation("查询设备扩展参数值列表")
    public TableDataInfo list(DeviceExtParamValue deviceExtParamValue) {
        Page<ProductExtParamVO> voPage = deviceExtParamValueService.pageDeviceExtParamValueVO(deviceExtParamValue);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出设备扩展参数值列表
     */
    @ApiOperation("导出设备扩展参数值列表")
    @PreAuthorize("@ss.hasPermi('iot:deviceExtValue:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceExtParamValue deviceExtParamValue) {
        Page<ProductExtParamVO> voPage = deviceExtParamValueService.pageDeviceExtParamValueVO(deviceExtParamValue);
        ExcelUtil<ProductExtParamVO> util = new ExcelUtil<ProductExtParamVO>(ProductExtParamVO.class);
        util.exportExcel(response, voPage.getRecords(), "设备扩展参数值数据");
    }

    /**
     * 获取设备扩展参数值详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:deviceExtValue:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取设备扩展参数值详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        DeviceExtParamValue deviceExtParamValue = new DeviceExtParamValue();
        deviceExtParamValue.setId(id);
        return success(deviceExtParamValueService.queryByIdWithCache(deviceExtParamValue));
    }

    /**
     * 新增设备扩展参数值
     */
    @PreAuthorize("@ss.hasPermi('iot:deviceExtValue:add')")
    @PostMapping
    @ApiOperation("新增设备扩展参数值")
    public AjaxResult add(@RequestBody DeviceExtParamValue deviceExtParamValue) {
        return toAjax(deviceExtParamValueService.insertWithCache(deviceExtParamValue));
    }

    /**
     * 修改设备扩展参数值
     */
    @PreAuthorize("@ss.hasPermi('iot:deviceExtValue:edit')")
    @PutMapping
    @ApiOperation("修改设备扩展参数值")
    public AjaxResult edit(@RequestBody DeviceExtParamValue deviceExtParamValue) {
        return toAjax(deviceExtParamValueService.updateWithCache(deviceExtParamValue));
    }

    /**
     * 删除设备扩展参数值
     */
    @PreAuthorize("@ss.hasPermi('iot:deviceExtValue:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除设备扩展参数值")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(deviceExtParamValueService.deleteWithCacheByIds(ids, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
