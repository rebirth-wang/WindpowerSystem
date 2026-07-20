package com.fastbee.iot.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ThingsModelTemplate;
import com.fastbee.iot.model.vo.ThingsModelTemplateVO;

/**
 * 物模型模板Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */
@Mapper
public interface ThingsModelTemplateConvert
{

    ThingsModelTemplateConvert INSTANCE = Mappers.getMapper(ThingsModelTemplateConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param thingsModelTemplate
     * @return 物模型模板集合
     */
    ThingsModelTemplateVO convertThingsModelTemplateVO(ThingsModelTemplate thingsModelTemplate);

    /**
     * VO类转换为实体类集合
     *
     * @param thingsModelTemplateVO
     * @return 物模型模板集合
     */
    ThingsModelTemplate convertThingsModelTemplate(ThingsModelTemplateVO thingsModelTemplateVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param thingsModelTemplateList
     * @return 物模型模板集合
     */
    List<ThingsModelTemplateVO> convertThingsModelTemplateVOList(List<ThingsModelTemplate> thingsModelTemplateList);

    /**
     * VO类转换为实体类
     *
     * @param thingsModelTemplateVOList
     * @return 物模型模板集合
     */
    List<ThingsModelTemplate> convertThingsModelTemplateList(List<ThingsModelTemplateVO> thingsModelTemplateVOList);

    /**
     * 分页转换
     * @param thingsModelTemplatePage
     * @return
     */
    Page<ThingsModelTemplateVO> convertThingsModelTemplateVOPage(Page<ThingsModelTemplate> thingsModelTemplatePage);
}
