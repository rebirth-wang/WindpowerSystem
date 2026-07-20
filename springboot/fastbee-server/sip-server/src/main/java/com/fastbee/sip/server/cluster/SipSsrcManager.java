package com.fastbee.sip.server.cluster;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * SSRC 分布式池管理器
 * 使用 Redis Set 替代内存 ArrayList，保证多实例 SSRC 不冲突
 */
@Slf4j
@Component
public class SipSsrcManager {

    private static final String SSRC_NOT_USED_PREFIX = "SIP:SSRC:NOT_USED:";
    private static final String SSRC_IS_USED_PREFIX = "SIP:SSRC:IS_USED:";
    private static final int SSRC_POOL_SIZE = 9999;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private volatile String ssrcPrefix;
    private final Object initLock = new Object();

    /**
     * 获取视频预览的SSRC值,第一位固定为0
     */
    public String getPlaySsrc(String domain) {
        return "0" + getSsrcPrefix(domain) + getSN(domain);
    }

    /**
     * 获取录像回放的SSRC值,第一位固定为1
     */
    public String getPlayBackSsrc(String domain) {
        return "1" + getSsrcPrefix(domain) + getSN(domain);
    }

    /**
     * 释放ssrc，用完的ssrc必须释放，否则会耗尽
     */
    public void releaseSsrc(String ssrc) {
        if (ssrc == null || ssrc.length() <= 6) {
            return;
        }
        String prefix = ssrc.substring(1, 6);
        String sn = ssrc.substring(6);
        String notUsedKey = SSRC_NOT_USED_PREFIX + prefix;
        String isUsedKey = SSRC_IS_USED_PREFIX + prefix;
        redisTemplate.opsForSet().move(isUsedKey, sn, notUsedKey);
    }

    private String getSN(String domain) {
        String prefix = getSsrcPrefix(domain);
        String notUsedKey = SSRC_NOT_USED_PREFIX + prefix;
        String isUsedKey = SSRC_IS_USED_PREFIX + prefix;

        // 原子弹出一个可用的SN
        Object sn = redisTemplate.opsForSet().pop(notUsedKey);
        if (sn == null) {
            // 池可能未初始化，执行惰性初始化
            initPool(prefix);
            sn = redisTemplate.opsForSet().pop(notUsedKey);
            if (sn == null) {
                throw new RuntimeException("ssrc已经用完");
            }
        }
        String snStr = sn.toString();
        redisTemplate.opsForSet().add(isUsedKey, snStr);
        return snStr;
    }

    private String getSsrcPrefix(String domain) {
        if (ssrcPrefix == null) {
            synchronized (initLock) {
                if (ssrcPrefix == null) {
                    ssrcPrefix = domain.substring(3, 8);
                    // 检查是否需要初始化池
                    String notUsedKey = SSRC_NOT_USED_PREFIX + ssrcPrefix;
                    Long size = redisTemplate.opsForSet().size(notUsedKey);
                    String isUsedKey = SSRC_IS_USED_PREFIX + ssrcPrefix;
                    Long usedSize = redisTemplate.opsForSet().size(isUsedKey);
                    if ((size == null || size == 0) && (usedSize == null || usedSize == 0)) {
                        initPool(ssrcPrefix);
                    }
                }
            }
        }
        return ssrcPrefix;
    }

    private void initPool(String prefix) {
        String notUsedKey = SSRC_NOT_USED_PREFIX + prefix;
        Long existingSize = redisTemplate.opsForSet().size(notUsedKey);
        String isUsedKey = SSRC_IS_USED_PREFIX + prefix;
        Long usedSize = redisTemplate.opsForSet().size(isUsedKey);
        // 只有当两个池都为空时才初始化，避免重复初始化
        if ((existingSize == null || existingSize == 0) && (usedSize == null || usedSize == 0)) {
            log.info("[SSRC池] 初始化SSRC池, prefix: {}", prefix);
            Object[] sns = new Object[SSRC_POOL_SIZE];
            for (int i = 1; i <= SSRC_POOL_SIZE; i++) {
                sns[i - 1] = String.format("%04d", i);
            }
            redisTemplate.opsForSet().add(notUsedKey, sns);
            log.info("[SSRC池] 初始化完成, 可用数量: {}", SSRC_POOL_SIZE);
        }
    }
}
