package com.fastbee.controller.ruleEngine;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.domain.vo.BridgeVO;
import com.fastbee.bridge.service.IBridgeService;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;

/**
 * 数据桥接Controller
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */
@RestController
@RequestMapping("/iot/bridge")
@Api(tags = "数据桥接")
public class BridgeController extends BaseController {
    @Resource
    private IBridgeService bridgeService;

    /**
     * 查询数据桥接列表
     */
    @PreAuthorize("@ss.hasPermi('iot:bridge:list')")
    @GetMapping("/list")
    @ApiOperation("查询数据桥接列表")
    public TableDataInfo list(Bridge bridge) {
        Page<BridgeVO> voPage = bridgeService.pageBridgeVO(bridge);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出数据桥接列表
     */
    @ApiOperation("导出数据桥接列表")
    @PreAuthorize("@ss.hasPermi('iot:bridge:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, Bridge bridge) {
        Page<BridgeVO> voPage = bridgeService.pageBridgeVO(bridge);
        ExcelUtil<BridgeVO> util = new ExcelUtil<BridgeVO>(BridgeVO.class);
        util.exportExcel(response, voPage.getRecords(), "数据桥接数据");
    }

    /**
     * 获取数据桥接详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:bridge:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取数据桥接详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        Bridge bridge = new Bridge();
        bridge.setId(id);
        return success(bridgeService.queryByIdWithCache(bridge));
    }

    /**
     * 新增数据桥接
     */
    @PreAuthorize("@ss.hasPermi('iot:bridge:add')")
    @PostMapping
    @ApiOperation("新增数据桥接")
    public AjaxResult add(@RequestBody Bridge bridge) {
        return toAjax(bridgeService.insertWithCache(bridge));
    }

    /**
     * 修改数据桥接
     */
    @PreAuthorize("@ss.hasPermi('iot:bridge:edit')")
    @PutMapping
    @ApiOperation("修改数据桥接")
    public AjaxResult edit(@RequestBody Bridge bridge) {
        SecurityUtils.checkUserOperatePermission(bridge.getTenantId(), bridge.getCreateBy());
        bridge.setUpdateBy(getUsername());
        return toAjax(bridgeService.updateWithCache(bridge));
    }

    /**
     * 删除数据桥接
     */
    @PreAuthorize("@ss.hasPermi('iot:bridge:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除数据桥接")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<Bridge> bridgeList = bridgeService.listByIds(Arrays.asList(ids));
        for (Bridge bridge : bridgeList) {
            SecurityUtils.checkUserOperatePermission(bridge.getTenantId(), bridge.getCreateBy());
        }
        return toAjax(bridgeService.deleteWithCacheByIds(ids, true));
    }

    @PreAuthorize("@ss.hasPermi('iot:bridge:edit')")
    @PostMapping("/connect")
    @ApiOperation("连接数据桥接")
    public AjaxResult connect(@RequestBody Bridge bridge)
    {
        SecurityUtils.checkUserOperatePermission(bridge.getTenantId(), bridge.getCreateBy());
        int result = bridgeService.connect(bridge);
        if (result == -1) {
            return AjaxResult.error("当前配置未启用，请先启用");
        }
        return toAjax(result);

    }
}
