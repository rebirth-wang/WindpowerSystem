package com.fastbee.iot.convert;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.iot.domain.GoviewProjectData;
import com.fastbee.iot.model.vo.GoviewProjectDataVO;

/**
 * 项目数据关联Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */
@Mapper
public interface GoviewProjectDataConvert
{

    GoviewProjectDataConvert INSTANCE = Mappers.getMapper(GoviewProjectDataConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param goviewProjectData
     * @return 项目数据关联集合
     */
    GoviewProjectDataVO convertGoviewProjectDataVO(GoviewProjectData goviewProjectData);

    /**
     * VO类转换为实体类集合
     *
     * @param goviewProjectDataVO
     * @return 项目数据关联集合
     */
    GoviewProjectData convertGoviewProjectData(GoviewProjectDataVO goviewProjectDataVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param goviewProjectDataList
     * @return 项目数据关联集合
     */
    List<GoviewProjectDataVO> convertGoviewProjectDataVOList(List<GoviewProjectData> goviewProjectDataList);

    /**
     * VO类转换为实体类
     *
     * @param goviewProjectDataVOList
     * @return 项目数据关联集合
     */
    List<GoviewProjectData> convertGoviewProjectDataList(List<GoviewProjectDataVO> goviewProjectDataVOList);
}
