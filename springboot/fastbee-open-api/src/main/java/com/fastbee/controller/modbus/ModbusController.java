package com.fastbee.controller.modbus;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.utils.modbus.ListToByteArrayConverter;
import com.fastbee.modbus.tcp.model.ModbusCommand;
import com.fastbee.modbus.tcp.model.vo.BatchModbusCommandVO;
import com.fastbee.modbus.tcp.model.vo.ModbusCommandVO;
import com.fastbee.modbus.tcp.write.ModbusClientManager;

@RestController
@RequestMapping("/api/modbus")
public class ModbusController {
    private final ModbusClientManager clientManager;

    public ModbusController(ModbusClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @PostMapping("/command")
    public CompletableFuture<AjaxResult> sendCommand(@RequestBody ModbusCommandVO vo) {

        ModbusCommand command = new ModbusCommand();
        command.setCode(vo.getCode());
        command.setRegister(vo.getRegister());
        command.setAddress(vo.getAddress());
        command.setQuantity(vo.getQuantity());
        if (vo.getValues() != null && !vo.getValues().isEmpty()) {
            switch (vo.getCode()) {
                case 5:
                case 15:
                    command.setData(ListToByteArrayConverter.convertValuesToByteArray(vo.getValues(), vo.getValues().size()));
                    break;
                case 6:
                case 16:
                    command.setData(ListToByteArrayConverter.convertToByteArray(vo.getValues()));
                    break;
            }
            command.setQuantity(vo.getValues().size());
        }

        return clientManager.executeCommand(vo.getClientId(), command);
    }


    @PostMapping("/batch-command")
    public AjaxResult sendBatchCommands(@RequestBody BatchModbusCommandVO vo) {
        for (BatchModbusCommandVO.ModbusSingleCommand command : vo.getCommands()) {
            clientManager.executeCommand(vo.getClientId(), convertToCommand(command));
        }
        return AjaxResult.success("指令已排队");
    }


    private ModbusCommand convertToCommand(BatchModbusCommandVO.ModbusSingleCommand singleCommand) {
        ModbusCommand command = new ModbusCommand();
        command.setCode(singleCommand.getCode());
        command.setRegister(singleCommand.getRegister());
        command.setQuantity(singleCommand.getQuantity());
        command.setAddress(singleCommand.getAddress());

        if (singleCommand.getValues() != null) {
            switch (singleCommand.getCode()) {
                case 5:
                case 15:
                    command.setData(ListToByteArrayConverter.convertValuesToByteArray(singleCommand.getValues(), singleCommand.getValues().size()));
                    break;
                case 6:
                case 16:
                    command.setData(ListToByteArrayConverter.convertToByteArray(singleCommand.getValues()));
                    break;
            }
        }
        return command;
    }
}
