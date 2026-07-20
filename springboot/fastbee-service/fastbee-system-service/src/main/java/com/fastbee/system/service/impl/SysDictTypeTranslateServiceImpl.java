package com.fastbee.system.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.convert.SysDictTypeTranslateConvert;
import com.fastbee.system.domain.SysDictTypeTranslate;
import com.fastbee.system.domain.vo.SysDictTypeTranslateVO;
import com.fastbee.system.mapper.SysDictTypeTranslateMapper;
import com.fastbee.system.service.ISysDictTypeTranslateService;

/**
 * 字典类型翻译Service业务层处理
 *
 * @author zhuangpeng.li
 * @date 2025-01-10
 */
@Service
public class SysDictTypeTranslateServiceImpl extends ServiceImpl<SysDictTypeTranslateMapper, SysDictTypeTranslate> implements ISysDictTypeTranslateService {

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询字典类型翻译
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 字典类型翻译
     */
    @Override
    @Cacheable(cacheNames = "SysDictTypeTranslate", key = "#id")
    public SysDictTypeTranslate queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询字典类型翻译
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 字典类型翻译
     */
    @Override
    @Cacheable(cacheNames = "SysDictTypeTranslate", key = "#id")
    public SysDictTypeTranslate selectSysDictTypeTranslateById(Long id){
        return this.getById(id);
    }

    /**
     * 查询字典类型翻译分页列表
     *
     * @param sysDictTypeTranslate 字典类型翻译
     * @return 字典类型翻译
     */
    @Override
    public Page<SysDictTypeTranslateVO> pageSysDictTypeTranslateVO(SysDictTypeTranslate sysDictTypeTranslate) {
        LambdaQueryWrapper<SysDictTypeTranslate> lqw = buildQueryWrapper(sysDictTypeTranslate);
        Page<SysDictTypeTranslate> sysDictTypeTranslatePage = baseMapper.selectPage(new Page<>(sysDictTypeTranslate.getPageNum(), sysDictTypeTranslate.getPageSize()), lqw);
        return SysDictTypeTranslateConvert.INSTANCE.convertSysDictTypeTranslateVOPage(sysDictTypeTranslatePage);
    }

    /**
     * 查询字典类型翻译列表
     *
     * @param sysDictTypeTranslate 字典类型翻译
     * @return 字典类型翻译
     */
    @Override
    public List<SysDictTypeTranslateVO> listSysDictTypeTranslateVO(SysDictTypeTranslate sysDictTypeTranslate) {
        LambdaQueryWrapper<SysDictTypeTranslate> lqw = buildQueryWrapper(sysDictTypeTranslate);
        List<SysDictTypeTranslate> sysDictTypeTranslateList = baseMapper.selectList(lqw);
        return SysDictTypeTranslateConvert.INSTANCE.convertSysDictTypeTranslateVOList(sysDictTypeTranslateList);
    }

    private LambdaQueryWrapper<SysDictTypeTranslate> buildQueryWrapper(SysDictTypeTranslate query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysDictTypeTranslate> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, SysDictTypeTranslate::getId, query.getId());
        lqw.eq(StringUtils.isNotBlank(query.getZhCn()), SysDictTypeTranslate::getZhCn, query.getZhCn());
        lqw.eq(StringUtils.isNotBlank(query.getEnUs()), SysDictTypeTranslate::getEnUs, query.getEnUs());

        return lqw;
    }

    /**
     * 新增字典类型翻译
     *
     * @param add 字典类型翻译
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(SysDictTypeTranslate add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改字典类型翻译
     *
     * @param update 字典类型翻译
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "SysDictTypeTranslate", key = "#update.id")
    public Boolean updateWithCache(SysDictTypeTranslate update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysDictTypeTranslate entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除字典类型翻译信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "SysDictTypeTranslate", keyGenerator = "deleteKeyGenerator" )
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
