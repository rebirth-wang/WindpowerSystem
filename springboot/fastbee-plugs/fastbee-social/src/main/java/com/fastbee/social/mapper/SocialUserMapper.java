package com.fastbee.social.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.social.domain.SocialUser;

/**
 * 用户第三方用户信息Mapper接口
 *
 * @author json
 * @date 2022-04-18
 */
@Repository
public interface SocialUserMapper extends BaseMapperX<SocialUser>
{

    /**
     * 查询用户第三方用户信息列表
     *
     * @param socialUser 用户第三方用户信息
     * @return 用户第三方用户信息集合
     */
    public List<SocialUser> selectSocialUserList(SocialUser socialUser);

    /**
     * 通过unionId查询
     * @param unionId
     * @return
     */
    IPage<Long> selectSysUserIdByUnionId(IPage<Long> page, @Param("unionId") String unionId);

    /**
     * 查询用户绑定微信公众号openId
     * @param userIdList 用户id集合
     * @return java.util.List<com.fastbee.social.domain.SocialUser>
     */
    List<SocialUser> listWechatPublicAccountOpenId(@Param("userIdList") List<String> userIdList);
}
