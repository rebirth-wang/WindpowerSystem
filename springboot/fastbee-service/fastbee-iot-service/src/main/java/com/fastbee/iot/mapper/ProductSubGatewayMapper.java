package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.ProductSubGateway;
import com.fastbee.iot.model.gateWay.ProductSubGatewayVO;

/**
 * 网关与子产品关联Mapper接口
 *
 * @author zhuangpeng.li
 * @date 2024-09-04
 */
public interface ProductSubGatewayMapper extends BaseMapperX<ProductSubGateway>
{

    /**
     * @description: 查询网关
     * @param: productSubGateway 网关
     * @return: java.util.List<com.fastbee.iot.model.gateWay.ProductSubGatewayVO>
     */
    Page<ProductSubGatewayVO> selectListVO(Page<ProductSubGatewayVO> page, @Param("productSubGateway") ProductSubGateway productSubGateway);
}
