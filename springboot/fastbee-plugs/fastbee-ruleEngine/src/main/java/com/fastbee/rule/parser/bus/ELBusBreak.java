package com.fastbee.rule.parser.bus;//package com.ming.core.parser.bus;
//
//import cn.hutool.core.util.StrUtil;
//import com.ming.core.parser.entity.node.Node;
//import com.yomahub.liteflow.builder.el.ELBus;
//import com.yomahub.liteflow.builder.el.ELWrapper;
//import com.yomahub.liteflow.builder.el.ThenELWrapper;
//
//public class ELBusBreak {
//
//    private ELWrapper wrapper;
//
//    private boolean isNodeEL = false;
//
//    public static ELBusBreak NEW(){
//        return new ELBusBreak();
//    }
//
//    public ELBusBreak nodeEL(){
//        this.isNodeEL = true;
//        return this;
//    }
//
//    public ELBusBreak thenEL(){
//        this.isNodeEL = false;
//        return this;
//    }
//
//
//    public ELBusBreak node(Node node){
////        if(isNodeEL){
////            ELWrapper elWrapper = ELBusNode.node(info);
////            wrapper = elWrapper;
////        }else{
////            ThenELWrapper thenELWrapper = ELBus.then(ELBusNode.node(info));
////            if(StrUtil.isNotEmpty(info.getCmpPre())){
////                thenELWrapper.pre(info.getCmpPre());
////            }
////            if(StrUtil.isNotEmpty(info.getCmpFinallyOpt())){
////                thenELWrapper.finallyOpt(info.getCmpFinallyOpt());
////            }
////            if(StrUtil.isNotEmpty(info.getCmpId())){
////                thenELWrapper.id(info.getCmpId());
////            }
////            if(StrUtil.isNotEmpty(info.getCmpTag())){
////                thenELWrapper.tag(info.getCmpTag());
////            }
////            if(info.getCmpMaxWaitSeconds() != null){
////                thenELWrapper.maxWaitSeconds(info.getCmpMaxWaitSeconds());
////            }
////            wrapper = thenELWrapper;
////        }
//        return this;
//    }
//
//    public String toEL(){
//        return wrapper.toEL();
//    }
//
//    public String toEL(boolean format){
//        return wrapper.toEL(format);
//    }
//
//    public ELWrapper toELWrapper(){
//        return wrapper;
//    }
//
//}
