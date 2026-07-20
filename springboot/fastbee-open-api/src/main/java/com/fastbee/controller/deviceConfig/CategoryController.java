package com.fastbee.controller.deviceConfig;

import static com.fastbee.system.domain.vo.SysRoleVO.isAdmin;

import java.util.Arrays;
import java.util.List;

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
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.iot.domain.Category;
import com.fastbee.iot.model.vo.CategoryVO;
import com.fastbee.iot.service.ICategoryService;

/**
 * 产品分类Controller
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Api(tags = "产品分类")
@RestController
@RequestMapping("/iot/category")
public class CategoryController extends BaseController
{
    @Resource
    private ICategoryService categoryService;

    /**
     * 查询产品分类列表
     */
    @PreAuthorize("@ss.hasPermi('iot:category:list')")
    @GetMapping("/list")
    @ApiOperation("分类分页列表")
    public TableDataInfo list(CategoryVO categoryVO)
    {
        Page<CategoryVO> voPage = categoryService.pageCategoryVO(categoryVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 查询产品简短分类列表
     */
    @PreAuthorize("@ss.hasPermi('iot:category:list')")
    @GetMapping("/shortlist")
    @ApiOperation("分类简短列表")
    public AjaxResult shortlist(CategoryVO categoryVO)
    {
        return AjaxResult.success(categoryService.selectCategoryShortList(categoryVO).getRecords());
    }

    /**
     * 导出产品分类列表
     */
    @PreAuthorize("@ss.hasPermi('iot:category:export')")
    @Log(title = "产品分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation("导出分类")
    public void export(HttpServletResponse response, CategoryVO categoryVO)
    {
        Page<CategoryVO> voPage = categoryService.pageCategoryVO(categoryVO);
        ExcelUtil<CategoryVO> util = new ExcelUtil<CategoryVO>(CategoryVO.class);
        util.exportExcel(response, voPage.getRecords(), "产品分类数据");
    }

    /**
     * 获取产品分类详细信息
     */
    @ApiOperation("获取分类详情")
    @PreAuthorize("@ss.hasPermi('iot:category:query')")
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        Category category = new Category();
        category.setCategoryId(categoryId);
        return AjaxResult.success(categoryService.selectCategoryByCategoryId(category));
    }

    /**
     * 新增产品分类
     */
    @PreAuthorize("@ss.hasPermi('iot:category:add')")
    @Log(title = "产品分类", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation("添加分类")
    public AjaxResult add(@RequestBody Category category)
    {
        return toAjax(categoryService.insertCategory(category));
    }

    /**
     * 修改产品分类
     */
    @PreAuthorize("@ss.hasPermi('iot:category:edit')")
    @Log(title = "产品分类", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation("修改分类")
    public AjaxResult edit(@RequestBody Category category)
    {
        category.setUpdateBy(getUsername());
        if (1 == category.getIsSys() && !isAdmin(getUserId())) {
            return AjaxResult.error(MessageUtils.message("no.operate.permission"));
        }
        SecurityUtils.checkUserOperatePermission(category.getTenantId(), category.getCreateBy());
        return toAjax(categoryService.updateCategory(category));
    }

    /**
     * 删除产品分类
     */
    @PreAuthorize("@ss.hasPermi('iot:category:remove')")
    @Log(title = "产品分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{categoryIds}")
    @ApiOperation("批量删除分类")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        List<Category> categoryList = categoryService.listByIds(Arrays.asList(categoryIds));
        for (Category category : categoryList) {
            SecurityUtils.checkUserOperatePermission(category.getTenantId(), category.getCreateBy());
        }
        return categoryService.deleteCategoryByCategoryIds(categoryIds);
    }
}
