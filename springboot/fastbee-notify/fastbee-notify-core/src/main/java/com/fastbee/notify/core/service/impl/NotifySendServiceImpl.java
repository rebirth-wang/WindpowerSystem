package com.fastbee.notify.core.service.impl;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fastbee.common.constant.HttpStatus;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.notify.AlertPushParams;
import com.fastbee.common.extend.core.domin.notify.NotifySendResponse;
import com.fastbee.common.extend.core.domin.notify.ReportNotifyParams;
import com.fastbee.common.extend.core.domin.notify.WorkOrderNotifyParams;
import com.fastbee.common.extend.core.domin.notify.msg.HttpMsgParams;
import com.fastbee.common.extend.enums.NotifyChannelEnum;
import com.fastbee.common.extend.enums.NotifyChannelProviderEnum;
import com.fastbee.common.extend.enums.NotifyServiceCodeEnum;
import com.fastbee.common.extend.utils.ForestHttpUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.ValidationUtils;
import com.fastbee.common.utils.VerifyCodeUtils;
import com.fastbee.common.utils.wechat.AesException;
import com.fastbee.common.utils.wechat.WXBizMsgCrypt;
import com.fastbee.iot.model.vo.CardVO;
import com.fastbee.notify.core.dingtalk.service.DingTalkService;
import com.fastbee.notify.core.email.service.EmailService;
import com.fastbee.notify.core.service.NotifySendService;
import com.fastbee.notify.core.sms.service.ISmsService;
import com.fastbee.notify.core.vo.SendParams;
import com.fastbee.notify.core.voice.service.VoiceService;
import com.fastbee.notify.core.wechat.service.WeChatPushService;
import com.fastbee.notify.domain.NotifyChannel;
import com.fastbee.notify.domain.NotifyLog;
import com.fastbee.notify.domain.NotifyTemplate;
import com.fastbee.notify.mapper.NotifyChannelMapper;
import com.fastbee.notify.service.INotifyChannelService;
import com.fastbee.notify.service.INotifyLogService;
import com.fastbee.notify.service.INotifyTemplateService;
import com.fastbee.notify.vo.NotifyVO;
import com.fastbee.system.mapper.SysUserMapper;

/**
 * @author fastb
 * @version 1.0
 * @description: 通知业务类
 * @date 2023-12-26 10:38
 */
@Slf4j
@Service
public class NotifySendServiceImpl implements NotifySendService {

    @Resource
    private NotifyChannelMapper notifyChannelMapper;
    @Resource
    private INotifyTemplateService notifyTemplateService;
    @Resource
    private INotifyChannelService notifyChannelService;
    @Resource
    private INotifyLogService notifyLogService;
    @Resource
    private ISmsService smsService;
    @Resource
    private VoiceService voiceService;
    @Resource
    private EmailService emailService;
    @Resource
    private WeChatPushService weChatPushService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private DingTalkService dingTalkService;
    @Resource
    private SysUserMapper sysUserMapper;
    @Lazy
    @Resource
    @Qualifier("notifyExecutor")
    private Executor notifyExecutor;

    @Override
    public AjaxResult send(SendParams sendParams) {
        // 获取配置参数
        NotifyTemplate notifyTemplate = notifyTemplateService.getById(sendParams.getId());
        NotifyChannel notifyChannel = notifyChannelMapper.selectById(notifyTemplate.getChannelId());
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(sendParams.getVariables())) {
            map = JSONObject.parseObject(sendParams.getVariables(), LinkedHashMap.class);
        }
        NotifyChannelProviderEnum notifyChannelProviderEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(notifyChannel.getChannelType(), notifyChannel.getProvider());
        NotifyVO notifyVO = new NotifyVO();
        notifyVO.setNotifyChannel(notifyChannel).setNotifyTemplate(notifyTemplate)
                .setSendAccount(sendParams.getSendAccount()).setMap(map).setNotifyChannelProviderEnum(notifyChannelProviderEnum);
        return this.notifySend(notifyVO);
    }

    @Override
    public AjaxResult notifySend(NotifyVO notifyVO) {
        // 获取发送参数
        NotifyChannel notifyChannel = notifyVO.getNotifyChannel();
        NotifyTemplate notifyTemplate = notifyVO.getNotifyTemplate();
        String sendAccount = notifyVO.getSendAccount();
        if (StringUtils.isNotEmpty(notifyVO.getSendAccount())) {
            String s = notifyVO.getSendAccount().replaceAll("，", ",");
            sendAccount = s;
            notifyVO.setSendAccount(s);
        }
        NotifyChannelEnum notifyChannelEnum = NotifyChannelEnum.getNotifyChannelEnum(notifyChannel.getChannelType());
        // 组装模板内容参数，发送通知
        NotifySendResponse notifySendResponse = new NotifySendResponse();
        switch (Objects.requireNonNull(notifyChannelEnum)) {
            case SMS:
                notifySendResponse = smsService.send(notifyVO);
                break;
            case EMAIL:
                notifySendResponse = emailService.send(notifyVO);
                break;
            case VOICE:
                notifySendResponse = voiceService.send(notifyVO);
                break;
            case WECHAT:
                notifySendResponse = weChatPushService.send(notifyVO);
                break;
            case DING_TALK:
                notifySendResponse = dingTalkService.send(notifyVO);
                break;
            case HTTP:
                notifySendResponse = this.httpSend(notifyVO);
                break;
            default:
                break;
        }
        // 保存日志
        NotifyLog notifyLog = new NotifyLog();
        notifyLog.setChannelId(notifyChannel.getId()).setNotifyTemplateId(notifyTemplate.getId())
                .setSendAccount(StringUtils.isNotEmpty(notifySendResponse.getOtherSendAccount()) ? notifySendResponse.getOtherSendAccount() :  sendAccount)
                .setServiceCode(notifyTemplate.getServiceCode())
                .setMsgContent(notifySendResponse.getSendContent())
                .setSendStatus(Long.valueOf(notifySendResponse.getStatus())).setResultContent(notifySendResponse.getResultContent())
                .setTenantId(notifyTemplate.getTenantId()).setTenantName(notifyTemplate.getTenantName()).setCreateBy(notifyTemplate.getCreateBy());
        notifyLogService.insertNotifyLog(notifyLog);
        return 1 == notifySendResponse.getStatus() ? AjaxResult.success() : AjaxResult.error(notifySendResponse.getResultContent());
    }

    private NotifySendResponse httpSend(NotifyVO notifyVO) {
        NotifySendResponse notifySendResponse = new NotifySendResponse();
        NotifyTemplate notifyTemplate = notifyVO.getNotifyTemplate();
        HttpMsgParams httpMsgParams = JSONObject.parseObject(notifyTemplate.getMsgParams(), HttpMsgParams.class);
        if (Objects.isNull(httpMsgParams) || StringUtils.isEmpty(httpMsgParams.getHostUrl())) {
            notifySendResponse.setStatus(0);
            notifySendResponse.setSendContent("未配置请求信息或请求地址为空");
            return notifySendResponse;
        }
        String requestHeaders = httpMsgParams.getRequestHeaders();
        Map<String, String> requestHeaderMap = new HashMap<>(2);
        if (StringUtils.isNotEmpty(requestHeaders)) {
            requestHeaderMap = JSONObject.parseObject(requestHeaders, new TypeReference<Map<String, String>>() {});
        }

        String requestParams = httpMsgParams.getRequestParams();
        if (StringUtils.isNotEmpty(requestParams)) {
            requestParams = StringUtils.strReplaceVariable("${", "}", requestParams, notifyVO.getMap());
        }
        Map<String, String> requestParamMap = JSONObject.parseObject(requestParams, new TypeReference<Map<String, String>>() {});

        String requestBody = httpMsgParams.getRequestBody();
        if (StringUtils.isNotEmpty(requestBody)) {
            requestBody = StringUtils.strReplaceVariable("${", "}", requestBody, notifyVO.getMap());
        }

        String method = httpMsgParams.getMethod();
        String result = "";
        try {
            if ("get".equals(method)) {
                result = ForestHttpUtils.get(httpMsgParams.getHostUrl(), requestHeaderMap, requestParamMap);
            } else if ("post".equals(method)) {
                if (StringUtils.isNotEmpty(requestBody)) {
                    result = ForestHttpUtils.postJson(httpMsgParams.getHostUrl(), requestHeaderMap, requestBody);
                } else {
                    result = ForestHttpUtils.postForm(httpMsgParams.getHostUrl(), requestHeaderMap, requestParamMap);
                }
            } else {
                if (StringUtils.isNotEmpty(requestBody)) {
                    result = ForestHttpUtils.putJson(httpMsgParams.getHostUrl(), requestHeaderMap, requestBody);
                } else {
                    result = ForestHttpUtils.putForm(httpMsgParams.getHostUrl(), requestHeaderMap, requestParamMap);
                }
            }
            notifySendResponse.setStatus(1);
            notifySendResponse.setResultContent(result);
        } catch (Exception e) {
            log.error("消息通知Http推送失败", e);
            notifySendResponse.setStatus(0);
            notifySendResponse.setResultContent(e.getMessage());
        }
        return notifySendResponse;
    }

    @Override
    public String alertSend(AlertPushParams alertPushParams) {
        NotifyTemplate notifyTemplate = notifyTemplateService.getByIdWithCache(alertPushParams.getNotifyTemplateId());
        if (Objects.isNull(notifyTemplate) || 0 == notifyTemplate.getStatus()) {
            log.info("告警关联通知模版未启用，模版编号：{}", alertPushParams.getNotifyTemplateId());
            return "";
        }
        NotifyChannel notifyChannel = notifyChannelService.getByIdWithCache(notifyTemplate.getChannelId());
        NotifyChannelProviderEnum notifyChannelProviderEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(notifyChannel.getChannelType(), notifyChannel.getProvider());
        NotifyVO notifyVO = new NotifyVO();
        notifyVO.setNotifyChannel(notifyChannel).setNotifyTemplate(notifyTemplate).setNotifyChannelProviderEnum(notifyChannelProviderEnum);
        JSONObject jsonMsgParams = JSONObject.parseObject(notifyVO.getNotifyTemplate().getMsgParams());
        String content;
        if (jsonMsgParams.containsKey("content")) {
            content = jsonMsgParams.get("content").toString();
        } else if (StringUtils.isNotEmpty(jsonMsgParams.get("requestBody").toString())) {
            content = jsonMsgParams.get("requestBody").toString();
        } else {
            content = jsonMsgParams.get("requestParams").toString();
        }
        List<String> variables = notifyTemplateService.listVariablesWithCache(content, notifyChannelProviderEnum);
        assert notifyChannelProviderEnum != null;
        NotifyChannelEnum notifyChannelEnum = NotifyChannelEnum.getNotifyChannelEnum(notifyChannelProviderEnum.getChannelType());
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        switch (Objects.requireNonNull(notifyChannelEnum)) {
            case SMS:
            case EMAIL:
            case WECHAT:
            case DING_TALK:
            case HTTP:
                for (int i = 0; i < variables.size(); i++) {
                    if (i == 0) {
                        map.put(variables.get(i), alertPushParams.getDeviceName());
                    } else if (i == 1) {
                        map.put(variables.get(i), alertPushParams.getSerialNumber());
                    } else if (i == 2) {
                        map.put(variables.get(i), alertPushParams.getAddress());
                    } else if (i == 3) {
                        map.put(variables.get(i), alertPushParams.getAlertName());
                    } else if (i == 4) {
                        map.put(variables.get(i), alertPushParams.getIdentify());
                    } else if (i == 5) {
                        map.put(variables.get(i), alertPushParams.getValue());
                    } else {
                        map.put(variables.get(i), alertPushParams.getAlertTime());
                    }
                }
                break;
            case VOICE:
                for (int i = 0; i < variables.size(); i++) {
                    if (i == 0) {
                        map.put(variables.get(i), alertPushParams.getDeviceName());
                    } else {
                        map.put(variables.get(i), alertPushParams.getAddress());
                    }
                }
                break;
            case MQTT:
                for (int i = 0; i < variables.size(); i++) {
                    if (i == 0) {
                        map.put(variables.get(i), alertPushParams.getSerialNumber());
                    } else if (i == 1) {
                        map.put(variables.get(i), alertPushParams.getAddress());
                    } else if (i == 2) {
                        map.put(variables.get(i), alertPushParams.getValue());
                    } else if (i == 3) {
                        map.put(variables.get(i), alertPushParams.getMatchValue());
                    } else {
                        map.put(variables.get(i), alertPushParams.getAlertTime());
                    }
                }
                jsonMsgParams.put("showDialog", 1);
                jsonMsgParams.put("title", alertPushParams.getDeviceName());
                jsonMsgParams.put("content", StringUtils.strReplaceVariable("${", "}", content, map));
                return jsonMsgParams.toString();
            default:
                break;
        }
        Object sendAccountObject = jsonMsgParams.get("sendAccount");
        Set<String> sendAccountSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(sendAccountObject)) {
            Collections.addAll(sendAccountSet, sendAccountObject.toString());
        }
        if (NotifyChannelEnum.SMS.equals(notifyChannelEnum) || NotifyChannelEnum.VOICE.equals(notifyChannelEnum)) {
            if (CollectionUtils.isNotEmpty(alertPushParams.getUserPhoneSet())) {
                sendAccountSet.addAll(alertPushParams.getUserPhoneSet());
            }
        }
        if (NotifyChannelProviderEnum.WECHAT_MINI_PROGRAM.equals(notifyChannelProviderEnum)
                || NotifyChannelProviderEnum.WECHAT_SERVICE_ACCOUNT.equals(notifyChannelProviderEnum)) {
            if (CollectionUtils.isNotEmpty(alertPushParams.getUserIdSet())) {
                for (Long userId : alertPushParams.getUserIdSet()) {
                    sendAccountSet.add(userId.toString());
                }
            }
        }
        notifyVO.setSendAccount(StringUtils.join(sendAccountSet, ","));
        notifyVO.setMap(map);
        NotifyChannel finalChannel = notifyChannel;
        NotifyTemplate finalTemplate = notifyTemplate;
        NotifyChannelProviderEnum finalProvider = notifyChannelProviderEnum;
        LinkedHashMap<String, String> finalMap = map;
        CompletableFuture.runAsync(() -> doAlertSend(finalChannel, finalTemplate, finalProvider, finalMap, sendAccountSet), notifyExecutor)
                .exceptionally(ex -> {
                    log.error("告警通知异步发送失败，templateId={}", alertPushParams.getNotifyTemplateId(), ex);
                    return null;
                });
        return "";
    }

    private void doAlertSend(NotifyChannel notifyChannel, NotifyTemplate notifyTemplate,
                              NotifyChannelProviderEnum notifyChannelProviderEnum,
                              LinkedHashMap<String, String> map, Set<String> sendAccountSet) {
        try {
            NotifyVO notifyVO = new NotifyVO();
            notifyVO.setNotifyChannel(notifyChannel)
                    .setNotifyTemplate(notifyTemplate)
                    .setNotifyChannelProviderEnum(notifyChannelProviderEnum)
                    .setSendAccount(StringUtils.join(sendAccountSet, ","))
                    .setMap(map);
            this.notifySend(notifyVO);
        } catch (Exception e) {
            log.error("告警通知发送异常，templateId={}", notifyTemplate.getId(), e);
        }
    }

    @Override
    public String workOrderSend(WorkOrderNotifyParams workOrderNotifyParams) {
        NotifyVO notifyVO = this.selectOnlyEnable(NotifyServiceCodeEnum.WORK_ORDER.getServiceCode(), NotifyChannelEnum.EMAIL.getType(), null, workOrderNotifyParams.getTenantId());
        NotifyChannelProviderEnum notifyChannelProviderEnum = notifyVO.getNotifyChannelProviderEnum();
        // 获取模板参数
        JSONObject jsonMsgParams = JSONObject.parseObject(notifyVO.getNotifyTemplate().getMsgParams());
        String content = jsonMsgParams.get("content").toString();
        // 从模板内容中获取 占位符 关键字
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        List<String> variables = notifyTemplateService.listVariables(content, notifyChannelProviderEnum);
        // 按顺序依次替换变量信息
        for (int i = 0; i < variables.size(); i++) {
            switch (i) {
                case 0:
                    map.put(variables.get(i), workOrderNotifyParams.getCreateBy());
                    break;
                case 1:
                    map.put(variables.get(i), workOrderNotifyParams.getName());
                    break;
                case 2:
                    map.put(variables.get(i), workOrderNotifyParams.getNumber());
                    break;
                case 3:
                    map.put(variables.get(i), workOrderNotifyParams.getWorkOrderType());
                    break;
                case 4:
                    map.put(variables.get(i), workOrderNotifyParams.getDeviceName());
                    break;
                case 5:
                    map.put(variables.get(i), workOrderNotifyParams.getSerialNumber());
                    break;
                default:
                    break;
            }
        }
        // 获取发送账号
        Object sendAccountObject = jsonMsgParams.get("sendAccount");
        Set<String> sendAccountSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(sendAccountObject)) {
            Collections.addAll(sendAccountSet, sendAccountObject.toString());
        }
        sendAccountSet.add(workOrderNotifyParams.getSendEmail());
        notifyVO.setSendAccount(StringUtils.join(sendAccountSet, ","));
        notifyVO.setMap(map);
        this.notifySend(notifyVO);
        return "";
    }

    @Override
    public AjaxResult sendCaptchaSms(String phone, String captcha) {
        NotifyVO notifyVO = this.selectOnlyEnable(NotifyServiceCodeEnum.CAPTCHA.getServiceCode(), NotifyChannelEnum.SMS.getType(), null, 1L);
        NotifyChannelProviderEnum notifyChannelProviderEnum = notifyVO.getNotifyChannelProviderEnum();
        // 获取模板参数
        JSONObject jsonMsgParams = JSONObject.parseObject(notifyVO.getNotifyTemplate().getMsgParams());
        String content = jsonMsgParams.get("content").toString();
        // 从模板内容中获取 占位符 关键字
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        List<String> variables = new ArrayList<>();
        if (NotifyChannelProviderEnum.SMS_ALIBABA.equals(notifyChannelProviderEnum)) {
            variables = StringUtils.getVariables("${}", content);
        } else if (NotifyChannelProviderEnum.SMS_TENCENT.equals(notifyChannelProviderEnum)) {
            variables = StringUtils.getVariables("{}", content);
        }
        map.put(variables.get(0), captcha);
        notifyVO.setSendAccount(phone);
        notifyVO.setMap(map);
        return this.notifySend(notifyVO);
    }

    @Override
    public NotifyVO selectOnlyEnable(String serviceCode, String channelType, String provider, Long tenantId) {
        // 获取查询条件
        NotifyTemplate enableQueryCondition = notifyTemplateService.getEnableQueryCondition(serviceCode, channelType, provider, tenantId);
        NotifyTemplate notifyTemplate = notifyTemplateService.selectOnlyEnable(enableQueryCondition);
        if (Objects.isNull(notifyTemplate)) {
            throw new ServiceException(MessageUtils.message("not.find.enable.notify.template"));
        }
        NotifyChannel notifyChannel = notifyChannelMapper.selectById(notifyTemplate.getChannelId());
        if (Objects.isNull(notifyChannel)) {
            throw new ServiceException(MessageUtils.message("not.find.notify.channel"));
        }
        NotifyVO notifyVO = new NotifyVO();
        notifyVO.setNotifyChannel(notifyChannel);
        notifyVO.setNotifyTemplate(notifyTemplate);
        NotifyChannelProviderEnum notifyChannelProviderEnum = NotifyChannelProviderEnum.getByChannelTypeAndProvider(notifyVO.getNotifyChannel().getChannelType(), notifyVO.getNotifyChannel().getProvider());
        notifyVO.setNotifyChannelProviderEnum(notifyChannelProviderEnum);
        return notifyVO;
    }

    @Override
    public AjaxResult sendSmsCaptcha(String phoneNumber, String redisKey) {
        String userIdKey = redisKey + phoneNumber;
        String captcha = VerifyCodeUtils.generateVerifyCode(6, "0123456789");
        AjaxResult ajaxResult = this.sendCaptchaSms(phoneNumber, captcha);
        if (HttpStatus.ERROR == Integer.parseInt(ajaxResult.get("code").toString())) {
            return AjaxResult.error(MessageUtils.message("sms.send.fail.contact.admin"));
        }
        redisCache.setCacheObject(userIdKey, captcha, 2, TimeUnit.MINUTES);
        return AjaxResult.success();
    }

    @Override
    public String weComVerifyUrl(String msgSignature, String timestamp, String nonce, String echostr) {
        // 因为只用验证一次，下面三个参数就不写在配置文件里了，需要验证的可以把下面的验证参数改为自己公司的，然后部署到服务器验证就行
        //token
        String token = "pr77kdcA5mzJwNeAwV86UcIS";
        // encodingAESKey
        String encodingAesKey = "efNILsQxM6wOCsrNPiBeuLOBDgDSnNtOVFBbtf6jwTe";
        //企业ID
        String corpId = "ww4761023a5d81550f";
        // 通过检验msg_signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        String result = null;
        try {
            WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, corpId);
            result = wxcpt.VerifyURL(msgSignature, timestamp, nonce, echostr);
        } catch (AesException e) {
            log.error("企业微信验证url错误,error:{}", e.getMessage());
        }
        return result;
    }

    @Override
    public String reportSend(ReportNotifyParams reportNotifyParams) {
        NotifyVO notifyVO = this.selectOnlyEnable(NotifyServiceCodeEnum.REPORT.getServiceCode(), NotifyChannelEnum.EMAIL.getType(), null, reportNotifyParams.getTenantId());
        NotifyChannelProviderEnum notifyChannelProviderEnum = notifyVO.getNotifyChannelProviderEnum();
        // 获取模板参数
        JSONObject jsonMsgParams = JSONObject.parseObject(notifyVO.getNotifyTemplate().getMsgParams());
        String content = jsonMsgParams.get("content").toString();
        // 从模板内容中获取 占位符 关键字
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        List<String> variables = notifyTemplateService.listVariables(content, notifyChannelProviderEnum);
        // 按顺序依次替换变量信息
        for (int i = 0; i < variables.size(); i++) {
            switch (i) {
                case 0:
                    map.put(variables.get(i), reportNotifyParams.getName());
                    break;
                case 1:
                    map.put(variables.get(i), reportNotifyParams.getStatusDesc());
                    break;
                case 2:
                    map.put(variables.get(i), reportNotifyParams.getUploadPath());
                    break;
                default:
                    break;
            }
        }
        // 获取发送账号
        Object sendAccountObject = jsonMsgParams.get("sendAccount");
        Set<String> sendAccountSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(sendAccountObject)) {
            Collections.addAll(sendAccountSet, sendAccountObject.toString());
        }
        sendAccountSet.addAll(reportNotifyParams.getSendEmail());
        notifyVO.setSendAccount(StringUtils.join(sendAccountSet, ","));
        notifyVO.setMap(map);
        this.notifySend(notifyVO);
        return "";
    }

    @Override
    public void cardSend(CardVO cardVO) {
        NotifyVO notifyVO = this.selectOnlyEnable(NotifyServiceCodeEnum.CARD.getServiceCode(), null, null, cardVO.getTenantId());
        NotifyChannelProviderEnum notifyChannelProviderEnum = notifyVO.getNotifyChannelProviderEnum();
        // 获取模板参数
        JSONObject jsonMsgParams = JSONObject.parseObject(notifyVO.getNotifyTemplate().getMsgParams());
        String content = jsonMsgParams.get("content").toString();
        // 从模板内容中获取 占位符 关键字
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        List<String> variables = notifyTemplateService.listVariables(content, notifyChannelProviderEnum);
        // 按顺序依次替换变量信息
        for (int i = 0; i < variables.size(); i++) {
            switch (i) {
                case 0:
                    map.put(variables.get(i), StringUtils.isEmpty(cardVO.getMsisdn()) ? "暂无" : cardVO.getMsisdn());
                    break;
                case 1:
                    map.put(variables.get(i), cardVO.getIccid());
                    break;
                case 2:
                    map.put(variables.get(i), Objects.isNull(cardVO.getTotalData()) ? "暂无" : cardVO.getTotalData().toString() + "MB");
                    break;
                case 3:
                    map.put(variables.get(i), cardVO.getDataUsed() + "MB");
                    break;
                case 4:
                    map.put(variables.get(i), Objects.isNull(cardVO.getDataRemaining()) ? "暂无" : cardVO.getDataRemaining().toString() + "MB");
                    break;
                default:
                    break;
            }
        }
        // 获取发送账号

        Object sendAccountObject = jsonMsgParams.get("sendAccount");
        Set<String> sendAccountSet = new HashSet<>();
        if (ObjectUtil.isNotEmpty(sendAccountObject)) {
            Collections.addAll(sendAccountSet, sendAccountObject.toString());
        }
        // 发送邮件通知
        if (StringUtils.isNotEmpty(cardVO.getNotifyUsers())) {
            List<String> userIds = StringUtils.str2List(cardVO.getNotifyUsers(), ",", true, true);
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysUser::getUserId, userIds);
            List<SysUser> sysUserList = sysUserMapper.selectList(queryWrapper);
            Set<String> emailSet = sysUserList.stream().map(SysUser::getEmail).collect(Collectors.toSet());
            sendAccountSet.addAll(emailSet);
        }
        if (CollectionUtils.isEmpty(sendAccountSet)) {
            return;
        }
        notifyVO.setSendAccount(StringUtils.join(sendAccountSet, ","));
        notifyVO.setMap(map);
        this.notifySend(notifyVO);
    }

    /**
     * 校验发送账号格式
     * @param sendAccount 发送账号
     * @param: notifyChannelEnum 通知枚举
     * @return java.lang.String
     */
    private String checkSendAccountMsg(String sendAccount, NotifyChannelProviderEnum notifyChannelProviderEnum) {
        boolean matches;
        switch (Objects.requireNonNull(notifyChannelProviderEnum)) {
            case SMS_ALIBABA:
            case SMS_TENCENT:
            case VOICE_ALIBABA:
            case DING_TALK_WORK:
            case WECHAT_WECOM_APPLY:
                matches  = ValidationUtils.isMobile(sendAccount);
                if (!matches) {
                    return "请输入正确的电话号码！";
                }
                break;
            case EMAIL_QQ:
            case EMAIL_163:
                matches = ValidationUtils.isEmail(sendAccount);
                if (!matches) {
                    return "请输入正确的邮箱地址！";
                }
                break;
            case WECHAT_MINI_PROGRAM:
                if (!StringUtils.isNumeric(sendAccount)) {
                    return "请输入正确的用户id";
                }
                break;
            default:
                return "";
        }
        return "";
    }

}
