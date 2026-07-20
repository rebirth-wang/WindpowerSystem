package com.fastbee.controller.protocol;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.Protocol;
import com.fastbee.iot.model.vo.ProtocolVO;
import com.fastbee.iot.service.IProtocolService;

/**
 * 协议Controller
 *
 * @author kerwincui
 * @date 2022-12-07
 */
@Api(tags = "协议")
@RestController
@RequestMapping("/iot/protocol")
public class ProtocolController extends BaseController
{

    @Resource
    private IProtocolService protocolService;

    /**
     * 查询协议列表
     */
    @ApiOperation("查询协议列表")
    @PreAuthorize("@ss.hasPermi('iot:protocol:list')")
    @GetMapping("/list")
    public TableDataInfo list(Protocol protocol)
    {
        Page<ProtocolVO> voPage = protocolService.pageProtocolVO(protocol);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出协议列表
     */
    @ApiOperation("导出协议列表")
    @PreAuthorize("@ss.hasPermi('iot:protocol:export')")
    @Log(title = "协议", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Protocol protocol)
    {
        Page<ProtocolVO> voPage = protocolService.pageProtocolVO(protocol);
        ExcelUtil<ProtocolVO> util = new ExcelUtil<ProtocolVO>(ProtocolVO.class);
        util.exportExcel(response, voPage.getRecords(), "协议数据");
    }

    /**
     * 获取协议详细信息
     */
    @ApiOperation("获取协议详细信息")
    @PreAuthorize("@ss.hasPermi('iot:protocol:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(protocolService.selectProtocolById(id));
    }

    /**
     * 新增协议
     */
    @ApiOperation("新增协议")
    @PreAuthorize("@ss.hasPermi('iot:protocol:add')")
    @Log(title = "协议", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Protocol protocol)
    {
        return toAjax(protocolService.insertProtocol(protocol));
    }

    /**
     * 修改协议
     */
    @ApiOperation("修改协议")
    @PreAuthorize("@ss.hasPermi('iot:protocol:edit')")
    @Log(title = "协议", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Protocol protocol)
    {
        return toAjax(protocolService.updateProtocol(protocol));
    }

    /**
     * 删除协议
     */
    @ApiOperation("删除协议")
    @PreAuthorize("@ss.hasPermi('iot:protocol:remove')")
    @Log(title = "协议", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(protocolService.deleteProtocolByIds(ids));
    }
}
