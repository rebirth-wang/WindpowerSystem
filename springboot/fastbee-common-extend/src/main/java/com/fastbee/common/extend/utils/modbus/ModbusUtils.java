package com.fastbee.common.extend.utils.modbus;

import java.util.List;

import com.fastbee.common.extend.core.protocol.modbus.ModbusCode;

/**
 * @author gsb
 * @date 2024/6/15 11:21
 */
public class ModbusUtils {

    /**
     * 获取modbus功能码
     * isReadOnly: 0-读写 1-只读
     * type: 1-IO寄存器 2-数据寄存器
     * IO寄存器读写 05功能码 数据寄存器只读 06功能码
     * @param type modbus数据类型
     * @return modbus功能码
     */
    public static ModbusCode getModbusCode(int type){
            if (type == 1){
                return ModbusCode.Write05;
            }else {
                return ModbusCode.Write06;
            }
    }

    public static ModbusCode getReadModbusCode(int type,int isReadOnly){
        if (type == 1){
            return isReadOnly == 1 ? ModbusCode.Read02 : ModbusCode.Read01;
        }else {
            return isReadOnly == 1 ? ModbusCode.Read04 : ModbusCode.Read03;
        }
    }

    public static int getReadCount(String hexResponse) {
        try {
            String hex = hexResponse.replaceAll("\\s+|:", "").toUpperCase();
            // 提取功能码（第2个字节）
            int funcCode = Integer.parseInt(hex.substring(2, 4), 16);
            // 提取字节数（第3个字节）
            int byteCount = Integer.parseInt(hex.substring(4, 6), 16);
            // 计算读取个数
            if (funcCode == 0x01 || funcCode == 0x02) {
                return byteCount * 8;      // 线圈/离散输入
            } else if (funcCode == 0x03 || funcCode == 0x04) {
                return byteCount / 2;      // 保持寄存器
            }
        } catch (Exception e) {

        }
        return -1;
    }

    /**
     * 获取modbus-hex字符串寄存器地址
     * @param hexString hex字符串
     * @return 寄存器地址-10进制
     */
    public static int getModbusAddress(String hexString){
        return Integer.parseInt(hexString.substring(4, 8),16);
    }

    /**
     * 获取从机地址
     * @param hexString
     * @return
     */
    public static int getModbusSlaveId(String hexString){
        return Integer.parseInt(hexString.substring(0,2),16);
    }

    /**
     * 获取功能码
     * @param hexString
     * @return
     */
    public static int getModbusCode(String hexString){
        return Integer.parseInt(hexString.substring(2,4),16);
    }

    public static ModbusParams getModbusParams(String hexString){
        ModbusParams mparams = new ModbusParams();
        mparams.setSlaveId(Integer.parseInt(hexString.substring(0,2),16));
        mparams.setCode(Integer.parseInt(hexString.substring(2,4),16));
        mparams.setAddress(Integer.parseInt(hexString.substring(4, 8),16));
        mparams.setLength(Integer.parseInt(hexString.substring(8, 12),16));
        return mparams;
    }


    public static void main(String[] args) {
        ModbusParams modbusParams = getModbusParams("03030000000AC42F");
        System.out.println(modbusParams);
        int readCount = getReadCount("02 01 01 01 90 0C");
        System.out.println(readCount);
    }

    public static byte[] getModbusCommandData(List<Integer> values, int functionCode) {
        if (values != null) {
            switch (functionCode) {
                case 5:
                case 1:
                    return ListToByteArrayConverter.convertValuesToByteArray(values, values.size());
                case 6:
                case 3:
                    return ListToByteArrayConverter.convertToByteArray(values);
                default:
                    return new byte[0];
            }
        }
        return new byte[0];
    }
}
