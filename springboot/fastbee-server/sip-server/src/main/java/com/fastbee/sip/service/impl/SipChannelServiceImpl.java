package com.fastbee.sip.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.*;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.service.IProductService;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.mapper.CommonChannelMapper;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.mapper.SipDeviceMapper;
import com.fastbee.sip.model.BaseTree;
import com.fastbee.sip.service.ISipChannelService;
import com.fastbee.sip.util.SipUtil;

@Slf4j
@Service
public class SipChannelServiceImpl extends ServiceImpl<CommonChannelMapper, CommonChannel> implements ISipChannelService {
    @Resource
    private SipDeviceMapper sipDeviceMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private IProductService productService;

    @Override
    public String insertSipChannelGen(Long createNum, CommonChannel sipDeviceChannel) {
        String newDevID = sipDeviceChannel.getDeviceId() + this.getDeviceSipId(sipDeviceChannel.getDeviceId());
        sipDeviceChannel.setDeviceId(newDevID);
        this.addChannelSipId(createNum, sipDeviceChannel, null);
        return newDevID;
    }

    private String getDeviceSipId(String deviceId) {
        Integer maxIndex = 0;
        String cacheKey = RedisKeyBuilder.buildSipDeviceidCacheKey(deviceId);
        Set<Integer> indexSet = redisCache.getCacheSet(cacheKey);
        if (indexSet != null && !indexSet.isEmpty()) {
            maxIndex = Collections.max(indexSet);
        } else {
            indexSet = new TreeSet<>();
        }
        // 更新set到缓存
        try {
            maxIndex++;
            indexSet.add(maxIndex);
            redisCache.setCacheSet(cacheKey, indexSet);
        } catch (Exception e) {
            log.warn("Failed to update Redis cache", e);
        }
        return String.format("%06d", maxIndex);
    }

    private int addChannelSipId(Long createNum, CommonChannel channel, Integer sIndex) {
        // 限制最大添加数量为50
        if (createNum > 50) {
            createNum = 50L;
        }
        int created = 0;
        Set<Integer> indexSet = new TreeSet<>();
        ;
        String baseChannelId = channel.getChannelId();
        String cacheKey = RedisKeyBuilder.buildSipChannelidCacheKey(baseChannelId);
        if (sIndex == null) {
            indexSet = redisCache.getCacheSet(cacheKey);
            if (indexSet != null && !indexSet.isEmpty()) {
                sIndex = Collections.max(indexSet);
            } else {
                sIndex = 0;
            }
        }

        for (int i = 1; created < createNum; i++) {
            Integer cIndex = sIndex + i;
            String newChannelId = baseChannelId + String.format("%06d", cIndex);
            CommonChannel existingChannel = this.lambdaQuery().eq(CommonChannel:: getChannelId,newChannelId).one();
            if (existingChannel == null) {
                try {
                    channel.setChannelId(newChannelId);
                    int ret = insertChannel(channel);
                    if (ret > 0) {
                        created++;
                        indexSet.add(cIndex);
                    }
                } catch (Exception e) {
                    // Handle insertion failure
                    log.warn("Failed to insert SipDeviceChannel: {}", e.getMessage());
                }
            }
        }
        redisCache.setCacheSet(cacheKey, indexSet);
        return created;
    }

    @Override
    public int insertChannel(CommonChannel channel) {
        Product product = productService.getProductBySerialNumber(channel.getDeviceId());
        if (product != null) {
            channel.setProductId(product.getProductId());
        }
        if(channel.getChannelName() == null) {
            channel.setChannelName(channel.getChannelId());
        }
        channel.setTenantId(getLoginUser().getDeptId());
        SysUser user = getLoginUser().getUser();
        channel.setTenantId(getLoginUser().getDeptUserId());
        channel.setCreateBy(user.getUserName());
        channel.setCreateTime(DateUtils.getNowDate());
        return baseMapper.insert(channel);
    }


    private int insertChannelByDevice(Device dev, CommonChannel channel) {
        Product product = productService.getProductBySerialNumber(channel.getDeviceId());
        if (product != null) {
            channel.setProductId(product.getProductId());
        }
        channel.setTenantId(dev.getTenantId());
        channel.setCreateTime(DateUtils.getNowDate());
        return baseMapper.insert(channel);
    }


    public List<BaseTree<CommonChannel>> queryVideoDeviceTree(String deviceId, String parentId, boolean onlyCatalog) {
        SipDevice device = sipDeviceMapper.selectSipDeviceBySipId(deviceId);
        if (device == null) {
            return null;
        }
        if (parentId == null || parentId.equals(deviceId)) {
            // 字根节点开始查询
            List<CommonChannel> rootNodes = getRootNodes(deviceId, true, !onlyCatalog);
            return transportChannelsToTree(rootNodes, "");
        }

        if (parentId.length() % 2 != 0) {
            return null;
        }
        if (parentId.length() == 10) {
            if (onlyCatalog) {
                return null;
            }
            // parentId为行业编码， 其下不会再有行政区划
            List<CommonChannel> channels = baseMapper.selectChannelByCivilCode(deviceId, parentId);
            return transportChannelsToTree(channels, parentId);
        }
        // 查询其下的行政区划和摄像机
        List<CommonChannel> channelsForCivilCode = baseMapper.selectChannelWithCivilCodeAndLength(deviceId, parentId, parentId.length() + 2);
        if (!onlyCatalog) {
            List<CommonChannel> channels = baseMapper.selectChannelByCivilCode(deviceId, parentId);

            for (CommonChannel channel : channels) {
                boolean flag = false;
                for (CommonChannel deviceChannel : channelsForCivilCode) {
                    if (channel.getChannelId().equals(deviceChannel.getChannelId())) {
                        flag = true;
                    }
                }
                if (!flag) {
                    channelsForCivilCode.add(channel);
                }
            }
        }
        return transportChannelsToTree(channelsForCivilCode, parentId);
    }

    private List<CommonChannel> getRootNodes(String deviceId, boolean haveCatalog, boolean haveChannel) {
        if (!haveCatalog && !haveChannel) {
            return null;
        }
        List<CommonChannel> result = new ArrayList<>();
        // 使用行政区划
        Integer length = baseMapper.getChannelMinLength(deviceId);
        if (length == null) {
            return null;
        }
        if (length <= 10) {
            if (haveCatalog) {
                List<CommonChannel> provinceNode = baseMapper.selectChannelWithCivilCodeAndLength(deviceId, null, length);
                if (provinceNode != null && !provinceNode.isEmpty()) {
                    result.addAll(provinceNode);
                }
            }
            if (haveChannel) {
                // 查询那些civilCode不在通道中的不规范通道，放置在根目录
                List<CommonChannel> nonstandardNode = baseMapper.selectChannelWithoutCiviCode(deviceId);
                if (nonstandardNode != null && !nonstandardNode.isEmpty()) {
                    result.addAll(nonstandardNode);
                }
            }
        } else {
            if (haveChannel) {
                List<CommonChannel> deviceChannels = this.lambdaQuery().eq(CommonChannel::getDeviceId, deviceId).list();
                if (deviceChannels != null && !deviceChannels.isEmpty()) {
                    result.addAll(deviceChannels);
                }
            }
        }
        return result;
    }

    private List<BaseTree<CommonChannel>> transportChannelsToTree(List<CommonChannel> channels, String parentId) {
        if (channels == null) {
            return null;
        }
        List<BaseTree<CommonChannel>> treeNotes = new ArrayList<>();
        if (channels.isEmpty()) {
            return treeNotes;
        }
        for (CommonChannel channel : channels) {
            BaseTree<CommonChannel> node = new BaseTree<>();
            node.setId(channel.getChannelId());
            node.setDeviceId(channel.getDeviceId());
            node.setName(channel.getChannelName());
            node.setPid(parentId);
            node.setBasicData(channel);
            node.setParent(false);
            if (channel.getChannelId().length() > 8) {
                if (channel.getChannelId().length() > 13) {
                    String gbCodeType = channel.getChannelId().substring(10, 13);
                    node.setParent(gbCodeType.equals(SipUtil.BUSINESS_GROUP) || gbCodeType.equals(SipUtil.VIRTUAL_ORGANIZATION));
                }
            } else {
                node.setParent(true);
            }
            treeNotes.add(node);
        }
        Collections.sort(treeNotes);
        return treeNotes;
    }
}
