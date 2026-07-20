package com.fastbee.controller.device;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.fastbee.iot.model.vo.DeviceVO;

/**
 * OpenAPI response envelopes for device-management integration contracts.
 */
public final class DeviceContractSchemas
{
    private DeviceContractSchemas()
    {
    }

    @ApiModel(value = "DevicePageResponse", description = "RuoYi paged response for device list APIs.")
    public static class DevicePageResponse
    {
        @ApiModelProperty(value = "Business code. 200 means success; non-200 means business failure.", example = "200")
        public Integer code;

        @ApiModelProperty(value = "Business message.", example = "query success")
        public String msg;

        @ApiModelProperty("Current page device rows.")
        public List<DeviceVO> rows;

        @ApiModelProperty(value = "Total matched devices.", example = "42")
        public Long total;
    }

    @ApiModel(value = "DeviceDetailResponse", description = "RuoYi AjaxResult response for one device.")
    public static class DeviceDetailResponse
    {
        @ApiModelProperty(value = "Business code. 200 means success; non-200 means business failure.", example = "200")
        public Integer code;

        @ApiModelProperty(value = "Business message.", example = "operation success")
        public String msg;

        @ApiModelProperty("Device detail. Empty when the device does not exist or caller has no visible data.")
        public DeviceVO data;
    }

    @ApiModel(value = "DeviceWriteResponse", description = "RuoYi AjaxResult response for create/update device APIs.")
    public static class DeviceWriteResponse
    {
        @ApiModelProperty(value = "Business code. 200 means success; non-200 means business failure.", example = "200")
        public Integer code;

        @ApiModelProperty(value = "Business message.", example = "operation success")
        public String msg;

        @ApiModelProperty(value = "Write result flag or created identifier, depending on service implementation.", example = "1")
        public Integer data;
    }

    @ApiModel(value = "DeviceDeleteResponse", description = "RuoYi AjaxResult response for delete device APIs.")
    public static class DeviceDeleteResponse
    {
        @ApiModelProperty(value = "Business code. 200 means success; non-200 means business failure.", example = "200")
        public Integer code;

        @ApiModelProperty(value = "Business message. Delete failures include blocking scene/model references.", example = "operation success")
        public String msg;
    }
}
