package com.fastbee.common.extend.core.domin.mq.message;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ModbusDevice {
    @TableId
    private Long deviceId;
    private String serialNumber;
    private String deviceIp;
    private String command;
    private int status;
    private int devicePort;
    private Long productId;
    private List<Command> commands;


}
