package com.fastbee.icc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.icc.client.IccHttpClient;
import com.fastbee.icc.exception.IccApiException;
import com.fastbee.icc.model.brm.device.ChannelPageRequest;
import com.fastbee.icc.model.brm.device.ChannelPageResponse;
import com.fastbee.icc.model.brm.device.DevicePageRequest;
import com.fastbee.icc.model.brm.device.DevicePageResponse;
import com.fastbee.icc.model.brm.device.DeviceTreeRequest;
import com.fastbee.icc.model.brm.device.DeviceTreeResponse;
import com.fastbee.media.enums.ChannelStreamType;

/**
 * 大华ICC设备管理服务
 *
 * <p>封装大华ICC基础资源管理（BRM）相关API，提供：
 * <ul>
 *   <li>分页查询设备信息</li>
 *   <li>获取全量设备列表</li>
 *   <li>分页查询通道信息</li>
 *   <li>设备树查询</li>
 * </ul>
 * </p>
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.DEVICEINFO_SERVICE + ChannelStreamType.ICC_DAHUA)
public class IccDeviceService {

    /** 分页查询设备接口路径 */
    private static final String API_DEVICE_PAGE = "/evo-apigw/evo-brm/1.2.0/device/subsystem/page";
    /** 分页查询通道接口路径 */
    private static final String API_CHANNEL_PAGE = "/evo-apigw/evo-brm/1.2.0/device/channel/subsystem/page";
    /** 设备树查询接口路径 */
    private static final String API_DEVICE_TREE = "/evo-apigw/evo-brm/1.0.0/tree";

    /** 默认每页查询数量 */
    private static final int DEFAULT_PAGE_SIZE = 50;

    private final IccHttpClient httpClient;

    public IccDeviceService(IccHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * 分页查询设备信息
     *
     * @param request 分页查询请求
     * @return 分页设备信息
     */
    public DevicePageResponse getDevicePage(DevicePageRequest request) {
        log.info("[大华ICC] 分页查询设备, pageNum={}, pageSize={}",
                request.getPageNum(), request.getPageSize());
        try {
            DevicePageResponse response = httpClient.post(API_DEVICE_PAGE, request, DevicePageResponse.class);
            if (!response.isSuccess()) {
                log.warn("[大华ICC] 分页查询设备失败: {}", response.getErrMsg());
            }
            return response;
        } catch (IccApiException e) {
            log.error("[大华ICC] 分页查询设备异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 获取全量设备信息列表（自动翻页）
     *
     * @return 所有设备信息
     */
    public List<DevicePageResponse.PageVO.DeviceInfoVO> getAllDevices() {
        List<DevicePageResponse.PageVO.DeviceInfoVO> allDevices = new ArrayList<>();
        DevicePageRequest request = new DevicePageRequest();
        request.setPageNum(1);
        request.setPageSize(DEFAULT_PAGE_SIZE);

        DevicePageResponse firstPage = getDevicePage(request);
        if (!firstPage.isSuccess() || firstPage.getData() == null) {
            log.warn("[大华ICC] 获取全量设备失败，返回空列表");
            return Collections.emptyList();
        }

        if (firstPage.getData().getPageData() != null) {
            allDevices.addAll(firstPage.getData().getPageData());
        }

        Integer totalPages = firstPage.getData().getTotalPage();
        if (totalPages != null && totalPages > 1) {
            for (int page = 2; page <= totalPages; page++) {
                request.setPageNum(page);
                DevicePageResponse pageResponse = getDevicePage(request);
                if (pageResponse.isSuccess() && pageResponse.getData() != null
                        && pageResponse.getData().getPageData() != null) {
                    allDevices.addAll(pageResponse.getData().getPageData());
                }
            }
        }

        log.info("[大华ICC] 获取全量设备完成，共{}台设备", allDevices.size());
        return allDevices;
    }

    /**
     * 分页查询通道信息
     *
     * @param request 分页查询请求
     * @return 分页通道信息
     */
    public ChannelPageResponse getChannelPage(ChannelPageRequest request) {
        log.info("[大华ICC] 分页查询通道, pageNum={}, pageSize={}",
                request.getPageNum(), request.getPageSize());
        try {
            ChannelPageResponse response = httpClient.post(API_CHANNEL_PAGE, request, ChannelPageResponse.class);
            if (!response.isSuccess()) {
                log.warn("[大华ICC] 分页查询通道失败: {}", response.getErrMsg());
            }
            return response;
        } catch (IccApiException e) {
            log.error("[大华ICC] 分页查询通道异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 设备树查询
     *
     * @param request 设备树查询请求
     * @return 设备树信息
     */
    public DeviceTreeResponse getDeviceTree(DeviceTreeRequest request) {
        log.info("[大华ICC] 设备树查询");
        try {
            DeviceTreeResponse response = httpClient.post(API_DEVICE_TREE, request, DeviceTreeResponse.class);
            if (!response.isSuccess()) {
                log.warn("[大华ICC] 设备树查询失败: {}", response.getErrMsg());
            }
            return response;
        } catch (IccApiException e) {
            log.error("[大华ICC] 设备树查询异常: {}", e.getMessage(), e);
            throw e;
        }
    }
}
