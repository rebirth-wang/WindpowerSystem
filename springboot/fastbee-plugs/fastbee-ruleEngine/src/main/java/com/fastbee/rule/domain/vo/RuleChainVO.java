package com.fastbee.rule.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 规则链对象 rule_chain
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */

@ApiModel(value = "RuleChainVO", description = "规则链 rule_chain")
@Data
public class RuleChainVO{
    /** 代码生成区域 可直接覆盖**/
    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private Long id;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String applicationName;

    /** 链路ID */
    @Excel(name = "链路ID")
    @ApiModelProperty("链路ID")
    private String chainId;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String chainName;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String elData;

    /** 场景状态（1-启动，2-停止） */
    @ApiModelProperty("场景状态")
    @Excel(name = "场景状态")
    private Long enable;

    /** 上下文bean */
    @Excel(name = "上下文bean")
    @ApiModelProperty("上下文bean")
    private String contextBeans;

    /** 创建人 */
    @Excel(name = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新人 */
    @Excel(name = "更新人")
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @Excel(name = "逻辑删除标识")
    @ApiModelProperty("逻辑删除标识")
    private Long delFlag;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
