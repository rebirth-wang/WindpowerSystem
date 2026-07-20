package com.fastbee.onvif.task;

import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fastbee.onvif.config.OnvifProperties;
import com.fastbee.onvif.service.OnvifAutoDiscoveryService;

/**
 * Startup and scheduled ONVIF local network discovery.
 */
@Slf4j
@Component
@EnableScheduling
public class OnvifAutoDiscoveryTask {

    private final OnvifProperties properties;
    private final OnvifAutoDiscoveryService autoDiscoveryService;

    public OnvifAutoDiscoveryTask(OnvifProperties properties,
                                  OnvifAutoDiscoveryService autoDiscoveryService) {
        this.properties = properties;
        this.autoDiscoveryService = autoDiscoveryService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void scanOnStartup() {
        if (!properties.isScanOnStartup()) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            log.info("[ONVIF auto discovery] Startup scan begin");
            autoDiscoveryService.scanAndSync();
        });
    }

    @Scheduled(cron = "${onvif.scan-cron:0 */5 * * * ?}")
    public void scheduledScan() {
        if (!properties.isScheduledScanEnabled()) {
            return;
        }
        autoDiscoveryService.scanAndSync();
    }
}
