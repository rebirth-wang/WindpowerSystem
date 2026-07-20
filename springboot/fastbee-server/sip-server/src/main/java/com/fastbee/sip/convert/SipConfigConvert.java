package com.fastbee.sip.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.sip.domain.SipConfig;
import com.fastbee.sip.model.vo.SipConfigVO;

/**
 * sip系统配置Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-24
 */
@Mapper
public interface SipConfigConvert
{
    /** 代码生成区域 可直接覆盖**/
    SipConfigConvert INSTANCE = Mappers.getMapper(SipConfigConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sipConfig
     * @return sip系统配置集合
     */
    SipConfigVO convertSipConfigVO(SipConfig sipConfig);

    /**
     * VO类转换为实体类集合
     *
     * @param sipConfigVO
     * @return sip系统配置集合
     */
    SipConfig convertSipConfig(SipConfigVO sipConfigVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sipConfigList
     * @return sip系统配置集合
     */
    List<SipConfigVO> convertSipConfigVOList(List<SipConfig> sipConfigList);

    /**
     * VO类转换为实体类
     *
     * @param sipConfigVOList
     * @return sip系统配置集合
     */
    List<SipConfig> convertSipConfigList(List<SipConfigVO> sipConfigVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sipConfigPage
     * @return sip系统配置分页
     */
    Page<SipConfigVO> convertSipConfigVOPage(Page<SipConfig> sipConfigPage);

    /**
     * VO类转换为实体类
     *
     * @param sipConfigVOPage
     * @return sip系统配置分页
     */
    Page<SipConfig> convertSipConfigPage(Page<SipConfigVO> sipConfigVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
