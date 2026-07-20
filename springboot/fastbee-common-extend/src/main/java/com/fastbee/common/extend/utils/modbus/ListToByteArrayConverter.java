package com.fastbee.common.extend.utils.modbus;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBufUtil;

public class ListToByteArrayConverter {

    /**
     * 将 List<Integer> 转换为 byte[]
     *
     * @param list 输入的整数列表
     * @return 转换后的字节数组
     * @throws IllegalArgumentException 如果输入列表为 null 或为空
     */
    public static byte[] convertToByteArray(List<Integer> list) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (Integer num : list) {
                if (num == null) {
                    throw new IllegalArgumentException("列表中不能包含 null 值");
                }
                if (num < 0 || num > 0xFFFF) {
                    throw new IllegalArgumentException("数值超出范围（0~65535）:" + num);
                }
                 // 高字节（前 8 位）
                byte high = (byte) ((num >> 8) & 0xFF);
                // 低字节（后 8 位）
                byte low = (byte) (num & 0xFF);
                baos.write(high);
                baos.write(low);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("转换失败", e);
        }
    }

    public static byte[] convertValuesToByteArray(List<Integer> values, int quantity) {
        int byteCount = (quantity + 7) / 8;
        byte[] byteArray = new byte[byteCount];
        for (int i = 0; i < quantity; i++) {
            Integer value = values.get(i);
            if (value == null) {
                throw new IllegalArgumentException("Value at index " + i + " is null");
            }
            if (value != 0) { // 非0视为1，设置对应位
                int byteIndex = i / 8;
                int bitIndex = i % 8;
                byteArray[byteIndex] |= (1 << bitIndex);
            }
        }
        return byteArray;
    }

    // 测试方法
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);

        try {
            byte[] bytes = convertToByteArray(list);
            System.out.println(ByteBufUtil.hexDump(bytes)); // 输出 65, 66, 67

            byte[] bytes1 = convertValuesToByteArray(list, list.size());
            System.out.println(ByteBufUtil.hexDump(bytes1));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }


    }
}
