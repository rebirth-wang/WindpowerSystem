package com.fastbee.rule.core;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import jakarta.annotation.Resource;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fastbee.rule.context.MsgContext;

@Component
@Slf4j
public class FlowLogExecutor {
    private static final Logger script_logger = LoggerFactory.getLogger("script");
    private static final Logger scene_logger = LoggerFactory.getLogger("scene");
    @Resource
    private FlowExecutor flowExecutor;


    public LiteflowResponse execute2Resp(String chainId, Object param, Object... contextBeanArray) {
        printContextBean(contextBeanArray);
        LiteflowResponse response = flowExecutor.execute2Resp(chainId, param, contextBeanArray);
        printResponse(response);
        return response;
    }

    public LiteflowResponse execute2RespWithRid(String chainId, String requestId, Object... contextBeanArray) {
        LiteflowResponse response = null;
        try {
            printContextBeanWithRid(requestId, contextBeanArray);
            response = flowExecutor.execute2RespWithRid(chainId, null, requestId, contextBeanArray);
            printResponseWithRid(requestId, response);
        } catch (Exception e) {
            // 打印异常信息，实际项目中建议使用日志框架
           log.error("规则引擎执行过程中发生异常: " + e.getMessage());
            e.printStackTrace();

            // 构造一个失败的 LiteflowResponse 返回
            response = new LiteflowResponse();
            response.setSuccess(false);
            response.setMessage("规则引擎执行过程中发生异常: " + e.getMessage());
        }
        return response;
    }

    public LiteflowResponse execute2RespWithEL(String elStr, String requestId, Object... contextBeanArray) {
        LiteflowResponse response = null;
        try {
            printContextBeanWithRid(requestId, contextBeanArray);
            response = flowExecutor.execute2RespWithEL(elStr, null, requestId, contextBeanArray);
            printResponseWithRid(requestId, response);
        } catch (Exception e) {
            // 打印异常信息，实际项目中建议使用日志框架
            log.error("规则引擎执行过程中发生异常: " + e.getMessage());
            e.printStackTrace();

            // 构造一个失败的 LiteflowResponse 返回
            response = new LiteflowResponse();
            response.setSuccess(false);
            response.setMessage("规则引擎执行过程中发生异常: " + e.getMessage());
        }
        return response;
    }

    public LiteflowResponse execute2Future(String chainId, Object... contextBeanArray) throws ExecutionException, InterruptedException {
        printContextBean(contextBeanArray);
        Future<LiteflowResponse> future = flowExecutor.execute2Future(chainId, null, contextBeanArray);
        LiteflowResponse response = future.get();
        printResponse(response);
        return response;
    }

    public LiteflowResponse execute2FutureWithRid(String chainId, String requestId, Object... contextBeanArray) {
        LiteflowResponse response = null;
        try {
            printContextBeanWithRid(requestId, contextBeanArray);
            Future<LiteflowResponse> future = flowExecutor.execute2FutureWithRid(chainId, null, requestId, contextBeanArray);
            response = future.get();
            printResponseWithRid(requestId, response);
        } catch (Exception e) {
            // 若捕获的是中断异常，建议恢复中断状态
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            log.error("规则引擎执行流程时发生异常，requestId: " + requestId);
            e.printStackTrace();

            response = new LiteflowResponse();
            response.setSuccess(false);
            response.setMessage("规则引擎流程执行失败: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }
        return response;
    }

    public void printContextBean(Object... contextBeanArray) {
        log.info("=====+>规则引擎执行前，上下文变量值");
        for (Object contextBean : contextBeanArray) {
            log.info("上下文数值：{}", contextBean);
        }
        log.info("=====+>规则引擎正在执行......");
    }

    public void printContextBeanWithRid(String requestId, Object... contextBeanArray) {
        Logger ruleLogger;
        String[] parts = requestId.split("/");
        if (parts.length >= 2) {
            if (Objects.equals(parts[0], "script")) {
                ruleLogger = script_logger;
            } else if (Objects.equals(parts[0], "scene")) {
                ruleLogger = scene_logger;
            } else {
                ruleLogger = log;
            }
            ruleLogger.info("[{}]=====+>规则引擎执行前，上下文变量值", requestId);
            for (Object contextBean : contextBeanArray) {
                // MsgContext
                if(contextBean instanceof MsgContext) {
                    ruleLogger.info("[{}]=====+> serialNumber：{}", requestId, ((MsgContext) contextBean).getSerialNumber());
                    ruleLogger.info("[{}]=====+> protocolCode：{}", requestId, ((MsgContext) contextBean).getProtocolCode());
                    ruleLogger.info("[{}]=====+> topic：{}", requestId, ((MsgContext) contextBean).getTopic());
                    // payload出去换行符
                    ruleLogger.info("[{}]=====+> payload：{}", requestId, ((MsgContext) contextBean).getPayload().replaceAll("[\\r\\n]", ""));
                } else {
                    ruleLogger.info("[{}]=====+>上下文数值：{}", requestId, contextBean);
                }
            }
            ruleLogger.info("[{}]=====+>规则引擎正在执行......", requestId);
        }
    }

    public void printResponse(LiteflowResponse response) {
        if (!response.isSuccess()) {
            Exception e = response.getCause();
            log.error("=====+>报错信息：{}", e.getMessage(), e);
        } else {
            //步骤详情
//            Map<String, List<CmpStep>> stepMap = response.getExecuteSteps();
//            stepMap.forEach((k, v) -> {
//                v.forEach((step) -> {
//                    log.info("步骤：{}({})，执行时间：{}", step.getNodeId(), step.getNodeName(), step.getTimeSpent());
//                });
//            });
            //每各步骤执行时间
            String stepStr = response.getExecuteStepStrWithTime();
            log.info("=====+>步骤：{}", stepStr);
        }
    }

    public void printResponseWithRid(String requestId, LiteflowResponse response) {
        Logger ruleLogger;
        String[] parts = requestId.split("/");
        if (parts.length >= 2) {
            if (Objects.equals(parts[0], "script")) {
                ruleLogger = script_logger;
                MsgContext msgContext = response.getContextBean(MsgContext.class);
                if (msgContext != null) {
                    ruleLogger.info("[{}]=====+>执行后，serialNumber：{}", requestId, msgContext.getSerialNumber());
                    ruleLogger.info("[{}]=====+>执行后，protocolCode：{}", requestId, msgContext.getProtocolCode());
                    ruleLogger.info("[{}]=====+>执行后，topic：{}", requestId, msgContext.getTopic());
                    // payload出去换行符
                    ruleLogger.info("[{}]=====+>执行后，payload：{}", requestId, msgContext.getPayload().replaceAll("[\\r\\n]", ""));
                }
            } else if (Objects.equals(parts[0], "scene")) {
                ruleLogger = scene_logger;
            } else {
                ruleLogger = log;
            }
            if (!response.isSuccess()) {
                Exception e = response.getCause();
                ruleLogger.error("[{}]=====+>报错信息：{}", requestId, e.toString());
            } else {
                //每各步骤执行时间
                String stepStr = response.getExecuteStepStrWithTime();
                ruleLogger.info("[{}]=====+>步骤：{}", requestId, stepStr);
                ruleLogger.info("[{}]=====+>执行完成！", requestId);
            }
        }
    }

}
