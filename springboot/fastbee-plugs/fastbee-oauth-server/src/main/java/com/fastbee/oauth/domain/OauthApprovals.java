package com.fastbee.oauth.domain;

import java.time.LocalDateTime;

import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 【请填写功能名称】对象 oauth_approvals
 *
 * @author kerwincui
 * @date 2024-03-20
 */
@Data
public class OauthApprovals
{
    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String userid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String clientid;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String scope;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String status;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private LocalDateTime expiresat;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private LocalDateTime lastmodifiedat;
}
