package com.fastbee.iot.service.impl;

import static org.apache.commons.lang3.SystemUtils.getUserName;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.enums.ModbusJobCommantEnum;
import com.fastbee.common.extend.enums.ModbusParamsPollTypeEnum;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.ModbusParams;
import com.fastbee.iot.domain.ProductModbusJob;
import com.fastbee.iot.mapper.ModbusParamsMapper;
import com.fastbee.iot.mapper.ProductModbusJobMapper;
import com.fastbee.iot.service.IModbusParamsService;

/**
 * 产品modbus配置参数Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2024-08-20
 */
@Service
public class ModbusParamsServiceImpl extends ServiceImpl<ModbusParamsMapper,ModbusParams> implements IModbusParamsService {

    @Resource
    private ProductModbusJobMapper productModbusJobMapper;

    /**
     * 查询产品modbus配置参数
     *
     * @param id 主键
     * @return 产品modbus配置参数
     */
    @Override
    @Cacheable(cacheNames = "ModbusParams", key = "#id")
    // 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
    public ModbusParams queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询产品modbus配置参数
     *
     * @param id 主键
     * @return 产品modbus配置参数
     */
    @Override
    @Cacheable(cacheNames = "ModbusParams", key = "#id")
    // 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
    public ModbusParams selectModbusParamsById(Long id){
        return this.getById(id);
    }

    /**
     * 查询产品modbus配置参数列表
     *
     * @param modbusParams 产品modbus配置参数
     * @return 产品modbus配置参数
     */
    @Override
    public Page<ModbusParams> selectModbusParamsList(ModbusParams modbusParams) {
        LambdaQueryWrapper<ModbusParams> lqw = buildQueryWrapper(modbusParams);
        return baseMapper.selectPage(new Page<>(modbusParams.getPageNum(), modbusParams.getPageSize()),lqw);
    }

    private LambdaQueryWrapper<ModbusParams> buildQueryWrapper(ModbusParams query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ModbusParams> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getProductId() != null, ModbusParams::getProductId, query.getProductId());
                    lqw.eq(query.getPollType() != null, ModbusParams::getPollType, query.getPollType());
                    lqw.eq(StringUtils.isNotBlank(query.getAddress()), ModbusParams::getAddress, query.getAddress());
                    lqw.eq(query.getStatusDeter() != null, ModbusParams::getStatusDeter, query.getStatusDeter());
                    lqw.eq(StringUtils.isNotBlank(query.getDeterTimer()), ModbusParams::getDeterTimer, query.getDeterTimer());
                    lqw.eq(query.getPollLength() != null, ModbusParams::getPollLength, query.getPollLength());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(ModbusParams::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增产品modbus配置参数
     *
     * @param add 产品modbus配置参数
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ModbusParams add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 更新或新增
     * @param modbusParams
     */
    @Override
    public boolean addOrUpdate(ModbusParams modbusParams){
        String userName = getUserName();
        Long productId = modbusParams.getProductId();
        ModbusParams params = this.getModbusParamsByProductId(productId);
        if (!Objects.isNull(params)){
            modbusParams.setId(params.getId());
            modbusParams.setUpdateBy(userName);
        } else {
            modbusParams.setCreateBy(userName);
        }
        boolean b = this.saveOrUpdate(modbusParams);
        if (b && ModbusParamsPollTypeEnum.EDGE_COLLECTION.getType().equals(modbusParams.getPollType())) {
            // 删除产品轮询任务配置
            LambdaQueryWrapper<ProductModbusJob> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ProductModbusJob::getProductId, productId);
            queryWrapper.eq(ProductModbusJob::getCommandType, ModbusJobCommantEnum.POLLING.getType());
            productModbusJobMapper.delete(queryWrapper);
        }
        return b;
    }

    /**
     * 修改产品modbus配置参数
     *
     * @param update 产品modbus配置参数
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "ModbusParams", key = "#update.id")
    public Boolean updateWithCache(ModbusParams update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ModbusParams entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除产品modbus配置参数信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "ModbusParams", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

    /**
     * 根据产品io获取modbus配置
     * @param productId
     * @return
     */
    @Override
    public ModbusParams getModbusParamsByProductId(Long productId){
        LambdaQueryWrapper<ModbusParams> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ModbusParams::getProductId, productId);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据设备id获取modbus配置
     * @param clientId
     * @return
     */
    @Override
    public ModbusParams getModbusParamsByClientId(String clientId){
        return baseMapper.getModbusParamsByClientId(clientId);
    }

    @Override
    public List<ModbusParams> selectModbusParamsListByProductIds(List<Long> productIdList) {
        LambdaQueryWrapper<ModbusParams> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ModbusParams::getProductId, productIdList);
        return baseMapper.selectList(queryWrapper);
    }
}
