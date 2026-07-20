package com.fastbee.iot.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.ModbusParams;
import com.fastbee.iot.domain.ProductSubGateway;
import com.fastbee.iot.mapper.ModbusParamsMapper;
import com.fastbee.iot.mapper.ProductSubGatewayMapper;
import com.fastbee.iot.model.gateWay.ProductSubGatewayAddVO;
import com.fastbee.iot.model.gateWay.ProductSubGatewayVO;
import com.fastbee.iot.service.IProductSubGatewayService;

/**
 * 网关与子产品关联Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2024-09-04
 */
@Service
public class ProductSubGatewayServiceImpl extends ServiceImpl<ProductSubGatewayMapper,ProductSubGateway> implements IProductSubGatewayService {

    @Resource
    private ModbusParamsMapper modbusParamsMapper;

    /**
     * 查询网关与子产品关联
     *
     * @param id 主键
     * @return 网关与子产品关联
     */
    @Override
    @Cacheable(cacheNames = "sql_cache:ProductSubGateway", key = "#id")
    // 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
    public ProductSubGateway queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询网关与子产品关联
     *
     * @param id 主键
     * @return 网关与子产品关联
     */
    @Override
    @Cacheable(cacheNames = "sql_cache:ProductSubGateway", key = "#id")
    // 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
    public ProductSubGateway selectProductSubGatewayById(Long id){
        return this.getById(id);
    }

    /**
     * 查询网关与子产品关联列表
     *
     * @param productSubGateway 网关与子产品关联
     * @return 网关与子产品关联
     */
    @Override
    public Page<ProductSubGatewayVO> selectProductSubGatewayList(ProductSubGateway productSubGateway) {
        return baseMapper.selectListVO(new Page<>(productSubGateway.getPageNum(), productSubGateway.getPageSize()), productSubGateway);
    }

    private LambdaQueryWrapper<ProductSubGateway> buildQueryWrapper(ProductSubGateway query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ProductSubGateway> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getGwProductId() != null, ProductSubGateway::getGwProductId, query.getGwProductId());
                    lqw.eq(query.getSubProductId() != null, ProductSubGateway::getSubProductId, query.getSubProductId());
                    lqw.eq(StringUtils.isNotEmpty(query.getAddress()), ProductSubGateway::getAddress, query.getAddress());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(ProductSubGateway::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增网关与子产品关联
     *
     * @param add 网关与子产品关联
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ProductSubGateway add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改网关与子产品关联
     *
     * @param update 网关与子产品关联
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "sql_cache:ProductSubGateway", key = "#update.id")
    public Boolean updateWithCache(ProductSubGateway update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProductSubGateway entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除网关与子产品关联信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "sql_cache:ProductSubGateway", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

    @Override
    public int addBatch(ProductSubGatewayAddVO productSubGatewayAddVO) {
        Long gwProductId = productSubGatewayAddVO.getGwProductId();
        List<Long> subProductIdList = productSubGatewayAddVO.getSubProductIds();
        if (CollectionUtils.isEmpty(subProductIdList)) {
            return 1;
        }
        LambdaQueryWrapper<ProductSubGateway> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductSubGateway::getGwProductId, gwProductId);
        queryWrapper.in(ProductSubGateway::getSubProductId, subProductIdList);
        List<ProductSubGateway> productSubGateways = baseMapper.selectList(queryWrapper);
        List<Long> exitSubProductIdList = productSubGateways.stream().map(ProductSubGateway::getSubProductId).collect(Collectors.toList());
        subProductIdList.removeAll(exitSubProductIdList);
        List<ProductSubGateway> list = new ArrayList<>();
        for (Long subProductId : subProductIdList) {
            LambdaQueryWrapper<ModbusParams> modbusParamsWrapper = new LambdaQueryWrapper<>();
            modbusParamsWrapper.eq(ModbusParams::getProductId, subProductId);
            ModbusParams modbusParams = modbusParamsMapper.selectOne(modbusParamsWrapper);
            ProductSubGateway productSubGateway = new ProductSubGateway();
            productSubGateway.setGwProductId(gwProductId);
            productSubGateway.setSubProductId(subProductId);
            if (!Objects.isNull(modbusParams)) {
                productSubGateway.setAddress(modbusParams.getAddress());
            }
            productSubGateway.setCreateBy(SecurityUtils.getUsername());
            list.add(productSubGateway);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            baseMapper.insertBatch(list);
        }
        return 1;
    }

    @Override
    public int editBatch(List<ProductSubGateway> list) {
        // 同步产品轮询修改从机地址
        Long gwProductId = list.get(0).getGwProductId();
        LambdaQueryWrapper<ProductSubGateway> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductSubGateway::getGwProductId, gwProductId);
        List<ProductSubGateway> productSubGatewayList = baseMapper.selectList(queryWrapper);
        List<ProductSubGateway> collectSlaveIdList = productSubGatewayList.stream().filter(i -> null != i.getAddress()).collect(Collectors.toList());
        Map<Long, String> productSubGatewayMap = collectSlaveIdList.stream().collect(Collectors.toMap(ProductSubGateway::getSubProductId, ProductSubGateway::getAddress));
        List<ProductSubGateway> editList = list.stream().filter(i -> !i.getAddress().equals(productSubGatewayMap.get(i.getSubProductId()))).collect(Collectors.toList());
        baseMapper.updateBatch(list, list.size());
        if (CollectionUtils.isEmpty(editList)) {
            return 1;
        }
        List<Long> editSubProductIdList = editList.stream().map(ProductSubGateway::getSubProductId).collect(Collectors.toList());
        LambdaQueryWrapper<ModbusParams> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.in(ModbusParams::getProductId, editSubProductIdList);
        List<ModbusParams> modbusParamsList = modbusParamsMapper.selectList(queryWrapper2);
        Map<Long, ProductSubGateway> editMap = editList.stream().collect(Collectors.toMap(ProductSubGateway::getSubProductId, Function.identity()));
        if (CollectionUtils.isNotEmpty(modbusParamsList)) {
            for (ModbusParams modbusParams : modbusParamsList) {
                ProductSubGateway productSubGateway = editMap.get(modbusParams.getProductId());
                if (Objects.nonNull(productSubGateway)) {
                    modbusParams.setAddress(productSubGateway.getAddress());
                }
            }
            modbusParamsMapper.updateBatch(modbusParamsList, modbusParamsList.size());
        }
        return 1;
    }

}
