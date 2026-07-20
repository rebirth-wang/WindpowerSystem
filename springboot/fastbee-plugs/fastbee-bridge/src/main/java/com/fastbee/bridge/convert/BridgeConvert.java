package com.fastbee.bridge.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.domain.vo.BridgeVO;

/**
 * 数据桥接Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-11-15
 */
@Mapper
public interface BridgeConvert
{

    BridgeConvert INSTANCE = Mappers.getMapper(BridgeConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param bridge
     * @return 数据桥接集合
     */
    BridgeVO convertBridgeVO(Bridge bridge);

    /**
     * VO类转换为实体类集合
     *
     * @param bridgeVO
     * @return 数据桥接集合
     */
    Bridge convertBridge(BridgeVO bridgeVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param bridgeList
     * @return 数据桥接集合
     */
    List<BridgeVO> convertBridgeVOList(List<Bridge> bridgeList);

    /**
     * VO类转换为实体类
     *
     * @param bridgeVOList
     * @return 数据桥接集合
     */
    List<Bridge> convertBridgeList(List<BridgeVO> bridgeVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param bridgePage
     * @return 数据桥接分页
     */
    Page<BridgeVO> convertBridgeVOPage(Page<Bridge> bridgePage);

    /**
     * VO类转换为实体类
     *
     * @param bridgeVOPage
     * @return 数据桥接分页
     */
    Page<Bridge> convertBridgePage(Page<BridgeVO> bridgeVOPage);
}
