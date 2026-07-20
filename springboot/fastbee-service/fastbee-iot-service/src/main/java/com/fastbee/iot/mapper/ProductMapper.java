package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.model.vo.ProductVO;

/**
 * 产品Mapper接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Repository
public interface ProductMapper extends BaseMapperX<Product>
{

    /**
     * 根据模板id查询所有使用的产品
     * @param templeId 模板id
     * @return
     */
    public List<Product> selectByTempleId(Long templeId);

    /**
     * 查询终端用户产品
     * @param productVO
     * @return java.util.List<com.fastbee.iot.domain.Product>
     */
    Page<ProductVO> pageTerminalUserProduct(Page<ProductVO> page, @Param("productVO") ProductVO productVO);

    /**
     * 查询组态信息
     * @param guidList 组态id集合
     * @return java.util.List<com.fastbee.iot.domain.Product>
     */
    List<ProductVO> selectListScadaIdByGuidS(@Param("guidList") List<String> guidList);

    int deleteProductById(Long productId);

    Page<ProductVO> selectDelProductVO(Page<Product> page, @Param("product") Product product);


    Product selectProductById(Long productId);

    int restoreProduct(Long productId);
}
