//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

public class BaseDO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("创建者")
    private String createBy;
    @ApiModelProperty("创建时间")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime createTime;
    @ApiModelProperty("更新者")
    private String updateBy;
    @ApiModelProperty("更新时间")
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime updateTime;
    @ApiModelProperty("逻辑删除")
    @TableLogic
    private Boolean delFlag;

    public String getCreateBy() {
        return this.createBy;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public Boolean getDelFlag() {
        return this.delFlag;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BaseDO)) {
            return false;
        } else {
            BaseDO var2 = (BaseDO)o;
            if (!var2.canEqual(this)) {
                return false;
            } else {
                Boolean var3 = this.getDelFlag();
                Boolean var4 = var2.getDelFlag();
                if (var3 == null) {
                    if (var4 != null) {
                        return false;
                    }
                } else if (!var3.equals(var4)) {
                    return false;
                }

                String var5 = this.getCreateBy();
                String var6 = var2.getCreateBy();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                LocalDateTime var7 = this.getCreateTime();
                LocalDateTime var8 = var2.getCreateTime();
                if (var7 == null) {
                    if (var8 != null) {
                        return false;
                    }
                } else if (!var7.equals(var8)) {
                    return false;
                }

                String var9 = this.getUpdateBy();
                String var10 = var2.getUpdateBy();
                if (var9 == null) {
                    if (var10 != null) {
                        return false;
                    }
                } else if (!var9.equals(var10)) {
                    return false;
                }

                LocalDateTime var11 = this.getUpdateTime();
                LocalDateTime var12 = var2.getUpdateTime();
                if (var11 == null) {
                    if (var12 != null) {
                        return false;
                    }
                } else if (!var11.equals(var12)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof BaseDO;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        Boolean var3 = this.getDelFlag();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        String var4 = this.getCreateBy();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        LocalDateTime var5 = this.getCreateTime();
        var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
        String var6 = this.getUpdateBy();
        var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
        LocalDateTime var7 = this.getUpdateTime();
        var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
        return var2;
    }

    public String toString() {
        return "BaseDO(createBy=" + this.getCreateBy() + ", createTime=" + this.getCreateTime() + ", updateBy=" + this.getUpdateBy() + ", updateTime=" + this.getUpdateTime() + ", delFlag=" + this.getDelFlag() + ")";
    }
}
