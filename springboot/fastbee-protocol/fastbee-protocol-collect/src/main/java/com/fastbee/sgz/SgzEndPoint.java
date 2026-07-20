package com.fastbee.sgz;

import java.util.List;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;

import com.fastbee.base.core.annotation.Node;
import com.fastbee.base.core.annotation.PakMapping;
import com.fastbee.base.session.Session;
import com.fastbee.common.extend.core.domin.mq.DeviceReport;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;

/**
 * @author gsb
 * @date 2024/3/29 16:06
 */
@Node
@Component
public class SgzEndPoint {

    @PakMapping(types = 150)
    public DeviceReport heart(DeviceReport report, Session session){
        JSONObject params = new JSONObject();
        params.putIfAbsent("device",report.getSerialNumber());
        List<ThingsModelSimpleItem> items = report.getThingsModelSimpleItem();
        for (ThingsModelSimpleItem item : items) {
            if (item.getId().equals("dtype")){
                params.putIfAbsent("dtype",item.getValue());
            }
        }
        params.putIfAbsent("fuc",SgzMessageType.LINKING.type);
        params.putIfAbsent("sdata",SgzMessageType.LINKING.type);
        report.setBody(params);
        return report;
    }

    @PakMapping(types = 154)
    public DeviceReport dataCallBack(DeviceReport report){
        JSONObject params = new JSONObject();
        params.putIfAbsent("device",report.getSerialNumber());
        List<ThingsModelSimpleItem> items = report.getThingsModelSimpleItem();
        for (ThingsModelSimpleItem item : items) {
            if (item.getId().equals("dtype")){
                params.putIfAbsent("dtype",item.getValue());
            }
        }
        params.putIfAbsent("fuc",SgzMessageType.CZROK.type);
        params.putIfAbsent("sdata",SgzMessageType.CZROK.type);
        report.setBody(params);
        return report;
    }

}
