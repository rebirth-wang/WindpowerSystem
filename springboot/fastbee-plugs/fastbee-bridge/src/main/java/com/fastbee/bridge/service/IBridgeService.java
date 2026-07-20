package com.fastbee.bridge.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.domain.vo.BridgeVO;

/**
 * 数据桥接Service接口
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */
public interface IBridgeService extends IService<Bridge> {

    /**
     * 查询数据桥接列表
     *
     * @param bridge 数据桥接
     * @return 数据桥接分页集合
     */
    Page<BridgeVO> pageBridgeVO(Bridge bridge);

    Bridge selectBridgeByName(String bridgeName);

    /**
     * 查询数据桥接
     *
     * @param bridge 桥接
     * @return 数据桥接
     */
    Bridge queryByIdWithCache(Bridge bridge);

    /**
     * 新增数据桥接
     *
     * @param bridge 数据桥接
     * @return 是否新增成功
     */
    Boolean insertWithCache(Bridge bridge);

    /**
     * 修改数据桥接
     *
     * @param bridge 数据桥接
     * @return 是否修改成功
     */
    Boolean updateWithCache(Bridge bridge);

    /**
     * 校验并批量删除数据桥接信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);

    int connect(Bridge bridge);

}
