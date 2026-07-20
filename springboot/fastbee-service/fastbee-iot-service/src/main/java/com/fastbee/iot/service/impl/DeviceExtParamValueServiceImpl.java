package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;
import static com.fastbee.common.extend.utils.SecurityUtils.getUsername;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.DeviceExtParamValueConvert;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceExtParamValue;
import com.fastbee.iot.domain.ProductExtParam;
import com.fastbee.iot.mapper.DeviceExtParamValueMapper;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.ProductExtParamMapper;
import com.fastbee.iot.model.vo.DeviceExtParamValueVO;
import com.fastbee.iot.model.vo.ProductExtParamVO;
import com.fastbee.iot.service.IDeviceExtParamValueService;

/**
 * 设备扩展参数值Service业务层处理
 *
 * @author fastbee
 * @date 2026-03-18
 */
@Service
public class DeviceExtParamValueServiceImpl extends ServiceImpl<DeviceExtParamValueMapper,DeviceExtParamValue> implements IDeviceExtParamValueService {

    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private ProductExtParamMapper productExtParamMapper;
    /**
     * 查询设备扩展参数值
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param deviceExtParamValue 设备扩展参数值
     * @return 设备扩展参数值
     */
    @Override
    @DataScope()
    @Cacheable(cacheNames = "DeviceExtParamValue", key = "#deviceExtParamValue.id")
    public DeviceExtParamValue queryByIdWithCache(DeviceExtParamValue deviceExtParamValue){
        LambdaQueryWrapper<DeviceExtParamValue> queryWrapper = this.buildQueryWrapper(deviceExtParamValue);
        return this.getOne(queryWrapper);
    }

    /**
     * 查询设备扩展参数值
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 设备扩展参数值
     */
    @Override
    @Cacheable(cacheNames = "DeviceExtParamValue", key = "#id")
    public DeviceExtParamValue selectDeviceExtParamValueById(Long id){
        return this.getById(id);
    }

    /**
     * 查询设备扩展参数值分页列表
     *
     * @param deviceExtParamValue 设备扩展参数值
     * @return 设备扩展参数值
     */
    @Override
    public Page<ProductExtParamVO> pageDeviceExtParamValueVO(DeviceExtParamValue deviceExtParamValue) {
        Long deviceId = deviceExtParamValue.getDeviceId();
        Device device = deviceMapper.selectListByDeviceId(deviceId).stream().findFirst().orElse(null);
        if (device == null) {
            return new Page<>(deviceExtParamValue.getPageNum(), deviceExtParamValue.getPageSize());
        }
        Long productId = device.getProductId();
        Page<ProductExtParam> productExtParamPage = productExtParamMapper.selectPage(new Page<>(deviceExtParamValue.getPageNum(), deviceExtParamValue.getPageSize()),
                Wrappers.lambdaQuery(ProductExtParam.class).eq(ProductExtParam::getProductId, productId));
        List<ProductExtParamVO> productExtParamVOList = productExtParamPage.getRecords().stream().map(productExtParam -> {
            ProductExtParamVO vo = new ProductExtParamVO();
            BeanUtils.copyProperties(productExtParam, vo);
            return vo;
        }).collect(Collectors.toList());
        List<DeviceExtParamValueVO> deviceExtParamValueVOList = this.listDeviceExtParamValueVO(deviceExtParamValue);
        Map<Long, DeviceExtParamValueVO> paramValueMap = deviceExtParamValueVOList.stream()
                .collect(Collectors.toMap(DeviceExtParamValueVO::getParamId, vo -> vo, (v1, v2) -> v1));
        productExtParamVOList.forEach(vo -> {
            DeviceExtParamValueVO extParamValueVO = paramValueMap.get(vo.getParamId());
            if (extParamValueVO != null) {
                vo.setId(extParamValueVO.getId());
                vo.setParamValue(extParamValueVO.getParamValue());
            } else {
                vo.setParamValue(vo.getDefaultValue());
            }
        });
        Page<ProductExtParamVO> resultPage = new Page<>(productExtParamPage.getCurrent(), productExtParamPage.getSize(), productExtParamPage.getTotal());
        resultPage.setRecords(productExtParamVOList);
        return resultPage;
    }

    /**
     * 查询设备扩展参数值列表
     *
     * @param deviceExtParamValue 设备扩展参数值
     * @return 设备扩展参数值
     */
    @Override
    public List<DeviceExtParamValueVO> listDeviceExtParamValueVO(DeviceExtParamValue deviceExtParamValue) {
        LambdaQueryWrapper<DeviceExtParamValue> lqw = buildQueryWrapper(deviceExtParamValue);
        List<DeviceExtParamValue> deviceExtParamValueList = baseMapper.selectList(lqw);
        return DeviceExtParamValueConvert.INSTANCE.convertDeviceExtParamValueVOList(deviceExtParamValueList);
    }

    private LambdaQueryWrapper<DeviceExtParamValue> buildQueryWrapper(DeviceExtParamValue query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<DeviceExtParamValue> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getId() != null, DeviceExtParamValue::getId, query.getId());
                    lqw.eq(query.getDeviceId() != null, DeviceExtParamValue::getDeviceId, query.getDeviceId());
                    lqw.eq(query.getParamId() != null, DeviceExtParamValue::getParamId, query.getParamId());
                    lqw.eq(StringUtils.isNotBlank(query.getParamValue()), DeviceExtParamValue::getParamValue, query.getParamValue());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), DeviceExtParamValue::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, DeviceExtParamValue::getUpdateTime, query.getUpdateTime());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(DeviceExtParamValue::getUpdateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增设备扩展参数值
     *
     * @param add 设备扩展参数值
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(DeviceExtParamValue add) {
        SysUser sysUser = getLoginUser().getUser();
        add.setUpdateBy(sysUser.getUserName());
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改设备扩展参数值
     *
     * @param update 设备扩展参数值
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "DeviceExtParamValue", key = "#update.id")
    public Boolean updateWithCache(DeviceExtParamValue update) {
        update.setUpdateBy(getUsername());
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(DeviceExtParamValue entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除设备扩展参数值信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "DeviceExtParamValue", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
