package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.iot.domain.Category;
import com.fastbee.iot.model.IdAndName;
import com.fastbee.iot.model.vo.CategoryVO;

/**
 * 产品分类Service接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
public interface ICategoryService extends IService<Category>
{
    /**
     * 查询产品分类
     *
     * @param category 产品分类
     * @return 产品分类
     */
    public Category selectCategoryByCategoryId(Category category);

    /**
     * 查询产品简短分类列表
     *
     * @return 产品分类集合
     */
    public Page<IdAndName> selectCategoryShortList(CategoryVO categoryVO);

    /**
     * 查询产品分类列表
     *
     * @param categoryVO 产品分类
     * @return 产品分类分页集合
     */
    Page<CategoryVO> pageCategoryVO(CategoryVO categoryVO);

    /**
     * 查询产品分类列表
     *
     * @param category 产品分类
     * @return 产品分类集合
     */
    List<CategoryVO> listCategoryVO(Category category);

    /**
     * 新增产品分类
     *
     * @param category 产品分类
     * @return 结果
     */
    public int insertCategory(Category category);

    /**
     * 修改产品分类
     *
     * @param category 产品分类
     * @return 结果
     */
    public int updateCategory(Category category);

    /**
     * 批量删除产品分类
     *
     * @param categoryIds 需要删除的产品分类主键集合
     * @return 结果
     */
    public AjaxResult deleteCategoryByCategoryIds(Long[] categoryIds);


}
