package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.Category;
import com.fastbee.iot.model.IdAndName;
import com.fastbee.iot.model.vo.CategoryVO;

/**
 * 产品分类Mapper接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Repository
public interface CategoryMapper extends BaseMapperX<Category>
{

    /**
     * 查询产品分类列表
     *
     * @param categoryVO 产品分类
     * @return 产品分类集合
     */
    public Page<Category> selectCategoryList(Page<Category> page, @Param("categoryVO") CategoryVO categoryVO);

    /**
     * 查询产品分类列表
     *
     * @param categoryVO 产品分类
     * @return 产品分类集合
     */
    public List<Category> selectCategoryList(@Param("categoryVO") CategoryVO categoryVO);

    /**
     * 查询产品简短分类列表
     *
     * @return 产品分类集合
     */
    public Page<IdAndName> selectCategoryShortList(Page<IdAndName> page, @Param("categoryVO") CategoryVO categoryVO);

    /**
     * 查询产品简短分类列表
     *
     * @return 产品分类集合
     */
    public List<IdAndName> selectCategoryShortList(@Param("categoryVO") CategoryVO categoryVO);

    /**
     * 分类下的产品数量
     *
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int productCountInCategorys(Long[] categoryIds);

}
