package com.fastbee.controller.deviceConfig;

import static com.fastbee.common.extend.utils.SecurityUtils.isAdmin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
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
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.iot.convert.ProductConvert;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.model.ChangeProductStatusModel;
import com.fastbee.iot.model.IdAndName;
import com.fastbee.iot.model.vo.ProductVO;
import com.fastbee.iot.service.IProductService;

/**
 * 产品Controller
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Api(tags = "产品管理")
@RestController
@RequestMapping("/iot/product")
public class ProductController extends BaseController
{
    @Resource
    private IProductService productService;

    /**
     * 查询产品列表
     */
    @GetMapping("/list")
    @ApiOperation("产品分页列表")
    public TableDataInfo list(ProductVO productVO)
    {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            productVO.setTenantId(user.getUserId());
            Page<ProductVO> voPage = productService.pageTerminalUserProduct(productVO);
            return getDataTable(voPage.getRecords(), voPage.getTotal());
        }
        if (Objects.isNull(productVO.getShowSenior())){
            // 默认不展示上级产品
            productVO.setShowSenior(false);
        }
        Long deptUserId = getLoginUser().getUser().getDept().getDeptUserId();
        productVO.setIsAdmin(isAdmin(deptUserId));
        productVO.setDeptId(getLoginUser().getDeptId());
        productVO.setTenantId(deptUserId);
        Page<ProductVO> voPage = productService.pageProductVO(productVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 查询产品简短列表
     */
    @PreAuthorize("@ss.hasPermi('iot:product:list')")
    @GetMapping("/shortList")
    @ApiOperation("产品简短列表")
    public AjaxResult shortList(ProductVO productVO)
    {
        Boolean showSenior = productVO.getShowSenior();
        if (Objects.isNull(showSenior)){
            // 默认不展示上级产品
            productVO.setShowSenior(false);
        }
        Long deptUserId = getLoginUser().getUser().getDept().getDeptUserId();
        productVO.setIsAdmin(isAdmin(deptUserId));
        productVO.setDeptId(getLoginUser().getDeptId());
        productVO.setTenantId(deptUserId);
        List<IdAndName> list = productService.selectProductShortList(productVO);
        return AjaxResult.success(list);
    }

    /**
     * 导出产品列表
     */
    @PreAuthorize("@ss.hasPermi('iot:product:export')")
    @Log(title = "产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出产品")
    public void export(HttpServletResponse response, ProductVO productVO)
    {
        Page<ProductVO> voPage = productService.pageProductVO(productVO);
        ExcelUtil<ProductVO> util = new ExcelUtil<ProductVO>(ProductVO.class);
        util.exportExcel(response, voPage.getRecords(), "产品数据");
    }

    /**
     * 获取产品详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:product:query')")
    @GetMapping(value = "/{productId}")
    @ApiOperation("获取产品详情")
    public AjaxResult getInfo(@PathVariable("productId") Long productId)
    {
        Product product = productService.selectProductByProductId(productId);
        ProductVO productVO = ProductConvert.INSTANCE.convertProductVO(product);
        Long deptUserId = getLoginUser().getUser().getDept().getDeptUserId();
        boolean admin = isAdmin(SecurityUtils.getUserId());
        productVO.setIsOwner(admin ? 1 : !Objects.equals(product.getTenantId(), deptUserId) ? 0 : 1);
        return AjaxResult.success(productVO);
    }

    /**
     * 新增产品
     */
    @PreAuthorize("@ss.hasPermi('iot:product:add')")
    @Log(title = "添加产品", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加产品")
    public AjaxResult add(@RequestBody ProductVO productVO) throws Exception {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return error(MessageUtils.message("user.not.login"));
        }
        // 查询归属机构
        if (null != loginUser.getDeptId()) {
            productVO.setTenantId(loginUser.getUser().getDept().getDeptUserId());
            productVO.setTenantName(loginUser.getUser().getDept().getDeptName());
        } else {
            productVO.setTenantId(loginUser.getUser().getUserId());
            productVO.setTenantName(loginUser.getUser().getUserName());
        }
        productVO.setCreateBy(getUsername());
        return AjaxResult.success(productService.insertProduct(productVO));
    }

    /**
     * 修改产品
     */
    @PreAuthorize("@ss.hasPermi('iot:product:edit')")
    @Log(title = "修改产品", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改产品")
    public AjaxResult edit(@RequestBody Product product)
    {
        product.setUpdateBy(getUsername());
        SecurityUtils.checkUserOperatePermission(product.getTenantId(), product.getCreateBy());
        return toAjax(productService.updateProduct(product));
    }

    /**
     * 获取产品下面的设备数量
     */
    @PreAuthorize("@ss.hasPermi('iot:product:query')")
    @GetMapping("/deviceCount/{productId}")
    @ApiOperation("获取产品下面的设备数量")
    public AjaxResult deviceCount(@PathVariable Long productId)
    {
        return AjaxResult.success(productService.selectDeviceCountByProductId(productId));
    }

    /**
     * 发布产品
     */
    @PreAuthorize("@ss.hasPermi('iot:product:add')")
    @Log(title = "更新产品状态", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    @ApiOperation("更新产品状态")
    public AjaxResult changeProductStatus(@RequestBody ChangeProductStatusModel model)
    {
        Product product = productService.getById(model.getProductId());
        SecurityUtils.checkUserOperatePermission(product.getTenantId(), product.getCreateBy());
        return productService.changeProductStatus(model);
    }

    /**
     * 删除产品
     */
    @PreAuthorize("@ss.hasPermi('iot:product:remove')")
    @Log(title = "产品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{productIds}")
    @ApiOperation("批量删除产品")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        List<Product> productList = productService.listByIds(Arrays.asList(productIds));
        for (Product product : productList) {
            SecurityUtils.checkUserOperatePermission(product.getTenantId(), product.getCreateBy());
        }
        return productService.deleteProductByProductIds(productIds);
    }


    /**
     * 查询采集点模板关联的所有产品
     */
    @PreAuthorize("@ss.hasPermi('iot:product:list')")
    @GetMapping("/queryByTemplateId")
    @ApiOperation("查询采集点模板id关联的所有产品")
    public AjaxResult queryByTemplateId(@RequestParam Long templateId){
        return AjaxResult.success(productService.selectByTempleId(templateId));
    }


    /**
     * 根据产品id查询Guid
     */
    @PreAuthorize("@ss.hasPermi('iot:product:query')")
    @GetMapping("/getGuid")
    @ApiOperation("根据产品id查询Guid")
    public AjaxResult getGuid(@RequestParam Long productId){
        return AjaxResult.success(productService.selectGuidByProductId(productId));
    }

    /**
     * 复制产品
     */
    @PreAuthorize("@ss.hasPermi('iot:product:add')")
    @Log(title = "复制产品", businessType = BusinessType.INSERT)
    @PostMapping("/copy")
    @ApiOperation("复制产品")
    public AjaxResult copy(Long productId)
    {
        return productService.copy(productId);
    }

    /**
     * 产品导入json文件
     * @param file 文件
     * @return
     */
    @ApiOperation("产品导入json文件")
    @PreAuthorize("@ss.hasPermi('iot:product:add')")
    @PostMapping("/importJson")
    public AjaxResult importJson(MultipartFile file) throws IOException {
        return productService.importJson(file);
    }

    /**
     * 上传面板背景图片
     * @param file 文件
     * @return
     */
    @ApiOperation("上传面板背景图片")
    @PostMapping("/panelBgUpload")
    public AjaxResult panelBgUpload(MultipartFile file) throws IOException {
        return productService.panelBgUpload(file);
    }

    /**
     * 查询已删除产品列表（del_flag = 1）
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:product')")
    @GetMapping("/deleted/list")
    @ApiOperation("查询已删除产品列表")
    public TableDataInfo listDeletedProducts(Product product) {
        Page<ProductVO> voPage = productService.pageDeletedProductVO(product);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 还原已删除的产品及其物模型数据
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:restore')")
    @Log(title = "产品还原", businessType = BusinessType.UPDATE)
    @PutMapping("/restore")
    @ApiOperation("还原已删除的产品及物模型")
    public AjaxResult restoreProduct(Long productId) {
        return productService.restoreProduct(productId);
    }

    /**
     * 删除产品
     */
    @PreAuthorize("@ss.hasPermi('iot:recovery:delete')")
    @Log(title = "产品管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{productIds}")
    @ApiOperation("删除产品")
    public AjaxResult removeProduct(@PathVariable Long[] productIds) {
        int i = productService.deleteProductByIds(productIds);
        return i > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    @GetMapping("/downloadJson")
    public void downloadJson(HttpServletResponse response) {
        try {
            // 1. 构造JSON数据
            String jsonData = "{\n" +
                    "  \"networkMethod\": 2,\n" +
                    "  \"deviceType\": 2,\n" +
                    "  \"vertificateMethod\": 3,\n" +
                    "  \"transport\": \"MQTT\",\n" +
                    "  \"locationWay\": 3,\n" +
                    "  \"productName\": \"\",\n" +
                    "  \"protocolCode\": \"JSON\",\n" +
                    "  \"categoryId\":,\n" +
                    "  \"firmwareType\": 2,\n" +
                    "  \"model\": [{\n" +
                    "    \"identifier\": \"switch\",\n" +
                    "    \"modelOrder\":0,\n" +
                    "    \"type\":2,\n" +
                    "    \"datatype\":\"bool\",\n" +
                    "    \"specs\": {\"type\": \"bool\", \"trueText\": \"打开\", \"falseText\": \"关闭\"},\n" +
                    "    \"modelName\": \"开关\",\n" +
                    "    \"isChart\":0\n" +
                    "  }] \n" +
                    "}";
            byte[] data = jsonData.getBytes(StandardCharsets.UTF_8);

            // 2. 设置响应头（关键修复点）
            response.reset(); // 清除可能存在的缓存头
            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=productJson.json");
            // 设置内容长度
            response.setContentLength(data.length);

            // 3. 写入响应（使用try-with-resources确保关闭）
            try (ServletOutputStream out = response.getOutputStream()) {
                out.write(data);
                out.flush(); // 强制刷新缓冲区
            }
        } catch (IOException e) {
            // 返回错误响应
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("{\"error\":\"文件生成失败\"}");
            } catch (IOException ignored) {

            }
        }
    }
}
