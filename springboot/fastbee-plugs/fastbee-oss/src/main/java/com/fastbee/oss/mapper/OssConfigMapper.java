package com.fastbee.oss.mapper;

import java.util.List;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.oss.domain.OssConfig;

/**
 * 对象存储配置Mapper接口
 *
 * @author zhuangpeng.li
 * @date 2024-04-19
 */
public interface OssConfigMapper extends BaseMapperX<OssConfig>
{

    /**
     * 查询对象存储配置列表
     *
     * @param ossConfig 对象存储配置
     * @return 对象存储配置集合
     */
    public List<OssConfig> selectOssConfigList(OssConfig ossConfig);

    public int resetConfigStatus();
}
