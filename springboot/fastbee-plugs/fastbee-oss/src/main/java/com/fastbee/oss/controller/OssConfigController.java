package com.fastbee.oss.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.oss.domain.OssConfig;
import com.fastbee.oss.service.IOssConfigService;
import com.fastbee.oss.vo.OssConfigVO;

/**
 * 对象存储配置Controller
 *
 * @author zhuangpeng.li
 * @date 2024-04-19
 */
@RestController
@RequestMapping("/oss/config")
public class OssConfigController extends BaseController
{
    @Resource
    private IOssConfigService ossConfigService;

    /**
     * 查询对象存储配置列表
     */
    @PreAuthorize("@ss.hasPermi('oss:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(OssConfig ossConfig)
    {
        Page<OssConfigVO> voPage = ossConfigService.pageOssConfigVO(ossConfig);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出对象存储配置列表
     */
    @PreAuthorize("@ss.hasPermi('oss:config:export')")
    @Log(title = "对象存储配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OssConfig ossConfig)
    {
        Page<OssConfigVO> voPage = ossConfigService.pageOssConfigVO(ossConfig);
        ExcelUtil<OssConfigVO> util = new ExcelUtil<OssConfigVO>(OssConfigVO.class);
        util.exportExcel(response, voPage.getRecords(), "对象存储配置数据");
    }

    /**
     * 获取对象存储配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('oss:config:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(ossConfigService.selectOssConfigById(id));
    }

    /**
     * 新增对象存储配置
     */
    @PreAuthorize("@ss.hasPermi('oss:config:add')")
    @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OssConfig ossConfig)
    {
        return toAjax(ossConfigService.insertOssConfig(ossConfig));
    }

    /**
     * 修改对象存储配置
     */
    @PreAuthorize("@ss.hasPermi('oss:config:edit')")
    @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OssConfig ossConfig)
    {
        return toAjax(ossConfigService.updateOssConfig(ossConfig));
    }

    /**
     * 删除对象存储配置
     */
    @PreAuthorize("@ss.hasPermi('oss:config:remove')")
    @Log(title = "对象存储配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(ossConfigService.deleteOssConfigByIds(ids));
    }


    @Log(title = "对象存储状态修改", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody OssConfig bo) {
        return toAjax(ossConfigService.updateOssConfigStatus(bo));
    }
}
