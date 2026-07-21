//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.gateway.mq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fastbee.common.enums.TopicType;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.collection.CollectionUtils;

@Component
public class TopicsUtils {
    private static final Logger bh = LoggerFactory.getLogger(TopicsUtils.class);
    @Value("${server.broker.enabled}")
    private Boolean enabled;

    public String buildTopic(Long productId, String serialNumber, TopicType type) {
        String var4 = String.valueOf(productId);
        if (null == productId || productId == -1L || productId == 0L) {
            var4 = "+";
        }

        if (StringUtils.isEmpty(serialNumber)) {
            serialNumber = "+";
        }

        if (type.getType() == 0) {
            if (this.enabled) {
                return "/" + var4 + "/" + serialNumber + type.getTopicSuffix();
            } else {
                return !TopicType.FETCH_UPGRADE_REPLY.equals(type) && !TopicType.HTTP_UPGRADE_REPLY.equals(type) ? "/+/+" + type.getTopicSuffix() : "/+" + type.getTopicSuffix();
            }
        } else {
            return "/" + var4 + "/" + serialNumber + type.getTopicSuffix();
        }
    }

    public String buildTopic(String serialNumber, TopicType type) {
        if (StringUtils.isEmpty(serialNumber)) {
            serialNumber = "+";
        }

        if (type.getType() == 0) {
            if (this.enabled) {
                return "/" + serialNumber + type.getTopicSuffix();
            } else {
                return !TopicType.FETCH_UPGRADE_REPLY.equals(type) && !TopicType.HTTP_UPGRADE_REPLY.equals(type) ? "/+/+" + type.getTopicSuffix() : "/+" + type.getTopicSuffix();
            }
        } else {
            return "/" + serialNumber + type.getTopicSuffix();
        }
    }

    public TopicsPost getAllPost() {
        ArrayList<String> var2 = new ArrayList<>();   // 明确指定泛型
        ArrayList<Integer> var1 = new ArrayList<>();  // 明确指定泛型
        TopicsPost var3 = new TopicsPost();

        for (TopicType var7 : TopicType.values()) {
            if (var7.getType() == 0) {
                String var8 = this.buildTopic(0L, (String) null, var7);
                var2.add(var8);
                var1.add(1);
            }
        }

        var3.setTopics(var2.toArray(new String[0]));

        // 修正点：使用 Integer::intValue 替代 Integer::valueOf
        int[] var9 = var1.stream().mapToInt(Integer::intValue).toArray();
        var3.setQos(var9);

        return var3;
    }

    public static List<Topics> getAllGet(boolean isSimulate) {
        ArrayList var1 = new ArrayList();

        for(TopicType var5 : TopicType.values()) {
            if (var5.getType() == 4) {
                Topics var6 = new Topics();
                var6.setTopicName(var5.getTopicSuffix());
                var6.setDesc(var5.getMsg());
                var6.setQos(1);
                var1.add(var6);
                if (isSimulate && var5 == TopicType.PROPERTY_GET) {
                    var1.remove(var6);
                }
            }
        }

        return var1;
    }

    public String topicSubDevice(String orgTopic, Long productId, String serialNumber) {
        if (StringUtils.isEmpty(orgTopic)) {
            return orgTopic;
        } else {
            String[] var4 = orgTopic.split("/");
            StringBuilder var5 = (new StringBuilder(var4[0])).append("/").append(productId).append("/").append(serialNumber);

            for(int var6 = 3; var6 < var4.length; ++var6) {
                var5.append("/").append(var4[var6]);
            }

            return var5.toString();
        }
    }

    public Long parseProductId(String topic) {
        try {
            String[] var2 = topic.split("/");
            return Long.parseLong(var2[1]);
        } catch (Throwable var3) {
            throw var3;
        }
    }

    public String parseSerialNumber(String topic) {
        try {
            String[] var2 = topic.split("/");
            String var3 = (String)Arrays.stream(var2).filter((var0) -> var0.length() > 9).findFirst().get();
            return var3;
        } catch (Throwable var4) {
            throw var4;
        }
    }

    public String parseSerialNumber2(String topic) {
        if (topic == null) {
            return "";
        } else {
            String[] var2 = topic.split("/", -1);
            String var3 = "";
            if (var2.length > 2) {
                var3 = var2[1];
            }

            return var3;
        }
    }

    public String parseTopicName(String topic) {
        String[] var2 = topic.split("/");
        if (var2.length > 2) {
            return !topic.contains(TopicType.FETCH_UPGRADE_REPLY.getTopicSuffix()) && !topic.contains(TopicType.HTTP_UPGRADE_REPLY.getTopicSuffix()) ? "/" + var2[3] + "/" + var2[4] : "/" + var2[2] + "/" + var2[3] + "/" + var2[4];
        } else {
            return null;
        }
    }

    public String parseTopicName4(String topic) {
        String[] var2 = topic.split("/");
        return var2[4];
    }

    public String getThingsModel(String topic) {
        try {
            String[] var2 = topic.split("/");
            return var2[2].toUpperCase();
        } catch (Throwable var3) {
            throw var3;
        }
    }

    public static boolean validTopicFilter(List<String> topicNameList) {
        for(String var2 : topicNameList) {
            if (StringUtils.isEmpty(var2)) {
                return false;
            }

            if (org.springframework.util.StringUtils.startsWithIgnoreCase(var2, "#") || org.springframework.util.StringUtils.startsWithIgnoreCase(var2, "+") || org.springframework.util.StringUtils.endsWithIgnoreCase(var2, "/") || !var2.contains("/")) {
                return false;
            }

            if (var2.contains("#")) {
                if (!org.springframework.util.StringUtils.endsWithIgnoreCase(var2, "/#")) {
                    return false;
                }

                if (org.springframework.util.StringUtils.countOccurrencesOf(var2, "#") > 1) {
                    return false;
                }
            }

            if (var2.contains("+") && org.springframework.util.StringUtils.countOccurrencesOf(var2, "+") != org.springframework.util.StringUtils.countOccurrencesOf(var2, "/+")) {
                return false;
            }
        }

        return true;
    }

    public static boolean matchTopic(String topic, String topicFilter) {
        if (!topic.contains("+") && !topic.contains("#")) {
            return topic.equals(topicFilter);
        } else {
            String[] var2 = topic.split("/");
            String[] var3 = topicFilter.split("/");
            if (!topic.contains("#") && var2.length < var3.length) {
                return false;
            } else {
                for(int var5 = 0; var5 < var2.length; ++var5) {
                    String var4 = var2[var5];
                    if (!var4.equals(var3[var5]) && !var4.equals("+") && !var4.equals("#")) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public static List<String> searchTopic(String topic) {
        try {
            ArrayList var1 = new ArrayList();
            var1.add(topic);
            String[] var2 = topic.split("/");
            int[] var3 = new int[var2.length];
            String var4 = "";

            for(int var5 = 0; var5 < var2.length; var3[var5] = var5++) {
                String var6 = var4.concat("#");
                var1.add(var6);
                var4 = var4.concat(var2[var5]).concat("/");
            }

            Map<List<Integer>, Boolean>  var12 = handle(var3);
            for(List<Integer> var7 : var12.keySet()) {
                String[] var8 = CollectionUtils.copy(var2);

                for(Integer var10 : var7) {
                    var8[var10] = "+";
                }

                String var14 = CollectionUtils.concat(var8, "/");
                var1.add(var14);
            }

            return var1;
        } catch (Exception var11) {
            bh.error("=>查询topic异常", var11);
            return null;
        }
    }

    public static Map<List<Integer>, Boolean> handle(int[] src) {
        int var1 = src.length;
        int var2 = -1 >>> 32 - var1;
        HashMap var3 = new HashMap();

        for(int var4 = 1; var4 <= var2; ++var4) {
            ArrayList var5 = new ArrayList();

            for(int var6 = 0; var6 < var1; ++var6) {
                if (var4 << 31 - var6 >> 31 == -1) {
                    var5.add(var6);
                }
            }

            var3.put(var5, true);
        }

        return var3;
    }

    public static String buildSceneReportTopic(Long sceneModelId, Long sceneModelDeviceId) {
        return "/" + sceneModelId + "/" + sceneModelDeviceId + "/scene/report";
    }

    public static String buildRuleEngineTopic(String requestId) {
        return "/" + requestId + "/ruleengine/test";
    }
}
