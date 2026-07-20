package com.fastbee.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.system.domain.SysDictDataTranslate;

/**
 * 字典数据翻译Service接口
 *
 * @author gx_ma
 * @date 2025-01-10
 */
public interface ISysDictDataTranslateService extends IService<SysDictDataTranslate>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询字典数据翻译列表
     *
     * @param sysDictDataTranslate 字典数据翻译
     * @return 字典数据翻译分页集合
     */
    Page<SysDictDataTranslate> pageSysDictDataTranslateVO(SysDictDataTranslate sysDictDataTranslate);

    /**
     * 查询字典数据翻译列表
     *
     * @param sysDictDataTranslate 字典数据翻译
     * @return 字典数据翻译集合
     */
    List<SysDictDataTranslate> listSysDictDataTranslateVO(SysDictDataTranslate sysDictDataTranslate);

    /**
     * 查询字典数据翻译
     *
     * @param id 主键
     * @return 字典数据翻译
     */
    SysDictDataTranslate selectSysDictDataTranslateById(Long id);

    /**
     * 查询字典数据翻译
     *
     * @param id 主键
     * @return 字典数据翻译
     */
    SysDictDataTranslate queryByIdWithCache(Long id);

    /**
     * 新增字典数据翻译
     *
     * @param sysDictDataTranslate 字典数据翻译
     * @return 是否新增成功
     */
    Boolean insertWithCache(SysDictDataTranslate sysDictDataTranslate);

    /**
     * 修改字典数据翻译
     *
     * @param sysDictDataTranslate 字典数据翻译
     * @return 是否修改成功
     */
    Boolean updateWithCache(SysDictDataTranslate sysDictDataTranslate);

    /**
     * 校验并批量删除字典数据翻译信息
     *
     * @param ids 待删除的主键集合
     * @return 是否删除成功
     */
    boolean deleteWithCacheByIds(Long[] ids);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/

}
