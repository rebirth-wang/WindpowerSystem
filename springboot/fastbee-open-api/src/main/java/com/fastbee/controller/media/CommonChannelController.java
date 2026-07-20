package com.fastbee.controller.media;

import java.util.List;
import java.util.Objects;

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
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.domain.vo.CommonChannelVO;
import com.fastbee.media.service.ICommonChannelService;
import com.fastbee.sip.service.ISipChannelService;

/**
 * 监控视频通道信息Controller
 *
 * @author fastbee
 * @date 2026-01-30
 */
@RestController
@RequestMapping("/iot/channel")
@Api(tags = "监控视频通道信息")
public class CommonChannelController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private ICommonChannelService commonChannelService;

    @Resource
    private ISipChannelService sipChannelService;

    /**
     * 查询监控视频通道信息列表
     */
    @PreAuthorize("@ss.hasPermi('iot:channel:list')")
    @GetMapping("/list")
    @ApiOperation("查询监控视频通道信息列表")
    public TableDataInfo list(CommonChannel commonChannel) {
        Page<CommonChannelVO> voPage = commonChannelService.pageCommonChannelVO(commonChannel);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出监控视频通道信息列表
     */
    @ApiOperation("导出监控视频通道信息列表")
    @PreAuthorize("@ss.hasPermi('iot:channel:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CommonChannel commonChannel) {
        Page<CommonChannelVO> voPage = commonChannelService.pageCommonChannelVO(commonChannel);
        ExcelUtil<CommonChannelVO> util = new ExcelUtil<CommonChannelVO>(CommonChannelVO.class);
        util.exportExcel(response, voPage.getRecords(), "", "监控视频通道信息数据");
    }

    /**
     * 获取监控视频通道信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:channel:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取监控视频通道信息详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(commonChannelService.queryByIdWithCache(id));
    }

    /**
     * 新增监控视频通道信息
     */
    @PreAuthorize("@ss.hasPermi('iot:channel:add')")
    @PostMapping
    @ApiOperation("新增监控视频通道信息")
    public AjaxResult add(@RequestBody CommonChannel commonChannel) {
        return toAjax(commonChannelService.insertWithCache(commonChannel));
    }

    @ApiOperation("新增监控设备通道信息")
    @PreAuthorize("@ss.hasPermi('iot:video:add')")
    @Log(title = "监控设备通道信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/sip/{createNum}")
    public AjaxResult sipadd(@PathVariable("createNum") Long createNum, @RequestBody CommonChannel sipDeviceChannel) {
        String devstr = sipChannelService.insertSipChannelGen(createNum, sipDeviceChannel);
        if (!Objects.equals(devstr, "")) {
            return AjaxResult.success(MessageUtils.message("operate.success"), devstr);
        } else {
            return AjaxResult.error(MessageUtils.message("operate.fail"));
        }
    }

    /**
     * 修改监控视频通道信息
     */
    @PreAuthorize("@ss.hasPermi('iot:channel:edit')")
    @PutMapping
    @ApiOperation("修改监控视频通道信息")
    public AjaxResult edit(@RequestBody CommonChannel commonChannel) {
        return toAjax(commonChannelService.updateWithCache(commonChannel));
    }

    /**
     * 删除监控视频通道信息
     */
    @PreAuthorize("@ss.hasPermi('iot:channel:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除监控视频通道信息")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(commonChannelService.deleteWithCacheByIds(ids, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    /**
     * 查询监控设备通道信息列表
     */
    @ApiOperation("查询监控设备通道信息列表")
    @PreAuthorize("@ss.hasPermi('iot:channel:list')")
    @GetMapping("/listRelDeviceOrScene")
    public AjaxResult listRelDeviceOrScene(String serialNumber, Long sceneModelId) {
        List<CommonChannelVO> list = commonChannelService.listRelDeviceOrScene(serialNumber, sceneModelId);
        return AjaxResult.success(list);
    }

    /** 自定义代码区域 END**/
}
