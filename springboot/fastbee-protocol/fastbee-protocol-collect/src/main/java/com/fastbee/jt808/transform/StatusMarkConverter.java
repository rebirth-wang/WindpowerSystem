package com.fastbee.jt808.transform;

import io.netty.buffer.ByteBuf;

import com.fastbee.common.extend.utils.ByteBufUtil;
import com.fastbee.common.extend.utils.jt.JT808Utils;
import com.fastbee.jt808.item.StatusMark;
import com.fastbee.protocol.base.model.WModel;

/**
 * 状态位转换器
 * @author gsb
 * @date 2025/10/29 16:31
 */
public class StatusMarkConverter implements WModel<StatusMark> {

    @Override
    public StatusMark readFrom(ByteBuf input) {
        StatusMark mark = new StatusMark();
        int value = (int) ByteBufUtil.readInt32(input, false);
        mark.setRawValue(value);

        // 根据表17解析各个状态位
        mark.setAccOn(JT808Utils.isTrue(value, 0));
        mark.setLocated(JT808Utils.isTrue(value, 1));
        mark.setSouthLatitude(JT808Utils.isTrue(value, 2));
        mark.setWestLongitude(JT808Utils.isTrue(value, 3));
        mark.setArmed(JT808Utils.isTrue(value, 6));
        mark.setOilCircuitDisconnected(JT808Utils.isTrue(value, 10));
        mark.setMainPowerDisconnected(JT808Utils.isTrue(value, 11));
        mark.setGpsLocated(JT808Utils.isTrue(value, 18));
        mark.setBeidouLocated(JT808Utils.isTrue(value, 19));

        return mark;
    }

    @Override
    public void writeTo(ByteBuf output, StatusMark mark) {
        int value = mark.getRawValue();
        ByteBufUtil.writeInt32(output, value, false);
    }
}
