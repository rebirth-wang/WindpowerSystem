//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;

public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableField(
            exist = false
    )
    @ApiModelProperty("搜索值")
    @JsonIgnore
    private String searchValue;
    @ApiModelProperty("创建者")
    private String createBy;
    @ApiModelProperty("创建时间")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date createTime;
    @ApiModelProperty("更新者")
    private String updateBy;
    @ApiModelProperty("更新时间")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private Date updateTime;
    @ApiModelProperty("备注")
    private String remark;
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;
    @TableField(
            exist = false
    )
    private Integer pageNum;
    @TableField(
            exist = false
    )
    private Integer pageSize;
    @TableField(
            exist = false
    )
    @ApiModelProperty("请求参数")
    @JsonInclude(Include.NON_EMPTY)
    private Map<String, Object> params;

    public <T> Page<T> buildPage() {
        Integer var1 = (Integer)ObjectUtil.defaultIfNull(this.pageNum, 1);
        Integer var2 = (Integer)ObjectUtil.defaultIfNull(this.pageSize, Integer.MAX_VALUE);
        if (var1 <= 0) {
            var1 = 1;
        }

        Page var3 = new Page((long)var1, (long)var2);
        return var3;
    }

    public Integer getPageNum() {
        return (Integer)ObjectUtil.defaultIfNull(this.pageNum, 1);
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return (Integer)ObjectUtil.defaultIfNull(this.pageSize, Integer.MAX_VALUE);
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearchValue() {
        return this.searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Map<String, Object> getParams() {
        if (this.params == null) {
            this.params = new HashMap();
        }

        return this.params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
