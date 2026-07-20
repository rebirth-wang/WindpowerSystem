package com.fastbee.oss.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.oss.domain.OssConfig;
import com.fastbee.oss.vo.OssConfigVO;

/**
 * 对象存储配置Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */
@Mapper
public interface OssConfigConvert
{

    OssConfigConvert INSTANCE = Mappers.getMapper(OssConfigConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param ossConfig
     * @return 对象存储配置集合
     */
    OssConfigVO convertOssConfigVO(OssConfig ossConfig);

    /**
     * VO类转换为实体类集合
     *
     * @param ossConfigVO
     * @return 对象存储配置集合
     */
    OssConfig convertOssConfig(OssConfigVO ossConfigVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param ossConfigList
     * @return 对象存储配置集合
     */
    List<OssConfigVO> convertOssConfigVOList(List<OssConfig> ossConfigList);

    /**
     * VO类转换为实体类
     *
     * @param ossConfigVOList
     * @return 对象存储配置集合
     */
    List<OssConfig> convertOssConfigList(List<OssConfigVO> ossConfigVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param ossConfigPage
     * @return 对象存储配置分页
     */
    Page<OssConfigVO> convertOssConfigVOPage(Page<OssConfig> ossConfigPage);

    /**
     * VO类转换为实体类
     *
     * @param ossConfigVOPage
     * @return 对象存储配置分页
     */
    Page<OssConfig> convertOssConfigPage(Page<OssConfigVO> ossConfigVOPage);
}
