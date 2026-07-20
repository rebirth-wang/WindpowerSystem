package com.fastbee.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.system.domain.SysMenuTranslate;
import com.fastbee.system.domain.vo.SysMenuTranslateVO;

/**
 * 菜单名称翻译Service接口
 *
 * @author fastbee
 * @date 2025-12-26
 */
public interface ISysMenuTranslateService extends IService<SysMenuTranslate>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询菜单名称翻译列表
     *
     * @param sysMenuTranslate 菜单名称翻译
     * @return 菜单名称翻译分页集合
     */
    Page<SysMenuTranslateVO> pageSysMenuTranslateVO(SysMenuTranslate sysMenuTranslate);

    /**
     * 查询菜单名称翻译列表
     *
     * @param sysMenuTranslate 菜单名称翻译
     * @return 菜单名称翻译集合
     */
    List<SysMenuTranslateVO> listSysMenuTranslateVO(SysMenuTranslate sysMenuTranslate);

    /**
     * 查询菜单名称翻译
     *
     * @param id 主键
     * @return 菜单名称翻译
     */
    SysMenuTranslate selectSysMenuTranslateById(Long id);

    /**
     * 查询菜单名称翻译
     *
     * @param id 主键
     * @return 菜单名称翻译
     */
    SysMenuTranslate queryByIdWithCache(Long id);

    /**
     * 新增菜单名称翻译
     *
     * @param sysMenuTranslate 菜单名称翻译
     * @return 是否新增成功
     */
    Boolean insertWithCache(SysMenuTranslate sysMenuTranslate);

    /**
     * 修改菜单名称翻译
     *
     * @param sysMenuTranslate 菜单名称翻译
     * @return 是否修改成功
     */
    Boolean updateWithCache(SysMenuTranslate sysMenuTranslate);

    /**
     * 校验并批量删除菜单名称翻译信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/

}
