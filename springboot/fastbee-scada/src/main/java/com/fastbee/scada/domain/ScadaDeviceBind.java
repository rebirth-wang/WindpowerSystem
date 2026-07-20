package com.fastbee.scada.domain;

import com.fastbee.common.core.domain.PageEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 组态设备关联对象 scada_device_bind
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScadaDeviceBind", description = "组态设备关联 scada_device_bind")
@Data
@TableName("scada_device_bind" )
public class ScadaDeviceBind extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** id唯一标识 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id唯一标识")
    private Long id;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 组态guid */
    @ApiModelProperty("组态guid")
    private String scadaGuid;

}
