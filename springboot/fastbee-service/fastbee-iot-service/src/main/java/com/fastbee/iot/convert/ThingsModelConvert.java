package com.fastbee.iot.convert;

import java.util.LinkedList;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.ThingsModel;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.dto.ThingsModelDTO;
import com.fastbee.iot.model.vo.ThingsModelVO;

/**
 * 物模型Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-23
 */
@Mapper
public interface ThingsModelConvert
{
    /** 代码生成区域 可直接覆盖**/
    ThingsModelConvert INSTANCE = Mappers.getMapper(ThingsModelConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param thingsModel
     * @return 物模型集合
     */
    ThingsModelVO convertThingsModelVO(ThingsModel thingsModel);

    /**
     * VO类转换为实体类集合
     *
     * @param thingsModelVO
     * @return 物模型集合
     */
    ThingsModel convertThingsModel(ThingsModelVO thingsModelVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param thingsModelList
     * @return 物模型集合
     */
    List<ThingsModelVO> convertThingsModelVOList(List<ThingsModel> thingsModelList);

    /**
     * VO类转换为实体类
     *
     * @param thingsModelVOList
     * @return 物模型集合
     */
    List<ThingsModel> convertThingsModelList(List<ThingsModelVO> thingsModelVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param thingsModelPage
     * @return 物模型分页
     */
    Page<ThingsModelVO> convertThingsModelVOPage(Page<ThingsModel> thingsModelPage);

    /**
     * VO类转换为实体类
     *
     * @param thingsModelVOPage
     * @return 物模型分页
     */
    Page<ThingsModel> convertThingsModelPage(Page<ThingsModelVO> thingsModelVOPage);

    LinkedList<ThingsModel> convertLinkedThingsModelList(LinkedList<ThingsModelVO> thingsModelLinkedList);

    ThingsModelDTO convertThingsModelDTO(ThingsModelValueItem valueItem);
}
