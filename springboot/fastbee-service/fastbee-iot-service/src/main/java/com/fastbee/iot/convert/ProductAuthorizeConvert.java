package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ProductAuthorize;
import com.fastbee.iot.model.vo.ProductAuthorizeVO;

/**
 * 产品授权码Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface ProductAuthorizeConvert {

    /**
     * 代码生成区域 可直接覆盖
     **/
    ProductAuthorizeConvert INSTANCE = Mappers.getMapper(ProductAuthorizeConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param productAuthorize
     * @return 产品授权码集合
     */
    ProductAuthorizeVO convertProductAuthorizeVO(ProductAuthorize productAuthorize);

    /**
     * VO类转换为实体类集合
     *
     * @param productAuthorizeVO
     * @return 产品授权码集合
     */
    ProductAuthorize convertProductAuthorize(ProductAuthorizeVO productAuthorizeVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param productAuthorizeList
     * @return 产品授权码集合
     */
    List<ProductAuthorizeVO> convertProductAuthorizeVOList(List<ProductAuthorize> productAuthorizeList);

    /**
     * VO类转换为实体类
     *
     * @param productAuthorizeVOList
     * @return 产品授权码集合
     */
    List<ProductAuthorize> convertProductAuthorizeList(List<ProductAuthorizeVO> productAuthorizeVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param productAuthorizePage
     * @return 产品授权码分页
     */
    Page<ProductAuthorizeVO> convertProductAuthorizeVOPage(Page<ProductAuthorize> productAuthorizePage);

    /**
     * VO类转换为实体类
     *
     * @param productAuthorizeVOPage
     * @return 产品授权码分页
     */
    Page<ProductAuthorize> convertProductAuthorizePage(Page<ProductAuthorizeVO> productAuthorizeVOPage);
/** 代码生成区域 可直接覆盖END**/

/** 自定义代码区域 **/


/** 自定义代码区域 END**/
}
