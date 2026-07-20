package com.fastbee.controller.goview;

import java.util.Objects;

import jakarta.annotation.Resource;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Anonymous;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.model.param.GoviewProjectDataSqlParam;
import com.fastbee.iot.service.IGoviewProjectDataService;

/**
 * goview 获取数据接口
 * 可自己根据需求在此添加获取数据接口
 * @author fastb
 * @date 2023-11-06 15:07
 */
@Anonymous
@Api(tags = "大屏管理获取数据")
@RestController
@RequestMapping("/goview/projectData")
public class GoviewProjectDataController {

    @Resource
    private IGoviewProjectDataService goviewProjectDataService;

    /**
     * 根据sql获取组件数据接口
     * @param param sql
     * @return 组件数据
     */
    @PostMapping("/executeSql")
    public AjaxResult executeSql(@RequestBody GoviewProjectDataSqlParam param) {
        if (Objects.isNull(param) || StringUtils.isEmpty(param.getSql())) {
            return AjaxResult.error(MessageUtils.message("goview.project.data.execute.sql.failed"));
        }
        return goviewProjectDataService.executeSql(param.getSql());
    }
}
