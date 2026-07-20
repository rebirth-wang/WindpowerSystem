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
import com.fastbee.system.convert.SysMenuTranslateConvert;
import com.fastbee.system.domain.SysMenuTranslate;
import com.fastbee.system.domain.vo.SysMenuTranslateVO;
import com.fastbee.system.mapper.SysMenuTranslateMapper;
import com.fastbee.system.service.ISysMenuTranslateService;

/**
 * 菜单名称翻译Service业务层处理
 *
 * @author fastbee
 * @date 2025-12-26
 */
@Service
public class SysMenuTranslateServiceImpl extends ServiceImpl<SysMenuTranslateMapper, SysMenuTranslate> implements ISysMenuTranslateService {

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询菜单名称翻译
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 菜单名称翻译
     */
    @Override
    @Cacheable(cacheNames = "SysMenuTranslate", key = "#id")
    public SysMenuTranslate queryByIdWithCache(Long id){
        return this.getById(id);
    }

    /**
     * 查询菜单名称翻译
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 菜单名称翻译
     */
    @Override
    @Cacheable(cacheNames = "SysMenuTranslate", key = "#id")
    public SysMenuTranslate selectSysMenuTranslateById(Long id){
        return this.getById(id);
    }

    /**
     * 查询菜单名称翻译分页列表
     *
     * @param sysMenuTranslate 菜单名称翻译
     * @return 菜单名称翻译
     */
    @Override
    public Page<SysMenuTranslateVO> pageSysMenuTranslateVO(SysMenuTranslate sysMenuTranslate) {
        LambdaQueryWrapper<SysMenuTranslate> lqw = buildQueryWrapper(sysMenuTranslate);
        Page<SysMenuTranslate> sysMenuTranslatePage = baseMapper.selectPage(new Page<>(sysMenuTranslate.getPageNum(), sysMenuTranslate.getPageSize()), lqw);
        return SysMenuTranslateConvert.INSTANCE.convertSysMenuTranslateVOPage(sysMenuTranslatePage);
    }

    /**
     * 查询菜单名称翻译列表
     *
     * @param sysMenuTranslate 菜单名称翻译
     * @return 菜单名称翻译
     */
    @Override
    public List<SysMenuTranslateVO> listSysMenuTranslateVO(SysMenuTranslate sysMenuTranslate) {
        LambdaQueryWrapper<SysMenuTranslate> lqw = buildQueryWrapper(sysMenuTranslate);
        List<SysMenuTranslate> sysMenuTranslateList = baseMapper.selectList(lqw);
        return SysMenuTranslateConvert.INSTANCE.convertSysMenuTranslateVOList(sysMenuTranslateList);
    }

    private LambdaQueryWrapper<SysMenuTranslate> buildQueryWrapper(SysMenuTranslate query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysMenuTranslate> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, SysMenuTranslate::getId, query.getId());
        lqw.eq(StringUtils.isNotBlank(query.getZhCn()), SysMenuTranslate::getZhCn, query.getZhCn());
        lqw.eq(StringUtils.isNotBlank(query.getEnUs()), SysMenuTranslate::getEnUs, query.getEnUs());

        return lqw;
    }

    /**
     * 新增菜单名称翻译
     *
     * @param add 菜单名称翻译
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(SysMenuTranslate add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改菜单名称翻译
     *
     * @param update 菜单名称翻译
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "SysMenuTranslate", key = "#update.id")
    public Boolean updateWithCache(SysMenuTranslate update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysMenuTranslate entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除菜单名称翻译信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "SysMenuTranslate", keyGenerator = "deleteKeyGenerator" )
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
