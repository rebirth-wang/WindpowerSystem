package com.fastbee.modbus.tcp.config;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.core.domin.mq.message.ModbusDevice;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.modbus.tcp.core.ModbusClient;

@Component
@EnableScheduling
@Slf4j
public class ModbusScheduler {
    private static final ExecutorService businessExecutor = Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors()/2));

    @Autowired
    private ModbusClient modbusClient;
    @Autowired
    private DeviceMapper deviceMapper;
    @Value("${server.modbus-tcp.batch-size}")
    private int BATCH_SIZE;

    @Scheduled(fixedRateString = "${server.modbus-tcp.poll}")
    public void collect() {
        log.debug("开始执行数据采集任务");
        List<ModbusDevice> modbusDevices = deviceMapper.selectAllDevicesWithCommands();
        log.debug("查询到 {} 个设备", modbusDevices.size());

        List<List<ModbusDevice>> batches = Lists.partition(modbusDevices, BATCH_SIZE);
        log.debug("将设备划分为 {} 个批次", batches.size());
        for (List<ModbusDevice> batch : batches) {
            processBatch(batch);
        }

        log.info("数据采集任务执行完成");

    }

    public void processBatch(List<ModbusDevice> batch) {
        log.debug("开始处理批次，批次大小: {}", batch.size());
        batch.parallelStream().forEach(device ->
                CompletableFuture.runAsync(() ->
                        modbusClient.executeDevice(device),businessExecutor));
        log.debug("批次处理完成");
    }
}
