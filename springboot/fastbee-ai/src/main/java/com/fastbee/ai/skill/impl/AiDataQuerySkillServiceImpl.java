package com.fastbee.ai.skill.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.skill.IAiDataQuerySkillService;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.vo.AlertCountVO;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;
import com.fastbee.iot.service.DataCenterService;

/**
 * AI 问数技能服务实现。
 */
@Service
public class AiDataQuerySkillServiceImpl implements IAiDataQuerySkillService {

    @Resource
    private DataCenterService dataCenterService;

    @Override
    public List<AlertCountVO> countAlertProcess(DataCenterParam dataCenterParam) {
        return dataCenterService.countAlertProcess(normalizeParam(dataCenterParam));
    }

    @Override
    public List<AlertCountVO> countAlertLevel(DataCenterParam dataCenterParam) {
        return dataCenterService.countAlertLevel(normalizeParam(dataCenterParam));
    }

    @Override
    public List<ThingsModelLogCountVO> countThingsModelInvoke(DataCenterParam dataCenterParam) {
        DataCenterParam query = normalizeParam(dataCenterParam);
        if (StringUtils.isBlank(query.getSerialNumber())) {
            throw new ServiceException(message("ai.device.serial.number.required"));
        }
        return dataCenterService.countThingsModelInvoke(query);
    }

    private DataCenterParam normalizeParam(DataCenterParam dataCenterParam) {
        DataCenterParam query = dataCenterParam == null ? new DataCenterParam() : dataCenterParam;
        if (query.getTenantId() == null) {
            query.setTenantId(resolveDataCenterTenantId());
        }
        return query;
    }

    private Long resolveDataCenterTenantId() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null || loginUser.getUser().getDept() == null) {
            throw new ServiceException(message("ai.data.query.current.tenant.required"));
        }
        return loginUser.getUser().getDept().getDeptUserId();
    }
}
