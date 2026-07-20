package com.fastbee.system.service.impl;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.domain.SysDictDataTranslate;
import com.fastbee.system.mapper.SysDictDataTranslateMapper;
import com.fastbee.system.service.ISysDictDataTranslateService;

/**
 * 字典数据翻译Service业务层处理
 *
 * @author gx_ma
 * @date 2025-01-10
 */
@Service
public class SysDictDataTranslateServiceImpl extends ServiceImpl<SysDictDataTranslateMapper,SysDictDataTranslate> implements ISysDictDataTranslateService {

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询字典数据翻译
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 字典数据翻译
     */
    @Override
    @Cacheable(cacheNames = "SysDictDataTranslate", key = "#id")
    public SysDictDataTranslate queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询字典数据翻译
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 字典数据翻译
     */
    @Override
    @Cacheable(cacheNames = "SysDictDataTranslate", key = "#id")
    public SysDictDataTranslate selectSysDictDataTranslateById(Long id){
        return this.getById(id);
    }

    /**
     * 查询字典数据翻译分页列表
     *
     * @param sysDictDataTranslate 字典数据翻译
     * @return 字典数据翻译
     */
    @Override
    public Page<SysDictDataTranslate> pageSysDictDataTranslateVO(SysDictDataTranslate sysDictDataTranslate) {
        LambdaQueryWrapper<SysDictDataTranslate> lqw = buildQueryWrapper(sysDictDataTranslate);
        return baseMapper.selectPage(new Page<>(sysDictDataTranslate.getPageNum(), sysDictDataTranslate.getPageSize()), lqw);
    }

    /**
     * 查询字典数据翻译列表
     *
     * @param sysDictDataTranslate 字典数据翻译
     * @return 字典数据翻译
     */
    @Override
    public List<SysDictDataTranslate> listSysDictDataTranslateVO(SysDictDataTranslate sysDictDataTranslate) {
        LambdaQueryWrapper<SysDictDataTranslate> lqw = buildQueryWrapper(sysDictDataTranslate);
        return baseMapper.selectList(lqw);
    }

    private LambdaQueryWrapper<SysDictDataTranslate> buildQueryWrapper(SysDictDataTranslate query) {
        LambdaQueryWrapper<SysDictDataTranslate> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, SysDictDataTranslate::getId, query.getId());
        lqw.eq(StringUtils.isNotBlank(query.getZhCn()), SysDictDataTranslate::getZhCn, query.getZhCn());
        lqw.eq(StringUtils.isNotBlank(query.getEnUs()), SysDictDataTranslate::getEnUs, query.getEnUs());

        return lqw;
    }

    /**
     * 新增字典数据翻译
     *
     * @param add 字典数据翻译
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(SysDictDataTranslate add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改字典数据翻译
     *
     * @param update 字典数据翻译
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "SysDictDataTranslate", key = "#update.id")
    public Boolean updateWithCache(SysDictDataTranslate update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysDictDataTranslate entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除字典数据翻译信息
     *
     * @param ids 待删除的主键集合
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "SysDictDataTranslate", keyGenerator = "deleteKeyGenerator" )
    public boolean deleteWithCacheByIds(Long[] ids) {
        return this.removeBatchByIds(Arrays.asList(ids));
    }
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
