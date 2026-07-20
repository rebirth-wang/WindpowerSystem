package com.fastbee.icc.model.brm.department;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * @className DepartmentAddResponse
 * @Author 355079
 * @Date 2025/3/21
 * @Description 部门添加返回结果
 */
@Data
public class DepartmentAddResponse extends IccResponse {
    private Data data;
    @lombok.Data
    public static class Data {
        /** 部门id */
        private Long id;
    }

}
