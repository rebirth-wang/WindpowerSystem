package com.fastbee.sip.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.sip.domain.SipConfig;

/**
 * sip系统配置Mapper接口
 *
 * @author zhuangpeng.li
 * @date 2022-11-30
 */
@Repository
public interface SipConfigMapper extends BaseMapperX<SipConfig>
{
    /**
     * 查询产品下第一条sip系统配置
     *
     * @return sip系统配置
     */
    public SipConfig selectSipConfigByProductId(Long productId);
    /**
     * 查询sip系统配置列表
     *
     * @param sipConfig sip系统配置
     * @return sip系统配置集合
     */
    public List<SipConfig> selectSipConfigList(SipConfig sipConfig);

    public int resetDefaultSipConfig();
}
