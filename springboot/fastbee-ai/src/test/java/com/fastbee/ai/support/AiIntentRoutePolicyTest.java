package com.fastbee.ai.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AiIntentRoutePolicyTest {

    @Test
    void shouldRecognizeNonExecutionPlatformConsultationQuestions() {
        Assertions.assertTrue(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("如果设备接在外部 emqx 服务器上，怎么在 fastbee 设备管理查看数据")));
        Assertions.assertTrue(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("产品必须搭配数据库才能存储历史数据吗")));
        Assertions.assertTrue(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("在固件升级时设备离线，平台下发升级指令，设备再上线能收到吗")));
        Assertions.assertTrue(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("String payload = msgContext.getPayload(); 这段代码的意思是不是读取上报内容")));
        Assertions.assertTrue(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("如何打开设备智能开关")));
        Assertions.assertTrue(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("详细介绍物模型数据")));
        Assertions.assertTrue(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("关联成功后，上报的原始数据会结合物模型的属性定义，被转换为标准格式存入时序数据库或物联网数据表中，列出一条标准格式的数据")));
        Assertions.assertTrue(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("如果物模型里开启的实时监测还能进行历史存储吗")));
        Assertions.assertTrue(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("// 1. 获取主题和内容 String name = msgContext.getTopic(); String payload = msgContext.getPayload(); JSONObject jsonObject = JSONUtil.parseObj(payload); 的意思是不是我设备发送的数据转变为一种格式后再发送出去")));
    }

    @Test
    void shouldNotTreatStrongExecutionOrQueryQuestionsAsPlatformConsultation() {
        Assertions.assertFalse(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("统计产品数量")));
        Assertions.assertFalse(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("查询设备 DEVICE88888888 最近1小时温度趋势")));
        Assertions.assertFalse(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("打开设备智能开关")));
        Assertions.assertFalse(AiIntentRoutePolicy.isNonExecutionPlatformConsultation(
                AiIntentRoutePolicy.normalizeQuestion("这个文件中的数据有哪些问题")));
    }

    @Test
    void shouldRequireStrongAdmissionForDataQueryExecution() {
        Assertions.assertTrue(AiIntentRoutePolicy.isDataQueryExecutionQuestion(
                AiIntentRoutePolicy.normalizeQuestion("统计产品数量")));
        Assertions.assertTrue(AiIntentRoutePolicy.isDataQueryExecutionQuestion(
                AiIntentRoutePolicy.normalizeQuestion("查询DEVICE88888888最近1小时灯光色值趋势")));

        AiRouteDecision decision = AiIntentRoutePolicy.evaluateDataQueryExecution(
                AiIntentRoutePolicy.normalizeQuestion("历史数据需要自建数据库吗，历史数据至少要多少条才能生成数据曲线"));

        Assertions.assertFalse(decision.isAccepted(AiIntentRoutePolicy.DATA_QUERY_ACCEPT_SCORE));
        Assertions.assertFalse(decision.getBlockers().isEmpty());

        AiRouteDecision formatExampleDecision = AiIntentRoutePolicy.evaluateDataQueryExecution(
                AiIntentRoutePolicy.normalizeQuestion("关联成功后，上报的原始数据会结合物模型的属性定义，被转换为标准格式存入时序数据库或物联网数据表中，列出一条标准格式的数据"));

        Assertions.assertFalse(formatExampleDecision.isAccepted(AiIntentRoutePolicy.DATA_QUERY_ACCEPT_SCORE));
        Assertions.assertFalse(formatExampleDecision.getBlockers().isEmpty());

        AiRouteDecision monitorHistoryDecision = AiIntentRoutePolicy.evaluateDataQueryExecution(
                AiIntentRoutePolicy.normalizeQuestion("如果物模型里开启的实时监测还能进行历史存储吗"));

        Assertions.assertFalse(monitorHistoryDecision.isAccepted(AiIntentRoutePolicy.DATA_QUERY_ACCEPT_SCORE));
        Assertions.assertFalse(monitorHistoryDecision.getBlockers().isEmpty());

        AiRouteDecision codeSnippetDecision = AiIntentRoutePolicy.evaluateDataQueryExecution(
                AiIntentRoutePolicy.normalizeQuestion("// 1. 获取主题和内容 String name = msgContext.getTopic(); String payload = msgContext.getPayload(); JSONObject jsonObject = JSONUtil.parseObj(payload); 的意思是不是我设备发送的数据转变为一种格式后再发送出去"));

        Assertions.assertFalse(codeSnippetDecision.isAccepted(AiIntentRoutePolicy.DATA_QUERY_ACCEPT_SCORE));
        Assertions.assertFalse(codeSnippetDecision.getBlockers().isEmpty());

        AiRouteDecision fileAnalysisDecision = AiIntentRoutePolicy.evaluateDataQueryExecution(
                AiIntentRoutePolicy.normalizeQuestion("这个文件中的数据有哪些问题"));

        Assertions.assertTrue(AiIntentRoutePolicy.isFileContentAnalysisQuestion(
                AiIntentRoutePolicy.normalizeQuestion("这个文件中的数据有哪些问题")));
        Assertions.assertFalse(fileAnalysisDecision.isAccepted(AiIntentRoutePolicy.DATA_QUERY_ACCEPT_SCORE));
        Assertions.assertTrue(fileAnalysisDecision.getBlockers().contains("文件或附件内容分析"));
    }

    @Test
    void shouldRecognizeDeviceRuntimeReadQuestion() {
        Assertions.assertTrue(AiIntentRoutePolicy.isDeviceRuntimeReadQuestion(
                AiIntentRoutePolicy.normalizeQuestion("查询设备 DEVICE88888888 最近1小时温度趋势")));
        Assertions.assertTrue(AiIntentRoutePolicy.isDeviceRuntimeReadQuestion(
                AiIntentRoutePolicy.normalizeQuestion("查询SN001当前灯光色值1是多少")));
        Assertions.assertTrue(AiIntentRoutePolicy.isDeviceRuntimeReadQuestion(
                AiIntentRoutePolicy.normalizeQuestion("query device SN001 current waterlevel value")));
        Assertions.assertTrue(AiIntentRoutePolicy.isDeviceRuntimeReadQuestion(
                AiIntentRoutePolicy.normalizeQuestion("query device SN001 latest pressure value")));
        Assertions.assertTrue(AiIntentRoutePolicy.isDeviceRuntimeReadQuestion(
                AiIntentRoutePolicy.normalizeQuestion("query device SN001 current concentration value")));
        Assertions.assertFalse(AiIntentRoutePolicy.isDeviceRuntimeReadQuestion(
                AiIntentRoutePolicy.normalizeQuestion("如果物模型里开启的实时监测还能进行历史存储吗")));
        Assertions.assertFalse(AiIntentRoutePolicy.isDeviceRuntimeReadQuestion(
                AiIntentRoutePolicy.normalizeQuestion("设备控制下发接口在哪里")));
        Assertions.assertFalse(AiIntentRoutePolicy.isDeviceRuntimeReadQuestion(
                AiIntentRoutePolicy.normalizeQuestion("打开设备智能开关")));
    }

    @Test
    void shouldRequireStrongAdmissionForDeviceControlExecution() {
        Assertions.assertTrue(AiIntentRoutePolicy.isDeviceControlExecutionQuestion(
                AiIntentRoutePolicy.normalizeQuestion("把★智能开关产品68的灯光色值1调为66")));
        Assertions.assertFalse(AiIntentRoutePolicy.isDeviceControlExecutionQuestion(
                AiIntentRoutePolicy.normalizeQuestion("在固件升级时，如果设备离线，平台下发了升级指令，设备再上线的时候能够收到指令之后升级吗")));
        Assertions.assertFalse(AiIntentRoutePolicy.isDeviceControlExecutionQuestion(
                AiIntentRoutePolicy.normalizeQuestion("Caused by: java.lang.reflect.InvocationTargetException at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)")));
    }

    @Test
    void shouldRecognizeAssistantModelIdentityQuestionAsGeneralIntent() {
        Assertions.assertTrue(AiIntentRoutePolicy.isAssistantModelIdentityQuestion(
                AiIntentRoutePolicy.normalizeQuestion("你使用的是什么大模型")));
        Assertions.assertTrue(AiIntentRoutePolicy.isAssistantModelIdentityQuestion(
                AiIntentRoutePolicy.normalizeQuestion("你是谁")));
        Assertions.assertFalse(AiIntentRoutePolicy.isAssistantModelIdentityQuestion(
                AiIntentRoutePolicy.normalizeQuestion("详细介绍物模型数据")));
        Assertions.assertFalse(AiIntentRoutePolicy.isAssistantModelIdentityQuestion(
                AiIntentRoutePolicy.normalizeQuestion("模型管理在哪里配置")));
    }
}
