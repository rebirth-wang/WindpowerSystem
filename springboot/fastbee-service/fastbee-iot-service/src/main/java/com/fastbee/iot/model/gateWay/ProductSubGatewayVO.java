package com.fastbee.iot.model.gateWay;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 网关与子产品关联对象 iot_product_sub_gateway
 *
 * @author zhuangpeng.li
 * @date 2024-09-04
 */

@Data
public class ProductSubGatewayVO implements Serializable{
    private static final long serialVersionUID=1L;

    /** 业务id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("业务id")
    private Long id;

    /** 网关产品id */
    private Long gwProductId;

    /** 子产品id */
    private Long subProductId;

    /**
     * 子产品名称
     */
    private String subProductName;

    /** 从机地址 */
    private String address;

    /** 备注 */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 子产品定位方式
     */
    private Integer subLocationWay;

}
