package com.fastbee.controller.socialUser;

import java.util.Date;

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
import com.fastbee.social.domain.SocialPlatform;
import com.fastbee.social.model.vo.SocialPlatformVO;
import com.fastbee.social.service.ISocialPlatformService;

/**
 * 第三方登录平台控制Controller
 *
 * @author kerwincui
 * @date 2022-04-11
 */
@Api(tags = "第三方登录平台")
@RestController
@RequestMapping("/iot/platform")
public class SocialPlatformController extends BaseController {
    @Resource
    private ISocialPlatformService socialPlatformService;

    /**
     * 查询第三方登录平台控制列表
     */
    @PreAuthorize("@ss.hasPermi('iot:platform:list')")
    @GetMapping("/list")
    @ApiOperation("第三方登录平台分页列表")
    public TableDataInfo list(SocialPlatform socialPlatform) {
        Page<SocialPlatformVO> voPage = socialPlatformService.pageSocialPlatformVO(socialPlatform);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出第三方登录平台控制列表
     */
    @ApiOperation("导出第三方登录平台控制列表")
    @PreAuthorize("@ss.hasPermi('iot:platform:export')")
    @Log(title = "第三方登录平台控制", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SocialPlatform socialPlatform) {
        Page<SocialPlatformVO> voPage = socialPlatformService.pageSocialPlatformVO(socialPlatform);
        ExcelUtil<SocialPlatformVO> util = new ExcelUtil<SocialPlatformVO>(SocialPlatformVO.class);
        util.exportExcel(response, voPage.getRecords(), "第三方登录平台控制数据");
    }

    /**
     * 获取第三方登录平台控制详细信息
     */
    @ApiOperation("获取第三方登录平台控制详细信息")
    @PreAuthorize("@ss.hasPermi('iot:platform:query')")
    @GetMapping(value = "/{socialPlatformId}")
    public AjaxResult getInfo(@PathVariable("socialPlatformId") Long socialPlatformId) {
        return success(socialPlatformService.queryByIdWithCache(socialPlatformId));
    }

    /**
     * 新增第三方登录平台控制
     */
    @ApiOperation("新增第三方登录平台控制")
    @PreAuthorize("@ss.hasPermi('iot:platform:add')")
    @Log(title = "第三方登录平台控制", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SocialPlatform socialPlatform) {
        socialPlatform.setCreateBy(getUsername());
        socialPlatform.setCreateTime(new Date());
        return toAjax(socialPlatformService.insertWithCache(socialPlatform));
    }

    /**
     * 修改第三方登录平台控制
     */
    @ApiOperation("修改第三方登录平台控制")
    @PreAuthorize("@ss.hasPermi('iot:platform:edit')")
    @Log(title = "第三方登录平台控制", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SocialPlatform socialPlatform) {
        socialPlatform.setUpdateBy(getUsername());
        socialPlatform.setUpdateTime(new Date());
        return toAjax(socialPlatformService.updateWithCache(socialPlatform));
    }

    /**
     * 删除第三方登录平台控制
     */
    @ApiOperation("删除第三方登录平台控制")
    @PreAuthorize("@ss.hasPermi('iot:platform:remove')")
    @Log(title = "第三方登录平台控制", businessType = BusinessType.DELETE)
    @DeleteMapping("/{socialPlatformIds}")
    public AjaxResult remove(@PathVariable Long[] socialPlatformIds) {
        return toAjax(socialPlatformService.deleteWithCacheByIds(socialPlatformIds, true));
    }
}
