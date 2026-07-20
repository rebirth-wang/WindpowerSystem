package com.fastbee.system.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 岗位信息对象 sys_post
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysPostVO", description = "岗位信息 sys_post")
@Data
public class SysPostVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 岗位ID */
    @Excel(name = "岗位ID")
    @ApiModelProperty("岗位ID")
    private Long postId;

    /** 岗位编码 */
    @Excel(name = "岗位编码")
    @ApiModelProperty("岗位编码")
    private String postCode;

    /** 岗位名称 */
    @Excel(name = "岗位名称")
    @ApiModelProperty("岗位名称")
    private String postName;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    @ApiModelProperty("显示顺序")
    private Long postSort;

    /** 状态（0正常 1停用） */
    @ApiModelProperty("状态")
    @Excel(name = "状态")
    private Long status;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    /** 用户是否存在此岗位标识 默认不存在 */
    private boolean flag = false;

    /** 自定义代码区域 END**/
}
