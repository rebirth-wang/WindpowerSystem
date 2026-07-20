package com.fastbee.controller.modbus;

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
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.ProductSubGateway;
import com.fastbee.iot.model.gateWay.ProductSubGatewayAddVO;
import com.fastbee.iot.model.gateWay.ProductSubGatewayVO;
import com.fastbee.iot.service.IProductSubGatewayService;

/**
 * 网关与子产品关联Controller
 *
 * @author zhuangpeng.li
 * @date 2024-09-04
 */
@RestController
@RequestMapping("/productModbus/gateway")
@Api(tags = "网关与子产品关联")
public class ProductSubGatewayController extends BaseController {

    @Resource
    private IProductSubGatewayService productSubGatewayService;

    /**
     * 查询网关与子产品关联列表
     */
    @PreAuthorize("@ss.hasPermi('productModbus:gateway:list')")
    @GetMapping("/list")
    @ApiOperation("查询网关与子产品关联列表")
    public TableDataInfo list(ProductSubGateway productSubGateway) {
        Page<ProductSubGatewayVO> subGatewayVOPage = productSubGatewayService.selectProductSubGatewayList(productSubGateway);
        return getDataTable(subGatewayVOPage.getRecords(), subGatewayVOPage.getTotal());
    }

    /**
     * 导出网关与子产品关联列表
     */
    @ApiOperation("导出网关与子产品关联列表")
    @PreAuthorize("@ss.hasPermi('productModbus:gateway:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, ProductSubGateway productSubGateway) {
        List<ProductSubGatewayVO> list = productSubGatewayService.selectProductSubGatewayList(productSubGateway).getRecords();
        ExcelUtil<ProductSubGatewayVO> util = new ExcelUtil<ProductSubGatewayVO>(ProductSubGatewayVO. class);
        util.exportExcel(response, list, "网关与子产品关联数据");
    }

    /**
     * 获取网关与子产品关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('productModbus:gateway:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取网关与子产品关联详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(productSubGatewayService.queryByIdWithCache(id));
    }

    /**
     * 新增网关与子产品关联
     */
    @PreAuthorize("@ss.hasPermi('productModbus:gateway:add')")
    @PostMapping
    @ApiOperation("新增网关与子产品关联")
    public AjaxResult add(@RequestBody ProductSubGateway productSubGateway) {
        return toAjax(productSubGatewayService.insertWithCache(productSubGateway));
    }

    /**
     * 新增网关与子产品关联
     */
    @PreAuthorize("@ss.hasPermi('productModbus:gateway:add')")
    @PostMapping("/addBatch")
    @ApiOperation("新增网关与子产品关联")
    public AjaxResult addBatch(@RequestBody ProductSubGatewayAddVO productSubGatewayAddVO) {
        return toAjax(productSubGatewayService.addBatch(productSubGatewayAddVO));
    }

    /**
     * 修改网关与子产品关联
     */
    @PreAuthorize("@ss.hasPermi('productModbus:gateway:edit')")
    @PutMapping
    @ApiOperation("修改网关与子产品关联")
    public AjaxResult edit(@RequestBody ProductSubGateway productSubGateway) {
        return toAjax(productSubGatewayService.updateWithCache(productSubGateway));
    }

    /**
     * 修改网关与子产品关联
     */
    @PreAuthorize("@ss.hasPermi('productModbus:gateway:edit')")
    @PostMapping("/editBatch")
    @ApiOperation("修改网关与子产品关联")
    public AjaxResult editBatch(@RequestBody List<ProductSubGateway> list) {
        for (ProductSubGateway productSubGateway : list) {
            productSubGateway.setUpdateBy(getUsername());
        }
        return toAjax(productSubGatewayService.editBatch(list));
    }

    /**
     * 删除网关与子产品关联
     */
    @PreAuthorize("@ss.hasPermi('productModbus:gateway:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除网关与子产品关联")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(productSubGatewayService.deleteWithCacheByIds(ids, true));
    }
}
