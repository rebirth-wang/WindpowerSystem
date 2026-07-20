package com.fastbee.rule.parser.convert;

import static com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder.EXPRESS_RUNNER;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.DefaultContext;
import com.yomahub.liteflow.common.ChainConstant;
import com.yomahub.liteflow.flow.FlowBus;
import com.yomahub.liteflow.flow.element.Condition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.rule.parser.entity.ELInfo;

@Slf4j
@Component
public class ExpressGenerator {

    public Condition getCondition(ELInfo elInfo) {
        Condition condition = null;
        try {
            DefaultContext<String, Object> context = new DefaultContext<>();

            // 这里一定要先放chain，再放node，因为node优先于chain，所以当重名时，node会覆盖掉chain
            // 往上下文里放入所有的chain，是的el表达式可以直接引用到chain
            FlowBus.getChainMap().values().forEach(chain -> context.put(chain.getChainId(), chain));

            // 往上下文里放入所有的node，使得el表达式可以直接引用到nodeId
            FlowBus.getNodeMap().keySet().forEach(nodeId -> context.put(nodeId, FlowBus.getNode(nodeId)));

            // 放入当前主chain的ID
            assert elInfo != null;
            context.put(ChainConstant.CURR_CHAIN_ID, elInfo.getChainId());

            List<String> errorList = new ArrayList<>();

            // promotionChain: THEN(fullCutCmp, fullDiscountCmp, rushBuyCmp);
            String elStr = elInfo.getElStr();
            condition = (Condition) EXPRESS_RUNNER.execute(elStr, context, errorList, true, true);
        } catch (Exception ex) {
            log.error("-----", ex);
        }
        return condition;
    }


//    public CmpProperty generateJsonEL(ELInfo elInfo) {
//        return builderJsonEL(getCondition(elInfo));
//    }

    /*private CmpProperty builderJsonEL(Condition condition) {
        ExpressParser parser = ParserSelector.getParser(condition);
        // id, type, property
        CmpProperty cmpProperty = parser.builderVO(condition);
        // conditionList
        cmpProperty.setCmpProperty(parser.builderCondition(condition));
        // childList
        cmpProperty.setChildren(parser.builderChildren(condition));
        return cmpProperty;
    }*/

}
