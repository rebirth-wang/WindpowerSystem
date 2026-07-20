package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.Product;
import com.fastbee.iot.model.vo.ProductVO;

/**
 * 产品Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-20
 */
@Mapper
public interface ProductConvert
{
    /** 代码生成区域 可直接覆盖**/
    ProductConvert INSTANCE = Mappers.getMapper(ProductConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param product
     * @return 产品集合
     */
    ProductVO convertProductVO(Product product);

    /**
     * VO类转换为实体类集合
     *
     * @param productVO
     * @return 产品集合
     */
    Product convertProduct(ProductVO productVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param productList
     * @return 产品集合
     */
    List<ProductVO> convertProductVOList(List<Product> productList);

    /**
     * VO类转换为实体类
     *
     * @param productVOList
     * @return 产品集合
     */
    List<Product> convertProductList(List<ProductVO> productVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param productPage
     * @return 产品分页
     */
    Page<ProductVO> convertProductVOPage(Page<Product> productPage);

    /**
     * VO类转换为实体类
     *
     * @param productVOPage
     * @return 产品分页
     */
    Page<Product> convertProductPage(Page<ProductVO> productVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
