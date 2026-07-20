package com.fastbee.system.domain.vo;

import lombok.Data;

/**
 * @author admin
 * @version 1.0
 * @description: 用户编辑VO类
 * @date 2024-12-16 10:12
 */
@Data
public class SysUserEditVO {

    private Long userId;

    private String phoneNumber;

    private String code;

    private String password;
}
