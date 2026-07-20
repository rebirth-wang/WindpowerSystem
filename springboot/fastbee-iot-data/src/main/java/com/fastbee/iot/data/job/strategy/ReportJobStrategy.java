package com.fastbee.iot.data.job.strategy;

import java.util.Objects;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.core.domin.notify.ReportNotifyParams;
import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.service.IReportService;
import com.fastbee.notify.core.service.NotifySendService;

/**
 * 场景运算
 * @author gsb
 * @date 2025/3/18 15:42
 */
@Component
@Slf4j
public class ReportJobStrategy implements JobInvokeStrategy{

    @Resource
    private IReportService reportService;
    @Resource
    private NotifySendService notifySendService;

    @Override
    public boolean supports(int jobType) {
        return DeviceJobTypeEnum.REPORT.getType() == jobType;
    }

    @Override
    public void invoke(DeviceJob job) {
        System.out.println("------------------[定时执行报表运算]---------------------");
        Boolean result = null;
        try {
            ReportNotifyParams reportNotifyParams = reportService.executeJob(job.getDatasourceId());
            if (Objects.isNull(reportNotifyParams)) {
                log.info("报表执行失败，未查询到报表，报表编号：" + job.getDatasourceId());
            } else {
                notifySendService.reportSend(reportNotifyParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("------------------[定时执行报表运算，报表编号：" + job.getDatasourceId() + ", 结果：" + result + "]---------------------");
    }
}
