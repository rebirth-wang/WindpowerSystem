package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ProductExtParam;
import com.fastbee.iot.model.vo.ProductExtParamVO;

/**
 * 产品扩展参数Convert转换类
 *
 * @author fastbee
 * @date 2026-03-18
 */
@Mapper
public interface ProductExtParamConvert
{
    /** 代码生成区域 可直接覆盖**/
    ProductExtParamConvert INSTANCE = Mappers.getMapper(ProductExtParamConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param productExtParam
     * @return 产品扩展参数集合
     */
    ProductExtParamVO convertProductExtParamVO(ProductExtParam productExtParam);

    /**
     * VO类转换为实体类集合
     *
     * @param productExtParamVO
     * @return 产品扩展参数集合
     */
    ProductExtParam convertProductExtParam(ProductExtParamVO productExtParamVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param productExtParamList
     * @return 产品扩展参数集合
     */
    List<ProductExtParamVO> convertProductExtParamVOList(List<ProductExtParam> productExtParamList);

    /**
     * VO类转换为实体类
     *
     * @param productExtParamVOList
     * @return 产品扩展参数集合
     */
    List<ProductExtParam> convertProductExtParamList(List<ProductExtParamVO> productExtParamVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param productExtParamPage
     * @return 产品扩展参数分页
     */
    Page<ProductExtParamVO> convertProductExtParamVOPage(Page<ProductExtParam> productExtParamPage);

    /**
     * VO类转换为实体类
     *
     * @param productExtParamVOPage
     * @return 产品扩展参数分页
     */
    Page<ProductExtParam> convertProductExtParamPage(Page<ProductExtParamVO> productExtParamVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
