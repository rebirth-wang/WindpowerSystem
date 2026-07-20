package com.fastbee.iot.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 项目数据关联对象 iot_goview_project_data
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "GoviewProjectData", description = "项目数据关联 iot_goview_project_data")
@Data
@TableName("iot_goview_project_data" )
public class GoviewProjectData extends PageEntity{
    /** 主键 */
    @TableId(value = "id")
    @ApiModelProperty("主键")
    private String id;

    /** 项目id */
    @ApiModelProperty("项目id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private String projectId;

    /** 存储数据 */
    @ApiModelProperty("存储数据")
    private byte[] content;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 创建人id */
    @ApiModelProperty("创建人id")
    private String createBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    public String getDataToStr() {
        byte[] bs = getContent();
        String str = "二进制转换错误";
        str = new String(bs);
        return str;
    }

}
