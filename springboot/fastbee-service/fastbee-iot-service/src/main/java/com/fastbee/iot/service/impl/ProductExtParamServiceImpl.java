package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;
import static com.fastbee.common.extend.utils.SecurityUtils.getUsername;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.ProductExtParamConvert;
import com.fastbee.iot.domain.ProductExtParam;
import com.fastbee.iot.mapper.ProductExtParamMapper;
import com.fastbee.iot.model.vo.ProductExtParamVO;
import com.fastbee.iot.service.IProductExtParamService;

/**
 * 产品扩展参数Service业务层处理
 *
 * @author fastbee
 * @date 2026-03-18
 */
@Service
public class ProductExtParamServiceImpl extends ServiceImpl<ProductExtParamMapper,ProductExtParam> implements IProductExtParamService {

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询产品扩展参数
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param productExtParam 产品扩展参数
     * @return 产品扩展参数
     */
    @Override
    @Cacheable(cacheNames = "ProductExtParam", key = "#productExtParam.paramId")
    public ProductExtParam queryByIdWithCache(ProductExtParam productExtParam){
        LambdaQueryWrapper<ProductExtParam> queryWrapper = this.buildQueryWrapper(productExtParam);
        return this.getOne(queryWrapper);
    }

    /**
     * 查询产品扩展参数
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param paramId 主键
     * @return 产品扩展参数
     */
    @Override
    @Cacheable(cacheNames = "ProductExtParam", key = "#paramId")
    public ProductExtParam selectProductExtParamById(Long paramId){
        return this.getById(paramId);
    }

    /**
     * 查询产品扩展参数分页列表
     *
     * @param productExtParam 产品扩展参数
     * @return 产品扩展参数
     */
    @Override
    public Page<ProductExtParamVO> pageProductExtParamVO(ProductExtParam productExtParam) {
        LambdaQueryWrapper<ProductExtParam> lqw = buildQueryWrapper(productExtParam);
        Page<ProductExtParam> productExtParamPage = baseMapper.selectPage(new Page<>(productExtParam.getPageNum(), productExtParam.getPageSize()), lqw);
        return ProductExtParamConvert.INSTANCE.convertProductExtParamVOPage(productExtParamPage);
    }

    /**
     * 查询产品扩展参数列表
     *
     * @param productExtParam 产品扩展参数
     * @return 产品扩展参数
     */
    @Override
    public List<ProductExtParamVO> listProductExtParamVO(ProductExtParam productExtParam) {
        LambdaQueryWrapper<ProductExtParam> lqw = buildQueryWrapper(productExtParam);
        List<ProductExtParam> productExtParamList = baseMapper.selectList(lqw);
        return ProductExtParamConvert.INSTANCE.convertProductExtParamVOList(productExtParamList);
    }

    private LambdaQueryWrapper<ProductExtParam> buildQueryWrapper(ProductExtParam query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ProductExtParam> lqw = Wrappers.lambdaQuery();
                    lqw.eq(query.getParamId() != null, ProductExtParam::getParamId, query.getParamId());
                    lqw.eq(query.getProductId() != null, ProductExtParam::getProductId, query.getProductId());
                    lqw.like(StringUtils.isNotBlank(query.getParamName()), ProductExtParam::getParamName, query.getParamName());
                    lqw.eq(query.getParamType() != null, ProductExtParam::getParamType, query.getParamType());
                    lqw.eq(StringUtils.isNotBlank(query.getDefaultValue()), ProductExtParam::getDefaultValue, query.getDefaultValue());
                    lqw.eq(query.getIsEnabled() != null, ProductExtParam::getIsEnabled, query.getIsEnabled());
                    lqw.eq(query.getIsAppShow() != null, ProductExtParam::getIsAppShow, query.getIsAppShow());
                    lqw.eq(StringUtils.isNotBlank(query.getSpec()), ProductExtParam::getSpec, query.getSpec());
                    lqw.eq(StringUtils.isNotBlank(query.getDescription()), ProductExtParam::getDescription, query.getDescription());
                    lqw.eq(query.getDelFlag() != null, ProductExtParam::getDelFlag, query.getDelFlag());
                    lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), ProductExtParam::getCreateBy, query.getCreateBy());
                    lqw.eq(query.getCreateTime() != null, ProductExtParam::getCreateTime, query.getCreateTime());
                    lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), ProductExtParam::getUpdateBy, query.getUpdateBy());
                    lqw.eq(query.getUpdateTime() != null, ProductExtParam::getUpdateTime, query.getUpdateTime());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(ProductExtParam::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增产品扩展参数
     *
     * @param add 产品扩展参数
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(ProductExtParam add) {
        SysUser sysUser = getLoginUser().getUser();
        add.setCreateBy(sysUser.getUserName());
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改产品扩展参数
     *
     * @param update 产品扩展参数
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "ProductExtParam", key = "#update.paramId")
    public Boolean updateWithCache(ProductExtParam update) {
        update.setUpdateBy(getUsername());
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProductExtParam entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除产品扩展参数信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "ProductExtParam", keyGenerator = "deleteKeyGenerator" )
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
