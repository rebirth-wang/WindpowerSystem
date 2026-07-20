package com.fastbee.controller.deviceConfig;

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
import com.fastbee.iot.domain.ProductAuthorize;
import com.fastbee.iot.model.vo.ProductAuthorizeVO;
import com.fastbee.iot.service.IProductAuthorizeService;

/**
 * 产品授权码Controller
 *
 * @author kami
 * @date 2022-04-11
 */
@Api(tags = "产品授权码")
@RestController
@RequestMapping("/iot/authorize")
public class ProductAuthorizeController extends BaseController
{
    @Resource
    private IProductAuthorizeService productAuthorizeService;

    /**
     * 查询产品授权码列表
     */
    @ApiOperation("查询产品授权码列表")
    @PreAuthorize("@ss.hasPermi('iot:authorize:query')")
    @GetMapping("/list")
    public TableDataInfo list(ProductAuthorize productAuthorize)
    {
        Page<ProductAuthorizeVO> voPage = productAuthorizeService.pageProductAuthorizeVO(productAuthorize);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出产品授权码列表
     */
    @ApiOperation("导出产品授权码列表")
    @PreAuthorize("@ss.hasPermi('iot:authorize:export')")
    @Log(title = "产品授权码", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProductAuthorize productAuthorize)
    {
        Page<ProductAuthorizeVO> voPage = productAuthorizeService.pageProductAuthorizeVO(productAuthorize);
        ExcelUtil<ProductAuthorizeVO> util = new ExcelUtil<ProductAuthorizeVO>(ProductAuthorizeVO.class);
        util.exportExcel(response, voPage.getRecords(), "产品授权码数据");
    }

    /**
     * 获取产品授权码详细信息
     */
    @ApiOperation("获取产品授权码详细信息")
    @PreAuthorize("@ss.hasPermi('iot:authorize:query')")
    @GetMapping(value = "/{authorizeId}")
    public AjaxResult getInfo(@PathVariable("authorizeId") Long authorizeId)
    {
        return AjaxResult.success(productAuthorizeService.selectProductAuthorizeByAuthorizeId(authorizeId));
    }

    /**
     * 新增产品授权码
     */
    @ApiOperation("新增产品授权码")
    @PreAuthorize("@ss.hasPermi('iot:authorize:add')")
    @Log(title = "产品授权码", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProductAuthorize productAuthorize)
    {
        return toAjax(productAuthorizeService.insertProductAuthorize(productAuthorize));
    }

    /**
     * 根据数量批量新增产品授权码
     */
    @ApiOperation("根据数量批量新增产品授权码")
    @PreAuthorize("@ss.hasPermi('iot:authorize:add')")
    @Log(title = "根据数量批量新增产品授权码", businessType = BusinessType.INSERT)
    @PostMapping("addProductAuthorizeByNum")
    public AjaxResult addProductAuthorizeByNum(@RequestBody ProductAuthorizeVO productAuthorizeVO)
    {
        return toAjax(productAuthorizeService.addProductAuthorizeByNum(productAuthorizeVO));
    }

    /**
     * 修改产品授权码
     */
    @ApiOperation("修改产品授权码")
    @PreAuthorize("@ss.hasPermi('iot:authorize:edit')")
    @Log(title = "产品授权码", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProductAuthorize productAuthorize)
    {
        return toAjax(productAuthorizeService.updateProductAuthorize(productAuthorize));
    }

    /**
     * 删除产品授权码
     */
    @ApiOperation("删除产品授权码")
    @PreAuthorize("@ss.hasPermi('iot:authorize:remove')")
    @Log(title = "产品授权码", businessType = BusinessType.DELETE)
	@DeleteMapping("/{authorizeIds}")
    public AjaxResult remove(@PathVariable Long[] authorizeIds)
    {
        return toAjax(productAuthorizeService.deleteProductAuthorizeByAuthorizeIds(authorizeIds));
    }
}
