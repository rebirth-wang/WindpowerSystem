package com.fastbee.icc.demo.ipms.blackWhiteListManage;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.model.ipms.blackWhiteListManage.*;

/**
 * @className BlacklistManageDemoTest
 * @Author 355079
 * @Date 2024/12/9
 * @Description
 */
@Slf4j
@Data
public class BlackWhiteListManageDemoTest {
    private BlackWhiteListManageDemo blackWhiteListManageDemo;
    public BlackWhiteListManageDemoTest(){
        blackWhiteListManageDemo = new BlackWhiteListManageDemo();
    }

    /**
     * 测试下发设备白名单
     */
    @Test
    public void testUpdateWhiteList() {
        List<UpdateWhiteListParam> updateWhiteListRequest = new ArrayList<>();
        UpdateWhiteListParam updateWhiteListParam1 = new UpdateWhiteListParam();
        updateWhiteListParam1.setCarId("1846070000130007042");
        updateWhiteListParam1.setDeviceCodes("1003225");
        updateWhiteListParam1.setValidStartDay("2024-12-09 00:00:00");
        updateWhiteListParam1.setValidEndDay("2024-12-10 00:00:00");
        updateWhiteListRequest.add(updateWhiteListParam1);
        UpdateWhiteListParam updateWhiteListParam2 = new UpdateWhiteListParam();
        updateWhiteListParam2.setCarId("1859863959163654146");
        updateWhiteListParam2.setDeviceCodes("1003226");
        updateWhiteListParam2.setValidStartDay("2024-12-10 00:00:00");
        updateWhiteListParam2.setValidEndDay("2024-12-11 00:00:00");
        updateWhiteListRequest.add(updateWhiteListParam2);
        blackWhiteListManageDemo.updateWhiteList(updateWhiteListRequest);
    }

    /**
     * 测试查询任务状态
     */
    @Test
    public void testQueryTaskStatus() {
        QueryTaskStatusRequest queryTaskStatusRequest = new QueryTaskStatusRequest();
        queryTaskStatusRequest.setPageNum(1);
        queryTaskStatusRequest.setPageSize(20);
        queryTaskStatusRequest.setCarNum("A112233");
        blackWhiteListManageDemo.queryTaskStatus(queryTaskStatusRequest);
    }

    /**
     * 测试查询白名单信息
     */
    @Test
    public void testQueryWhiteListInfo() {
        QueryWhiteListInfoRequest queryWhiteListInfoRequest = new QueryWhiteListInfoRequest();
        queryWhiteListInfoRequest.setPageNum(1);
        queryWhiteListInfoRequest.setPageSize(20);
        queryWhiteListInfoRequest.setCarNumLikeStr("浙A112233");
        blackWhiteListManageDemo.queryWhiteListInfo(queryWhiteListInfoRequest);
    }

    /**
     * 测试删除从白名单中删除指定车辆
     */
    @Test
    public void testDeleteWhiteList() {
        List<String> carIds = new ArrayList<>();
        carIds.add("1846070000130007042");
        blackWhiteListManageDemo.deleteWhiteList(carIds);
    }

    /**
     * 测试下发设备黑名单
     */
    @Test
    public void testUpdateBlackList() {
        List<UpdateBlackListParam> updateBlackListRequest = new ArrayList<>();
        UpdateBlackListParam updateBlackListParam1 = new UpdateBlackListParam();
        updateBlackListParam1.setCarId("1846070000130007042");
        updateBlackListParam1.setDeviceCodes("1003225");
        updateBlackListParam1.setValidStartDay("2024-12-09 00:00:00");
        updateBlackListParam1.setValidEndDay("2024-12-10 00:00:00");
        updateBlackListRequest.add(updateBlackListParam1);
        UpdateBlackListParam updateBlackListParam2 = new UpdateBlackListParam();
        updateBlackListParam2.setCarId("1859863959163654146");
        updateBlackListParam2.setDeviceCodes("1003225");
        updateBlackListParam2.setValidStartDay("2024-12-10 00:00:00");
        updateBlackListParam2.setValidEndDay("2024-12-11 00:00:00");
        updateBlackListRequest.add(updateBlackListParam2);
        blackWhiteListManageDemo.updateBlackList(updateBlackListRequest);
    }

    /**
     * 测试查询黑名单信息
     */
    @Test
    public void testQueryBlackListInfo() {
        QueryBlackListInfoRequest queryBlackListInfoRequest = new QueryBlackListInfoRequest();
        queryBlackListInfoRequest.setPageNum(1);
        queryBlackListInfoRequest.setPageSize(20);
        queryBlackListInfoRequest.setCarNumLikeStr("浙A112233");
        blackWhiteListManageDemo.queryBlackListInfo(queryBlackListInfoRequest);
    }

    /**
     * 测试删除从黑名单中删除指定车辆
     */
    @Test
    public void testDeleteBlackList() {
        List<String> carIds = new ArrayList<>();
        carIds.add("1846070000130007042");
        blackWhiteListManageDemo.deleteBlackList(carIds);
    }
}
