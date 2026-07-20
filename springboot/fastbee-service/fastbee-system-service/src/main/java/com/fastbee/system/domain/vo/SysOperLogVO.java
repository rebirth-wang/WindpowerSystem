package com.fastbee.system.domain.vo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 操作日志记录对象 sys_oper_log
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysOperLogVO", description = "操作日志记录 sys_oper_log")
@Data
public class SysOperLogVO extends PageEntity {

    /** 操作日志ID */
    @Excel(name = "操作日志ID")
    @ApiModelProperty("操作日志ID")
    private Long operId;

    /** 模块标题 */
    @Excel(name = "模块标题")
    @ApiModelProperty("模块标题")
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    @ApiModelProperty("业务类型")
    @Excel(name = "业务类型")
    private Long businessType;

    /** 方法名称 */
    @Excel(name = "方法名称")
    @ApiModelProperty("方法名称")
    private String method;

    /** 请求方式 */
    @Excel(name = "请求方式")
    @ApiModelProperty("请求方式")
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    @ApiModelProperty("操作类别")
    @Excel(name = "操作类别")
    private Long operatorType;

    /** 操作人员 */
    @Excel(name = "操作人员")
    @ApiModelProperty("操作人员")
    private String operName;

    /** 部门名称 */
    @Excel(name = "部门名称")
    @ApiModelProperty("部门名称")
    private String deptName;

    /** 请求URL */
    @Excel(name = "请求URL")
    @ApiModelProperty("请求URL")
    private String operUrl;

    /** 主机地址 */
    @Excel(name = "主机地址")
    @ApiModelProperty("主机地址")
    private String operIp;

    /** 操作地点 */
    @Excel(name = "操作地点")
    @ApiModelProperty("操作地点")
    private String operLocation;

    /** 请求参数 */
    @Excel(name = "请求参数")
    @ApiModelProperty("请求参数")
    private String operParam;

    /** 返回参数 */
    @Excel(name = "返回参数")
    @ApiModelProperty("返回参数")
    private String jsonResult;

    /** 操作状态（0正常 1异常） */
    @ApiModelProperty("操作状态")
    @Excel(name = "操作状态")
    private Long status;

    /** 错误消息 */
    @Excel(name = "错误消息")
    @ApiModelProperty("错误消息")
    private String errorMsg;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("操作时间")
    @Excel(name = "操作时间")
    private Date operTime;

    @Setter
    @TableField(exist = false)
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }

    /** 自定义代码区域 **/

    /** 业务类型数组 */
    @ApiModelProperty("业务类型数组")
    private Integer[] businessTypes;

    /** 自定义代码区域 END**/
}
