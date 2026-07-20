package com.fastbee.sip.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.sip.model.BaseTree;

public interface ISipChannelService extends IService<CommonChannel> {
    public int insertChannel(CommonChannel channel);
    public String insertSipChannelGen(Long createNum, CommonChannel channel);
    public List<BaseTree<CommonChannel>> queryVideoDeviceTree(String deviceId, String parentId, boolean onlyCatalog);
}
