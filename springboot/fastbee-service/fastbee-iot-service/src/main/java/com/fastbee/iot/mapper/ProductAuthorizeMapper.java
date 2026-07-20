package com.fastbee.iot.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.ProductAuthorize;

/**
 * 产品授权码Mapper接口
 *
 * @author kami
 * @date 2022-04-11
 */
@Repository
public interface ProductAuthorizeMapper extends BaseMapperX<ProductAuthorize>
{

    /**
     * 根据授权码查询一条未绑定的授权码
     * @param authorize
     * @return
     */
    IPage<ProductAuthorize> selectFirstAuthorizeByAuthorizeCode(IPage<ProductAuthorize> page, @Param("authorize") ProductAuthorize authorize);

}
