package com.fastbee.social.service.impl;

import static com.fastbee.social.service.impl.SocialLoginServiceImpl.BIND_REDIS_KEY;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.common.constant.HttpStatus;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.enums.SocialPlatformType;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.social.domain.SocialUser;
import com.fastbee.social.domain.UserSocialProfile;
import com.fastbee.social.model.login.BindIdValue;
import com.fastbee.social.service.ISocialUserService;
import com.fastbee.social.service.IUserSocialProfileService;

@Service
public class UserSocialProfileServiceImpl implements IUserSocialProfileService {

    @Resource
    private RedisCache redisCache;
    @Resource
    private ISocialUserService iSocialUserService;

    @Override
    public List<UserSocialProfile> selectUserSocialProfile(Long sysUserId) {
        SocialUser selectSocialUser = new SocialUser();
        selectSocialUser.setSysUserId(sysUserId);
        List<SocialUser> socialUserList = iSocialUserService.selectSocialUserList(selectSocialUser);
        List<UserSocialProfile> userSocialProfileList = new ArrayList<>();
        for (SocialUser socialUser : socialUserList) {
            //如果是删除的标记
            if (socialUser.getDelFlag().equals("1")) {
                continue;
            }
            UserSocialProfile userSocialProfile = new UserSocialProfile();
            userSocialProfile.setSocialUserId(socialUser.getSocialUserId());
            userSocialProfile.setAvatar(socialUser.getAvatar());
            userSocialProfile.setSource(socialUser.getSource());
            userSocialProfile.setUsername(socialUser.getUsername());
            userSocialProfile.setNickname(socialUser.getNickname());
            userSocialProfile.setStatus(socialUser.getStatus());
            userSocialProfile.setSourceClient(socialUser.getSourceClient());
            userSocialProfileList.add(userSocialProfile);
        }
        return userSocialProfileList;
    }

    @Override
    public AjaxResult bindUser(String bindId, Long sysUserId) {
        BindIdValue bindValue = redisCache.getCacheObject(BIND_REDIS_KEY + bindId);
        if (bindValue == null) {
            //不作提示
            return AjaxResult.error(HttpStatus.NO_MESSAGE_ALERT, "未知异常");
        }
        SocialUser socialUser = findSocialUser(bindValue.getUuid(), bindValue.getSource());
        SocialUser updateSocialUser = new SocialUser();
        updateSocialUser.setSocialUserId(socialUser.getSocialUserId());
        updateSocialUser.setSysUserId(sysUserId);
        iSocialUserService.updateSocialUser(updateSocialUser);
        redisCache.deleteObject(BIND_REDIS_KEY + bindId);
        return AjaxResult.success(MessageUtils.message("bind.success"));
    }

    @Override
    public AjaxResult bindSocialAccount(String platform) {
        try {
            SocialPlatformType.valueOf(platform);
        } catch (Exception e) {
            return AjaxResult.error(MessageUtils.message("socialLogin.platform.type.fail"));
        }
        return AjaxResult.success();
    }

    @Override
    public AjaxResult unbindSocialAccount(Long socialUserId, Long sysUserId) {
        SocialUser socialUser = iSocialUserService.selectSocialUserBySocialUserId(socialUserId);
        if (socialUser == null) {
            return AjaxResult.error(MessageUtils.message("bind.account.not.exist"));
        } else if (!socialUser.getSysUserId().equals(socialUserId)) {
            return AjaxResult.error(MessageUtils.message("user.account.and.bind.account.not.match"));
        } else {
            SocialUser updateSocialUser = new SocialUser();
            updateSocialUser.setSocialUserId(socialUserId);
            updateSocialUser.setSysUserId(-1L);
            iSocialUserService.updateSocialUser(updateSocialUser);
            return AjaxResult.success(MessageUtils.message("unbind.success"));
        }
    }

    public SocialUser findSocialUser(String uuid, String source) {
        SocialUser socialUser = new SocialUser();
        socialUser.setSource(source);
        socialUser.setUuid(uuid);
        List<SocialUser> socialUserList = iSocialUserService.selectSocialUserList(socialUser);
        return socialUserList == null || socialUserList.isEmpty() ? null : socialUserList.get(0);

    }
}
