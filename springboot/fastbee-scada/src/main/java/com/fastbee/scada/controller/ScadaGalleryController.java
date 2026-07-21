package com.fastbee.scada.controller;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.scada.domain.ScadaGallery;
import com.fastbee.scada.service.IScadaGalleryService;
import com.fastbee.scada.vo.ScadaGalleryVO;

/**
 * 图库管理Controller
 *
 * @author kerwincui
 * @date 2023-11-10
 */
@Api(tags = "图库管理")
@RestController
@RequestMapping("/scada/gallery")
public class ScadaGalleryController extends BaseController
{
    @Resource
    private IScadaGalleryService scadaGalleryService;

    /**
     * 查询图库管理列表
     */
    @ApiOperation("查询图库管理")
    @PreAuthorize("@ss.hasPermi('scada:gallery:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScadaGallery scadaGallery)
    {
        Page<ScadaGalleryVO> voPage = scadaGalleryService.pageScadaGalleryVO(scadaGallery);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出图库管理列表
     */
    @ApiOperation("导出图库管理列表")
    @PreAuthorize("@ss.hasPermi('scada:gallery:export')")
    @Log(title = "图库管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ScadaGallery scadaGallery)
    {
        Page<ScadaGalleryVO> voPage = scadaGalleryService.pageScadaGalleryVO(scadaGallery);
        ExcelUtil<ScadaGalleryVO> util = new ExcelUtil<ScadaGalleryVO>(ScadaGalleryVO.class);
        util.exportExcel(response, voPage.getRecords(), "组态图库数据");
    }

    /**
     * 获取图库管理详细信息
     */
    @ApiOperation("获取图库详细信息")
    @PreAuthorize("@ss.hasPermi('scada:gallery:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ScadaGallery scadaGallery = new ScadaGallery();
        scadaGallery.setId(id);
        return success(scadaGalleryService.selectScadaGalleryById(scadaGallery));
    }

    /**
     * 新增图库管理
     */
    @ApiOperation("新增图库")
    @PreAuthorize("@ss.hasPermi('scada:gallery:add')")
    @Log(title = "图库管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ScadaGallery scadaGallery)
    {
        return toAjax(scadaGalleryService.insertScadaGallery(scadaGallery));
    }

    /**
     * 修改图库管理
     */
    @ApiOperation("修改图库")
    @PreAuthorize("@ss.hasPermi('scada:gallery:edit')")
    @Log(title = "图库管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ScadaGallery scadaGallery)
    {
        SecurityUtils.checkUserOperatePermission(scadaGallery.getTenantId(), scadaGallery.getCreateBy());
        return toAjax(scadaGalleryService.updateScadaGallery(scadaGallery));
    }

    /**
     * 删除图库管理
     */
    @ApiOperation("删除图库")
    @PreAuthorize("@ss.hasPermi('scada:gallery:remove')")
    @Log(title = "图库管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        List<ScadaGallery> scadaGalleryList = scadaGalleryService.listByIds(Arrays.asList(ids));
        for (ScadaGallery scadaGallery : scadaGalleryList) {
            SecurityUtils.checkUserOperatePermission(scadaGallery.getTenantId(), scadaGallery.getCreateBy());
        }
        return toAjax(scadaGalleryService.deleteScadaGalleryByIds(ids));
    }

    /**
     * 上传文件
     * @param file 文件
     * @param categoryName 分类名称
     * @return
     */
    @ApiOperation("上传文件")
    @PreAuthorize("@ss.hasPermi('scada:gallery:add')")
    @PostMapping("/uploadFile")
    public AjaxResult uploadFile(MultipartFile file, String categoryName) {
        if (file == null) {
            return error("上传文件为空");
        }
        if (StringUtils.isEmpty(categoryName)) {
            return error("上传文件分类为空");
        }
        return scadaGalleryService.uploadFile(file, categoryName);
    }
}
