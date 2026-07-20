package com.fastbee.controller.deviceConfig;

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
import com.fastbee.iot.domain.ProductExtParam;
import com.fastbee.iot.model.vo.ProductExtParamVO;
import com.fastbee.iot.service.IProductExtParamService;

/**
 * 产品扩展参数Controller
 *
 * @author fastbee
 * @date 2026-03-18
 */
@RestController
@RequestMapping("/iot/productExtParam")
@Api(tags = "产品扩展参数")
public class ProductExtParamController extends BaseController {
    /** 代码生成区域 可直接覆盖**/
    @Resource
    private IProductExtParamService productExtParamService;

    /**
     * 查询产品扩展参数列表
     */
    @PreAuthorize("@ss.hasPermi('iot:productExtParam:list')")
    @GetMapping("/list")
    @ApiOperation("查询产品扩展参数列表")
    public TableDataInfo list(ProductExtParam productExtParam) {
        Page<ProductExtParamVO> voPage = productExtParamService.pageProductExtParamVO(productExtParam);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出产品扩展参数列表
     */
    @ApiOperation("导出产品扩展参数列表")
    @PreAuthorize("@ss.hasPermi('iot:productExtParam:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProductExtParam productExtParam) {
        Page<ProductExtParamVO> voPage = productExtParamService.pageProductExtParamVO(productExtParam);
        ExcelUtil<ProductExtParamVO> util = new ExcelUtil<ProductExtParamVO>(ProductExtParamVO.class);
        util.exportExcel(response, voPage.getRecords(), "产品扩展参数数据");
    }

    /**
     * 获取产品扩展参数详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:productExtParam:query')")
    @GetMapping(value = "/{paramId}")
    @ApiOperation("获取产品扩展参数详细信息")
    public AjaxResult getInfo(@PathVariable("paramId") Long paramId) {
        ProductExtParam productExtParam = new ProductExtParam();
        productExtParam.setParamId(paramId);
        return success(productExtParamService.queryByIdWithCache(productExtParam));
    }

    /**
     * 新增产品扩展参数
     */
    @PreAuthorize("@ss.hasPermi('iot:productExtParam:add')")
    @PostMapping
    @ApiOperation("新增产品扩展参数")
    public AjaxResult add(@RequestBody ProductExtParam productExtParam) {
        return toAjax(productExtParamService.insertWithCache(productExtParam));
    }

    /**
     * 修改产品扩展参数
     */
    @PreAuthorize("@ss.hasPermi('iot:productExtParam:edit')")
    @PutMapping
    @ApiOperation("修改产品扩展参数")
    public AjaxResult edit(@RequestBody ProductExtParam productExtParam) {
        return toAjax(productExtParamService.updateWithCache(productExtParam));
    }

    /**
     * 删除产品扩展参数
     */
    @PreAuthorize("@ss.hasPermi('iot:productExtParam:remove')")
    @DeleteMapping("/{paramIds}")
    @ApiOperation("删除产品扩展参数")
    public AjaxResult remove(@PathVariable Long[] paramIds) {
        return toAjax(productExtParamService.deleteWithCacheByIds(paramIds, true));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
