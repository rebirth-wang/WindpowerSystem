package com.fastbee.jt808.transform;

import io.netty.buffer.ByteBuf;

import com.fastbee.common.extend.utils.ByteBufUtil;
import com.fastbee.common.extend.utils.jt.JT808Utils;
import com.fastbee.jt808.item.WarningMark;
import com.fastbee.protocol.base.model.WModel;

/**
 * 报警标志转换器
 * @author gsb
 * @date 2025/10/29 16:31
 */
public class WarningMarkConverter implements WModel<WarningMark> {

    @Override
    public WarningMark readFrom(ByteBuf input) {
        WarningMark mark = new WarningMark();
        int value = (int) ByteBufUtil.readInt32(input, false);
        mark.setRawValue(value);

        // 根据表18解析各个报警位
        mark.setSos(JT808Utils.isTrue(value, 0));
        mark.setOverSpeed(JT808Utils.isTrue(value, 1));
        mark.setFatigueDriving(JT808Utils.isTrue(value, 2));
        mark.setMainPowerUnderVoltage(JT808Utils.isTrue(value, 7));
        mark.setMainPowerDisconnected(JT808Utils.isTrue(value, 8));
        mark.setBatteryLow(JT808Utils.isTrue(value, 15));
        mark.setVibration(JT808Utils.isTrue(value, 16));
        mark.setRemoval(JT808Utils.isTrue(value, 17));
        mark.setTimeoutParking(JT808Utils.isTrue(value, 19));
        mark.setIllegalMovement(JT808Utils.isTrue(value, 28));

        return mark;
    }

    @Override
    public void writeTo(ByteBuf output, WarningMark mark) {
        int value = mark.getRawValue();
        ByteBufUtil.writeInt32(output, value, false);
    }
}
