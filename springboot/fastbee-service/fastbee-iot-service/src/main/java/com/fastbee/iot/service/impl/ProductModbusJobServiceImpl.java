package com.fastbee.iot.service.impl;

import java.util.*;
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

import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.ProductModbusJobConvert;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.enums.DeviceType;
import com.fastbee.iot.mapper.*;
import com.fastbee.iot.model.modbus.ProductModbusJobVO;
import com.fastbee.iot.service.IProductModbusJobService;
import com.fastbee.iot.util.JobCronUtils;

/**
 * 产品轮训任务列Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2024-09-04
 */
@Service
public class ProductModbusJobServiceImpl extends ServiceImpl<ProductModbusJobMapper,ProductModbusJob> implements IProductModbusJobService {

    @Resource
    private SubGatewayMapper subGatewayMapper;
    @Resource
    private ModbusConfigMapper modbusConfigMapper;
    @Resource
    private ProductSubGatewayMapper productSubGatewayMapper;

    /**
     * 查询产品轮训任务列
     *
     * @param taskId 主键
     * @return 产品轮训任务列
     */
    @Override
    @Cacheable(cacheNames = "sql_cache:ProductModbusJob", key = "#taskId")
    // 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
    public ProductModbusJob queryByIdWithCache(Long taskId){
        return this.getById(taskId);
    }

    /**
     * 查询产品轮训任务列
     *
     * @param taskId 主键
     * @return 产品轮训任务列
     */
    @Override
    @Cacheable(cacheNames = "sql_cache:ProductModbusJob", key = "#taskId")
    // 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
    public ProductModbusJob selectProductModbusJobById(Long taskId){
        return this.getById(taskId);
    }

    @Override
    public Page<ProductModbusJobVO> pageProductModbusJobVO(ProductModbusJob productModbusJob) {
        LambdaQueryWrapper<ProductModbusJob> queryWrapper = this.buildQueryWrapper(productModbusJob);
        Page<ProductModbusJob> productModbusJobPage = baseMapper.selectPage(new Page<>(productModbusJob.getPageNum(), productModbusJob.getPageSize()), queryWrapper);
        if (0 == productModbusJobPage.getTotal()) {
            return new Page<>();
        }
        Page<ProductModbusJobVO> voPage = ProductModbusJobConvert.INSTANCE.convertProductModbusJobVOPage(productModbusJobPage);
        for (ProductModbusJobVO productModbusJobVO : voPage.getRecords()) {
            if (StringUtils.isNotEmpty(productModbusJobVO.getRemark())) {
                productModbusJobVO.setRemarkStr(JobCronUtils.handleCronCycle(1, productModbusJobVO.getRemark()).getDesc());
            }
        }
        return voPage;
    }

    /**
     * 查询产品轮训任务列列表
     *
     * @param productModbusJob 产品轮训任务列
     * @return 产品轮训任务列
     */
    @Override
    public List<ProductModbusJobVO> selectProductModbusJobVOList(ProductModbusJob productModbusJob) {
        LambdaQueryWrapper<ProductModbusJob> lqw = buildQueryWrapper(productModbusJob);
        List<ProductModbusJob> productModbusJobList = baseMapper.selectList(lqw);
        List<ProductModbusJobVO> productModbusJobVOList = ProductModbusJobConvert.INSTANCE.convertProductModbusJobVOList(productModbusJobList);
        for (ProductModbusJobVO productModbusJobVO : productModbusJobVOList) {
            if (StringUtils.isNotEmpty(productModbusJobVO.getRemark())) {
                productModbusJobVO.setRemarkStr(JobCronUtils.handleCronCycle(1, productModbusJobVO.getRemark()).getDesc());
            }
        }
        return productModbusJobVOList;
    }

    private LambdaQueryWrapper<ProductModbusJob> buildQueryWrapper(ProductModbusJob query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ProductModbusJob> lqw = Wrappers.lambdaQuery();
                    lqw.like(StringUtils.isNotBlank(query.getJobName()), ProductModbusJob::getJobName, query.getJobName());
                    lqw.eq(query.getProductId() != null, ProductModbusJob::getProductId, query.getProductId());
                    lqw.eq(query.getCommandType() != null, ProductModbusJob::getCommandType, query.getCommandType());
                    lqw.eq(StringUtils.isNotBlank(query.getCommand()), ProductModbusJob::getCommand, query.getCommand());
                    lqw.eq(query.getStatus() != null, ProductModbusJob::getStatus, query.getStatus());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(ProductModbusJob::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增产品轮训任务列
     *
     * @param add 产品轮训任务列
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ProductModbusJob add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改产品轮训任务列
     *
     * @param update 产品轮训任务列
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "sql_cache:ProductModbusJob", key = "#update.taskId")
    public Boolean updateWithCache(ProductModbusJob update) {
        validEntityBeforeSave(update);
        if (null != update.getTaskId()) {
            if ("[]".equals(update.getCommand())) {
                return this.removeById(update.getTaskId());
            }
            return this.updateById(update);
        } else {
            return this.save(update);
        }
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProductModbusJob entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除产品轮训任务列信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "sql_cache:ProductModbusJob", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

    @Override
    public List<String> getAddress(Long productId, String serialNumber, Integer deviceType) {
        if (DeviceType.DIRECT_DEVICE.getCode() == deviceType) {
            if (StringUtils.isEmpty(serialNumber)) {
                LambdaQueryWrapper<ModbusConfig> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.select(ModbusConfig::getAddress);
                queryWrapper.eq(ModbusConfig::getProductId, productId);
                List<ModbusConfig> modbusConfigList = modbusConfigMapper.selectList(queryWrapper);
                if (CollectionUtils.isNotEmpty(modbusConfigList)) {
                    // 添加过滤null值的操作
                    return modbusConfigList.stream()
                            .filter(Objects::nonNull)
                            .map(ModbusConfig::getAddress)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());
                }
            }
        }
        if (DeviceType.GATEWAY.getCode() == deviceType) {
            if (StringUtils.isEmpty(serialNumber)) {
                LambdaQueryWrapper<ProductSubGateway> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.select(ProductSubGateway::getAddress);
                queryWrapper.eq(ProductSubGateway::getGwProductId, productId);
                List<ProductSubGateway> productSubGatewayList = productSubGatewayMapper.selectList(queryWrapper);
                if (CollectionUtils.isNotEmpty(productSubGatewayList)) {
                    // 添加过滤null值的操作
                    return productSubGatewayList.stream()
                            .filter(Objects::nonNull)
                            .map(ProductSubGateway::getAddress)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());                }
            } else {
                LambdaQueryWrapper<SubGateway> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.select(SubGateway::getAddress);
                queryWrapper.eq(SubGateway::getParentClientId, serialNumber);
                List<SubGateway> subGatewayList = subGatewayMapper.selectList(queryWrapper);
                if (CollectionUtils.isNotEmpty(subGatewayList)) {
                    // 添加过滤null值的操作
                    return subGatewayList.stream()
                            .filter(Objects::nonNull)
                            .map(SubGateway::getAddress)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());                }
            }
        }
        return new ArrayList<>();
    }

}
