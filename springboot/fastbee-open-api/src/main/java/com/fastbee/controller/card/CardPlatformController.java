package com.fastbee.controller.card;

import java.util.Arrays;
import java.util.List;

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
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.CardPlatform;
import com.fastbee.iot.model.vo.CardPlatformVO;
import com.fastbee.iot.service.ICardPlatformService;

/**
 * 物联卡平台Controller
 *
 * @author fastbee
 * @date 2025-11-11
 */
@RestController
@RequestMapping("/iot/cardPlatform")
@Api(tags = "物联卡平台")
public class CardPlatformController extends BaseController {

    @Resource
    private ICardPlatformService cardPlatformService;

    /**
     * 查询物联卡平台列表
     */
    @PreAuthorize("@ss.hasPermi('iot:cardPlatform:list')")
    @GetMapping("/list")
    @ApiOperation("查询物联卡平台列表")
    public TableDataInfo list(CardPlatform cardPlatform) {
        Page<CardPlatformVO> voPage = cardPlatformService.pageCardPlatformVO(cardPlatform);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出物联卡平台列表
     */
    @ApiOperation("导出物联卡平台列表")
    @PreAuthorize("@ss.hasPermi('iot:cardPlatform:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CardPlatform cardPlatform) {
        Page<CardPlatformVO> voPage = cardPlatformService.pageCardPlatformVO(cardPlatform);
        ExcelUtil<CardPlatformVO> util = new ExcelUtil<CardPlatformVO>(CardPlatformVO.class);
        util.exportExcel(response, voPage.getRecords(), "物联卡平台数据");
    }

    /**
     * 获取物联卡平台详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:cardPlatform:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取物联卡平台详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        CardPlatform cardPlatform = new CardPlatform();
        cardPlatform.setId(id);
        return success(cardPlatformService.queryByIdWithCache(cardPlatform));
    }

    /**
     * 新增物联卡平台
     */
    @PreAuthorize("@ss.hasPermi('iot:cardPlatform:add')")
    @PostMapping
    @ApiOperation("新增物联卡平台")
    public AjaxResult add(@RequestBody CardPlatform cardPlatform) {
        return toAjax(cardPlatformService.insertWithCache(cardPlatform));
    }

    /**
     * 修改物联卡平台
     */
    @PreAuthorize("@ss.hasPermi('iot:cardPlatform:edit')")
    @PutMapping
    @ApiOperation("修改物联卡平台")
    public AjaxResult edit(@RequestBody CardPlatform cardPlatform) {
        SecurityUtils.checkUserOperatePermission(cardPlatform.getTenantId(), cardPlatform.getCreateBy());
        return toAjax(cardPlatformService.updateWithCache(cardPlatform));
    }

    /**
     * 删除物联卡平台
     */
    @PreAuthorize("@ss.hasPermi('iot:cardPlatform:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除物联卡平台")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<CardPlatform> cardPlatformList = cardPlatformService.listByIds(Arrays.asList(ids));
        for (CardPlatform cardPlatform : cardPlatformList) {
            SecurityUtils.checkUserOperatePermission(cardPlatform.getTenantId(), cardPlatform.getCreateBy());
        }
        return cardPlatformService.deleteWithCacheByIds(ids, true);
    }

}
