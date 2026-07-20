package com.fastbee.controller.gateway;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.protobuf.ServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.model.gateWay.GateSubDeviceVO;
import com.fastbee.iot.model.gateWay.SubDeviceAddVO;
import com.fastbee.iot.model.gateWay.SubDeviceListVO;
import com.fastbee.iot.model.vo.SubGatewayVO;
import com.fastbee.iot.service.ISubGatewayService;

/**
 * 网关与子设备关联Controller
 *
 * @author gsb
 * @date 2024-05-27
 */
@RestController
@RequestMapping("/sub/gateway")
@Api(tags = "网关与子设备关联")
public class SubGatewayController extends BaseController
{
    @Resource
    private ISubGatewayService gatewayService;

    /**
     * 查询网关与子设备关联列表
     */
    @PreAuthorize("@ss.hasPermi('sub:gateway:list')")
    @GetMapping("/list")
    @ApiOperation("查询网关与子设备关联列表")
    public TableDataInfo list(SubGatewayVO gateway)
    {
        Page<SubDeviceListVO> voPage = gatewayService.selectGatewayList(gateway);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 获取网关与子设备关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('sub:gateway:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取网关与子设备关联详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gatewayService.selectGatewayById(id));
    }

    /**
     * 新增网关与子设备关联
     */
    @PreAuthorize("@ss.hasPermi('sub:gateway:add')")
    @PostMapping
    @ApiOperation("新增网关与子设备关联")
    public AjaxResult add(@RequestBody SubGateway gateway)
    {
        return toAjax(gatewayService.insertGateway(gateway));
    }

    /**
     * 修改网关与子设备关联
     */
    @PreAuthorize("@ss.hasPermi('sub:gateway:edit')")
    @PutMapping
    @ApiOperation("修改网关与子设备关联")
    public AjaxResult edit(@RequestBody SubGateway gateway)
    {
        return toAjax(gatewayService.updateGateway(gateway));
    }

    /**
     * 删除网关与子设备关联
     */
    @PreAuthorize("@ss.hasPermi('sub:gateway:remove')")
	@DeleteMapping("/{ids}")
    @ApiOperation("删除网关与子设备关联")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gatewayService.deleteGatewayByIds(ids));
    }

    /**
     * 获取可选择的网关子设备列表
     */
    @PreAuthorize("@ss.hasPermi('sub:gateway:query')")
    @GetMapping(value = "/subDevice")
    @ApiOperation("获取可选择的网关子设备列表")
    public TableDataInfo subDevice(GateSubDeviceVO subDeviceVO)
    {
        Page<GateSubDeviceVO> voPage = gatewayService.getIsSelectGateSubDevice(subDeviceVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }


    /**
     * 新增网关与子设备关联
     */
    @PreAuthorize("@ss.hasPermi('sub:gateway:add')")
    @PostMapping("/addBatch")
    @ApiOperation("批量新增网关与子设备关联")
    public AjaxResult addBatch(@RequestBody SubDeviceAddVO subDeviceAddVO) throws ServiceException {
        return toAjax(gatewayService.insertSubDeviceBatch(subDeviceAddVO));
    }

    /**
     * 批量修改网关与子设备关联
     */
    @PreAuthorize("@ss.hasPermi('sub:gateway:edit')")
    @PostMapping("/editBatch")
    @ApiOperation("批量修改网关与子设备关联")
    public AjaxResult editBatch(@RequestBody List<SubGateway> list)
    {
        if (CollectionUtils.isEmpty(list)) {
            return success();
        }
        List<String> addressList = list.stream()
                .map(SubGateway::getAddress).distinct().collect(Collectors.toList());
        if (addressList.size() != list.size()){
            return error(MessageUtils.message("subGateway.subDeviceAddress.is.repeat"));
        }
        gatewayService.updateSubDeviceBatch(list);
        return AjaxResult.success();
    }

}
