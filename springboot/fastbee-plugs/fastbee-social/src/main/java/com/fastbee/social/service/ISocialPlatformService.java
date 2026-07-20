package com.fastbee.social.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.social.domain.SocialPlatform;
import com.fastbee.social.model.vo.SocialPlatformVO;

/**
 * 第三方登录平台控制Service接口
 *
 * @author json
 * @date 2022-04-12
 */
public interface ISocialPlatformService extends IService<SocialPlatform>
{

    /**
     * 分页查询
     * @param socialPlatform 实体类
     * @return
     */
    Page<SocialPlatformVO> pageSocialPlatformVO(SocialPlatform socialPlatform);

    /**
     * 查询第三方登录平台控制列表
     *
     * @param socialPlatform 第三方登录平台控制
     * @return 第三方登录平台控制集合
     */
    List<SocialPlatformVO> selectSocialPlatformVOList(SocialPlatform socialPlatform);

    /**
     * 新增第三方登录平台控制
     *
     * @param socialPlatform 第三方登录平台控制
     * @return 是否新增成功
     */
    Boolean insertWithCache(SocialPlatform socialPlatform);

    /**
     * 修改第三方登录平台控制
     *
     * @param socialPlatform 第三方登录平台控制
     * @return 是否修改成功
     */
    Boolean updateWithCache(SocialPlatform socialPlatform);

    /**
     * 校验并批量删除第三方登录平台控制信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);

    /**
     * 查询第三方登录平台控制
     *
     * @param socialPlatformId 主键
     * @return 第三方登录平台控制
     */
    SocialPlatform queryByIdWithCache(Long socialPlatformId);

    /**
     * 查询第三方登录平台控制
     *
     * @param platform 第三方登录平台名称
     * @return 第三方登录平台控制
     */
    public SocialPlatform selectSocialPlatformByPlatform(String platform);
}
