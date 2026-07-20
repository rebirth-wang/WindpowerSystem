package com.fastbee.iot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.iot.model.speaker.OauthAccessTokenReportVO;
import com.fastbee.iot.model.speaker.OauthClientDetailsReportVO;

/**
 * 音箱Mapper接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Repository
public interface SpeakerMapper
{

    Long selectRelatedIdByProductIdAndUserId(@Param("productId") Long productId, @Param("userId") Long userId);

    List<String> listAttributesByRelatedIdAndIdentifier(@Param("relatedId") Long relatedId, @Param("identifierList") List<String> identifierList);

    OauthAccessTokenReportVO selectOauthAccessTokenByUserNameAndClientId(@Param("userName") String userName, @Param("clientId") String clientId);

    OauthClientDetailsReportVO selectOauthClientDetailsByType(Integer type);
}
