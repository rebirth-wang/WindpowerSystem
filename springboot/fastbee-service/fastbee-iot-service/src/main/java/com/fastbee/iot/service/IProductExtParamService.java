package com.fastbee.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.ProductExtParam;
import com.fastbee.iot.model.vo.ProductExtParamVO;

/**
 * 产品扩展参数Service接口
 *
 * @author fastbee
 * @date 2026-03-18
 */
public interface IProductExtParamService extends IService<ProductExtParam>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询产品扩展参数列表
     *
     * @param productExtParam 产品扩展参数
     * @return 产品扩展参数分页集合
     */
    Page<ProductExtParamVO> pageProductExtParamVO(ProductExtParam productExtParam);

    /**
     * 查询产品扩展参数列表
     *
     * @param productExtParam 产品扩展参数
     * @return 产品扩展参数集合
     */
    List<ProductExtParamVO> listProductExtParamVO(ProductExtParam productExtParam);

    /**
     * 查询产品扩展参数
     *
     * @param paramId 主键
     * @return 产品扩展参数
     */
     ProductExtParam selectProductExtParamById(Long paramId);

    /**
     * 查询产品扩展参数
     *
     * @param productExtParam 产品扩展参数
     * @return 产品扩展参数
     */
    ProductExtParam queryByIdWithCache(ProductExtParam productExtParam);

    /**
     * 新增产品扩展参数
     *
     * @param productExtParam 产品扩展参数
     * @return 是否新增成功
     */
    Boolean insertWithCache(ProductExtParam productExtParam);

    /**
     * 修改产品扩展参数
     *
     * @param productExtParam 产品扩展参数
     * @return 是否修改成功
     */
    Boolean updateWithCache(ProductExtParam productExtParam);

    /**
     * 校验并批量删除产品扩展参数信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/

}
