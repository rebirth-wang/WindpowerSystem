package com.fastbee.controller.ruleview;

import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.rule.parser.convert.FlowConvert;
import com.fastbee.rule.parser.entity.FlowData;
import com.fastbee.rule.parser.graph.Graph;
import com.fastbee.rule.parser.graph.GraphInfo;
import com.fastbee.rule.parser2.GraphEL;
import com.fastbee.rule.util.RuleConvertUtil;

@Slf4j
@RestController
@RequestMapping("/ruleview/parser")
@Api(tags = "规则解析")
public class RuleParserController extends BaseController {

    @PostMapping("/jsonToEL")
    @PreAuthorize("@ss.hasPermi('rule:parser:edit')")
    @ApiOperation("jsonToEL")
    public AjaxResult jsonToEL(@RequestBody FlowData data) throws Exception {
        Graph graph = new Graph(data);
        GraphInfo graphInfo = graph.toELInfo();
        return success(graphInfo.toString());
    }

    @PostMapping("/jsonToEL2")
    @PreAuthorize("@ss.hasPermi('rule:parser:edit')")
    @ApiOperation("jsonToEL2")
    public AjaxResult jsonToEL2(@RequestBody FlowData data) throws Exception {
        GraphEL graphEL = new GraphEL(data);
        String ret = graphEL.toEL(true);
        log.info("GraphEL: {}", ret);
        return success(ret);
    }

    @PostMapping("/elToJson")
    @PreAuthorize("@ss.hasPermi('rule:parser:edit')")
    @ApiOperation("elToJson")
    public AjaxResult elToJson(@RequestBody Map<String,Object> params) throws Exception {
        FlowConvert convert = RuleConvertUtil.convert();
        String ret = convert.el2Json((String) params.get("el"));
        log.info("elToJson: {}", ret);
        return success(ret);
    }

    @PostMapping("/getRetryExceptions")
    @PreAuthorize("@ss.hasPermi('rule:parser:edit')")
    @ApiOperation("getRetryExceptions")
    public AjaxResult getRetryExceptions(@RequestBody Map<String,Object> params) throws Exception {
        return success();
    }

}
