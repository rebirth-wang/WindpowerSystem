package com.fastbee.iot.model.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.iot.model.scenemodel.CusDeviceVO;

/**
 * 场景管理对象 scene_model
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneModelVO", description = "场景管理 scene_model")
@Data
public class SceneModelVO extends PageEntity {

    /** 场景管理id */
    @Excel(name = "场景管理id")
    @ApiModelProperty("场景管理id")
    private Long sceneModelId;

    /** 所属租户id */
    @Excel(name = "所属租户id")
    @ApiModelProperty("所属租户id")
    private Long tenantId;

    /** 场景管理名称 */
    @Excel(name = "场景管理名称")
    @ApiModelProperty("场景管理名称")
    private String sceneModelName;

    /** 场景状态 0-停用 1-启用 */
    @Excel(name = "场景状态 0-停用 1-启用")
    @ApiModelProperty("场景状态 0-停用 1-启用")
    private Long status;

    /** 关联的组态id */
    @Excel(name = "关联的组态id")
    @ApiModelProperty("关联的组态id")
    private String guid;

    /** 场景描述 */
    @Excel(name = "场景描述")
    @ApiModelProperty("场景描述")
    private String sceneDesc;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    private String delFlag;

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

    /** 图片地址 */
    @Excel(name = "图片地址")
    @ApiModelProperty("图片地址")
    private String imgUrl;

    /**
     * 机构id
     */
    @Excel(name = "机构id")
    @ApiModelProperty("机构id")
    private Long deptId;

    /**
     * 机构名称
     */
    @Excel(name = "机构名称")
    @ApiModelProperty("机构名称")
    private String deptName;

    /**
     * 关联设备数
     */
    @Excel(name = "关联设备数")
    @ApiModelProperty("关联设备数")
    private Integer deviceTotal;

    /**
     * 关联设备名称
     */
    @Excel(name = "关联设备名称")
    @ApiModelProperty("关联设备名称")
    private List<CusDeviceVO> cusDeviceList;

    /**
     * 关联设备类型
     */
    @Excel(name = "关联设备类型")
    @ApiModelProperty("关联设备类型")
    private List<SceneModelDeviceVO> sceneModelDeviceVOList;
    /**
     * 关联的监控设备
     */
    @Excel(name = "关联的监控设备")
    @ApiModelProperty("关联的监控设备")
    private List<SipRelationVO> sipRelationVOList;

    /**
     * 组态id
     */
    @Excel(name = "组态id")
    @ApiModelProperty("组态id")
    private Long scadaId;


}
