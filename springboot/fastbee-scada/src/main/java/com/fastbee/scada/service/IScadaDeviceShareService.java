package com.fastbee.scada.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.scada.domain.ScadaDeviceShare;
import com.fastbee.scada.vo.ScadaShareVO;

/**
 * shareService接口
 *
 * @author zhuangpeng.li
 * @date 2024-08-20
 */
public interface IScadaDeviceShareService extends IService<ScadaDeviceShare>
{

    /**
     * 查询share列表
     *
     * @param scadaDeviceShare share
     * @return share集合
     */
     List<ScadaDeviceShare> selectScadaDeviceShareList(ScadaDeviceShare scadaDeviceShare);

    /**
     * 查询share
     *
     * @param id 主键
     * @return share
     */
    ScadaDeviceShare queryByIdWithCache(Long id);

    /**
     * 新增share
     *
     * @param scadaDeviceShare share
     * @return 是否新增成功
     */
    Boolean insertWithCache(ScadaDeviceShare scadaDeviceShare);

    /**
     * 修改share
     *
     * @param scadaDeviceShare share
     * @return 是否修改成功
     */
    Boolean updateWithCache(ScadaDeviceShare scadaDeviceShare);

    /**
     * 校验并批量删除share信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);

    /**
     * @description: 查询设备分享信息
     * @param: scadaVO 组态信息
     * @return: com.fastbee.scada.domain.ScadaDeviceShare
     */
    ScadaDeviceShare getByGuidAndSerialNumber(ScadaShareVO scadaShareVO);

    /**
     * @description: 更新实体类
     * @param: editScadaDeviceShare 实体类
     * @return: void
     */
    void updateOneById(ScadaDeviceShare editScadaDeviceShare);
}
