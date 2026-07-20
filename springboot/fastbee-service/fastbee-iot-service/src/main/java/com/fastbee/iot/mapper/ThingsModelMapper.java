package com.fastbee.iot.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.fastbee.common.extend.core.response.IdentityAndName;
import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.iot.domain.ThingsModel;
import com.fastbee.iot.model.ThingsModelPerm;
import com.fastbee.iot.model.ThingsModels.ThingsItems;
import com.fastbee.iot.model.modbus.ModbusAndThingsVO;
import com.fastbee.iot.model.vo.ThingsModelSimVO;
import com.fastbee.iot.model.vo.ThingsModelVO;

/**
 * 物模型Mapper接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
@Repository
public interface ThingsModelMapper extends BaseMapperX<ThingsModel>
{

    /**
     * 分页查询物模型列表
     * @param page 分页参数
     * @param thingsModelVO 查询参数
     * @return
     */
    Page<ThingsModelVO> pageThingsModelVO(Page<ThingsModelVO> page, @Param("thingsModelVO") ThingsModelVO thingsModelVO);

    /**
     * 根据 productId 查询 model_order 的最大值
     *
     * @param productId 产品ID
     * @return model_order 的最大值
     */
    Integer selectMaxModelOrderByProductId(Long productId);

    void updateSortToTop(@Param("thingsModelVO") ThingsModelVO thingsModelVO);

    void swapSortWithPrevious(@Param("thingsModelVO") ThingsModelVO thingsModelVO);

    void swapSortWithNext(@Param("thingsModelVO") ThingsModelVO thingsModelVO);

    /**
     * 查询物模型列表
     *
     * @param thingsModelVO 物模型
     * @return 物模型集合
     */
    public List<ThingsModelVO> selectThingsModelList(ThingsModelVO thingsModelVO);

    /**
     * 查询物模型对应分享设备权限列表
     *
     * @param productId 产品ID
     * @param language 语言
     * @return 物模型集合
     */
    public List<ThingsModelPerm> selectThingsModelPermList(@Param("productId") Long productId, @Param("language") String language);


    /**
     * 根据产品ID数组获取物模型列表
     * @param modelIds
     * @param language 语言
     * @return
     */
    public List<ThingsModel> selectThingsModelListByProductIds(@Param("modelIds") Long[] modelIds, @Param("language") String language);

    /**
     * 查询物模型是否历史存储
     * @param items
     * @return
     */
    public List<IdentityAndName> selectThingsModelIsMonitor(ThingsItems items);


    List<ThingsModelSimVO> listSimByProductIds(@Param("productIdList") List<Long> productIdList, @Param("language") String language);

    /**
     * 场景管理查询相关物模型列表
     * @param modelIdList 物模id
     * @return java.util.List<com.fastbee.iot.domain.ThingsModel>
     */
    List<ThingsModel> listSceneModelDataByModelIds(@Param("modelIdList") List<Long> modelIdList,@Param("language") String languag);

    /**
     * 获取modbus配置可选择物模型
     * @param productId
     * @return
     */
    Page<ModbusAndThingsVO> getModbusConfigUnSelectThingsModel(Page<ModbusAndThingsVO> page, @Param("productId")Long productId, @Param("language") String language);

    /**
     * 批量查询物模型
     * @param modelIdList 物模id集合
     * @return java.util.List<com.fastbee.iot.domain.ThingsModel>
     */
    List<ThingsModel> selectThingsModelListByModelIds(@Param("modelIdList") List<Long> modelIdList);

    /**
     * 根据设备编号获取读写物模型
     * @return
     */
    Page<ThingsModel> selectThingsModelBySerialNumber(Page<ThingsModel> page, @Param("deviceId") Long deviceId , @Param("language") String language);

    /**
     *
     * @param identifierList
     * @return
     */
    List<String> checkIdentifierList(@Param("productId") Long productId, @Param("identifierList") List<String> identifierList);

    int restoreThingsModelByProductId(Long productId);

    void deleteThingsModelByModelIds(Long productId);
}
