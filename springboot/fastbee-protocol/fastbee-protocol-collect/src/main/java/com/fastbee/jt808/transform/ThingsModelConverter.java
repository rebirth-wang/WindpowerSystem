package com.fastbee.jt808.transform;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import io.netty.buffer.ByteBuf;

import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.jt808.item.StatusMark;
import com.fastbee.jt808.item.T0200;
import com.fastbee.jt808.item.WarningMark;

/**
 * 物模型转换器
 * @author gsb
 * @date 2025/10/29 16:31
 */
public class ThingsModelConverter {

    // 用于跟踪已经处理过的对象，避免循环引用
    private static final ThreadLocal<Set<Object>> processedObjects = new ThreadLocal<>();

    // 需要排除的类列表
    private static final Set<Class<?>> EXCLUDED_CLASSES = new HashSet<>(Arrays.asList(
            ByteBuf.class
            // 可以在这里添加其他需要排除的类
    ));

    public static List<ThingsModelSimpleItem> beanToThingsModel(Object object) throws IllegalAccessException {
        // 初始化已处理对象集合
        processedObjects.set(new HashSet<>());
        try {
            return beanToThingsModelInternal(object);
        } finally {
            // 清理ThreadLocal，避免内存泄漏
            processedObjects.remove();
        }
    }

    private static List<ThingsModelSimpleItem> beanToThingsModelInternal(Object object) throws IllegalAccessException {
        List<ThingsModelSimpleItem> result = new ArrayList<>();

        if (object == null) {
            return result;
        }

        // 检查是否已经处理过该对象，避免循环引用
        Set<Object> processed = processedObjects.get();
        if (processed.contains(object)) {
            return result;
        }
        processed.add(object);

        // 获取当前类和所有父类的字段
        Class<?> currentClass = object.getClass();
        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                // 跳过静态字段
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                // 跳过transient字段
                if (Modifier.isTransient(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                Object value = field.get(object);

                // 特殊处理T0200消息
                if (object instanceof T0200) {
                    T0200 location = (T0200) object;

                    // 为经纬度创建专门的转换项
                    if ("latitude".equals(field.getName())) {
                        String preciseLat = calculatePreciseCoordinate(location.getLatitude());
                        addPreciseCoordinateItem(result, "latitude", preciseLat);
                        continue;
                    }

                    if ("longitude".equals(field.getName())) {
                        String preciseLng = calculatePreciseCoordinate(location.getLongitude());
                        addPreciseCoordinateItem(result, "longitude", preciseLng);
                        continue;
                    }

                    // 特殊处理报警标志和状态位
                    if ("warningMark".equals(field.getName()) && value != null) {
                        WarningMark warningMark = location.getWarningMark();
                        addWarningMarkItems(result, warningMark);
                        continue;
                    }

                    if ("statusMark".equals(field.getName()) && value != null) {
                        StatusMark statusMark = location.getStatusMark();
                        addStatusMarkItems(result, statusMark);
                        continue;
                    }

                    // 处理其他字段
                    if ("altitude".equals(field.getName())) {
                        addIntegerItem(result, "altitude", location.getAltitude());
                        continue;
                    }

                    if ("speed".equals(field.getName())) {
                        // 速度单位转换：1/10km/h -> km/h
                        addIntegerItem(result, "speed", location.getSpeed() / 10);
                        continue;
                    }

                    if ("direction".equals(field.getName())) {
                        addIntegerItem(result, "direction", location.getDirection());
                        continue;
                    }

                    if ("deviceTime".equals(field.getName()) && value != null) {
                        addStringItem(result, "deviceTime", location.getDeviceTime().toString());
                        continue;
                    }
                }

                // 通用处理：递归处理嵌套对象
                if (shouldRecurse(value)) {
                    // 递归处理嵌套对象
                    List<ThingsModelSimpleItem> nestedItems = beanToThingsModelInternal(value);
                    for (ThingsModelSimpleItem nestedItem : nestedItems) {
                        nestedItem.setId(field.getName() + "." + nestedItem.getId());
                        result.add(nestedItem);
                    }
                } else {
                    // 基本类型直接添加
                    ThingsModelSimpleItem item = new ThingsModelSimpleItem();
                    item.setId(field.getName());
                    item.setValue(value != null ? formatValue(value) : "null");
                    item.setTs(new Date());
                    result.add(item);
                }
            }
            // 获取父类继续处理
            currentClass = currentClass.getSuperclass();
        }

        return result;
    }

    /**
     * 判断是否应该递归处理该对象
     */
    private static boolean shouldRecurse(Object value) {
        if (value == null) {
            return false;
        }

        Class<?> clazz = value.getClass();

        // 如果是基本类型或包装类型，不递归
        if (isPrimitiveOrWrapper(clazz)) {
            return false;
        }

        // 如果是常见的不需要递归的类型，不递归
        if (value instanceof String ||
                value instanceof Date ||
                value instanceof LocalDateTime ||
                value instanceof Enum) {
            return false;
        }

        // 检查是否在排除列表中
        for (Class<?> excludedClass : EXCLUDED_CLASSES) {
            if (excludedClass.isAssignableFrom(clazz)) {
                return false;
            }
        }

        // 检查包名，排除一些已知的不需要递归的包
        String packageName = clazz.getPackage() != null ? clazz.getPackage().getName() : "";
        if (packageName.startsWith("java.") ||
                packageName.startsWith("javax.") ||
                packageName.startsWith("sun.") ||
                packageName.startsWith("com.sun.") ||
                packageName.startsWith("io.netty.")) {
            return false;
        }

        return true;
    }

    /**
     * 格式化值，特别处理枚举和布尔值
     */
    private static String formatValue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value ? "1" : "0";
        } else if (value instanceof Enum) {
            // 对于枚举，返回其名称而不是序号
            return ((Enum<?>) value).name();
        }
        return value.toString();
    }

    /**
     * 检查是否为基本类型或包装类型
     */
    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Boolean.class ||
                clazz == Character.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class ||
                clazz == Void.class;
    }

    /**
     * 添加报警标志的详细项（按照物模型标识符）
     */
    private static void addWarningMarkItems(List<ThingsModelSimpleItem> result, WarningMark warningMark) {
        if (warningMark == null) return;

        addBooleanItem(result, "sosAlarm", warningMark.isSos());
        addBooleanItem(result, "overspeedAlarm", warningMark.isOverSpeed());
        addBooleanItem(result, "fatigueDrivingAlarm", warningMark.isFatigueDriving());
        addBooleanItem(result, "lowVoltageAlarm", warningMark.isMainPowerUnderVoltage());
        addBooleanItem(result, "powerDisconnectAlarm", warningMark.isMainPowerDisconnected());
        addBooleanItem(result, "batteryLowAlarm", warningMark.isBatteryLow());
        addBooleanItem(result, "vibrationAlarm", warningMark.isVibration());
        addBooleanItem(result, "removalAlarm", warningMark.isRemoval());
        addBooleanItem(result, "overtimeParkingAlarm", warningMark.isTimeoutParking());
        addBooleanItem(result, "illegalMovementAlarm", warningMark.isIllegalMovement());
        addValueItem(result, "warningMark_raw", warningMark.getRawValue());
    }

    /**
     * 添加状态位的详细项（按照物模型标识符）
     */
    private static void addStatusMarkItems(List<ThingsModelSimpleItem> result, StatusMark statusMark) {
        if (statusMark == null) return;

        addBooleanItem(result, "accStatus", statusMark.isAccOn());
        addBooleanItem(result, "locationStatus", statusMark.isLocated());
        addBooleanItem(result, "latDirection", statusMark.isSouthLatitude());
        addBooleanItem(result, "lngDirection", statusMark.isWestLongitude());
        addBooleanItem(result, "defenseStatus", statusMark.isArmed());
        addBooleanItem(result, "fuelCircuitStatus", statusMark.isOilCircuitDisconnected());
        addBooleanItem(result, "mainPowerStatus", statusMark.isMainPowerDisconnected());
        addBooleanItem(result, "gpsLocationFlag", statusMark.isGpsLocated());
        addBooleanItem(result, "beidouLocationFlag", statusMark.isBeidouLocated());
        addValueItem(result, "statusMark_raw", statusMark.getRawValue());
    }


    /**
     * 添加高精度坐标项
     */
    private static void addPreciseCoordinateItem(List<ThingsModelSimpleItem> result, String id, String value) {
        ThingsModelSimpleItem item = new ThingsModelSimpleItem();
        item.setId(id);
        item.setValue(value);
        item.setTs(new Date());
        result.add(item);
    }

    private static String calculatePreciseCoordinate(int coordinate) {
        return new BigDecimal(coordinate)
                .divide(new BigDecimal(1000000), 8, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 添加布尔值项（返回0/1）
     */
    private static void addBooleanItem(List<ThingsModelSimpleItem> result, String id, boolean value) {
        ThingsModelSimpleItem item = new ThingsModelSimpleItem();
        item.setId(id);
        item.setValue(value ? "1" : "0"); // 按照要求返回0/1
        item.setTs(new Date());
        result.add(item);
    }

    /**
     * 添加数值项
     */
    private static void addValueItem(List<ThingsModelSimpleItem> result, String id, Object value) {
        ThingsModelSimpleItem item = new ThingsModelSimpleItem();
        item.setId(id);
        item.setValue(value != null ? value.toString() : "null");
        item.setTs(new Date());
        result.add(item);
    }

    /**
     * 添加整数项
     */
    private static void addIntegerItem(List<ThingsModelSimpleItem> result, String id, int value) {
        ThingsModelSimpleItem item = new ThingsModelSimpleItem();
        item.setId(id);
        item.setValue(String.valueOf(value));
        item.setTs(new Date());
        result.add(item);
    }

    /**
     * 添加小数项
     */
    private static void addDecimalItem(List<ThingsModelSimpleItem> result, String id, double value) {
        ThingsModelSimpleItem item = new ThingsModelSimpleItem();
        item.setId(id);
        item.setValue(String.valueOf(value));
        item.setTs(new Date());
        result.add(item);
    }

    /**
     * 添加字符串项
     */
    private static void addStringItem(List<ThingsModelSimpleItem> result, String id, String value) {
        ThingsModelSimpleItem item = new ThingsModelSimpleItem();
        item.setId(id);
        item.setValue(value != null ? value : "null");
        item.setTs(new Date());
        result.add(item);
    }
}
