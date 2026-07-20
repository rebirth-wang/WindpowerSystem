package com.fastbee.social.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.social.domain.SocialPlatform;
import com.fastbee.social.model.vo.SocialPlatformVO;

/**
 * 第三方登录平台控制Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */
@Mapper
public interface SocialPlatformConvert
{

    SocialPlatformConvert INSTANCE = Mappers.getMapper(SocialPlatformConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param socialPlatform
     * @return 第三方登录平台控制集合
     */
    SocialPlatformVO convertSocialPlatformVO(SocialPlatform socialPlatform);

    /**
     * VO类转换为实体类集合
     *
     * @param socialPlatformVO
     * @return 第三方登录平台控制集合
     */
    SocialPlatform convertSocialPlatform(SocialPlatformVO socialPlatformVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param socialPlatformList
     * @return 第三方登录平台控制集合
     */
    List<SocialPlatformVO> convertSocialPlatformVOList(List<SocialPlatform> socialPlatformList);

    /**
     * VO类转换为实体类
     *
     * @param socialPlatformVOList
     * @return 第三方登录平台控制集合
     */
    List<SocialPlatform> convertSocialPlatformList(List<SocialPlatformVO> socialPlatformVOList);

    /**
     * 分页转换
     * @param socialPlatformPage 实体类
     * @return
     */
    Page<SocialPlatformVO> convertSocialPlatformPage(Page<SocialPlatform> socialPlatformPage);
}
