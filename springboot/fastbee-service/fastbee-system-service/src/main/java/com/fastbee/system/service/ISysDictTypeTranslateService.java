package com.fastbee.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.system.domain.SysDictTypeTranslate;
import com.fastbee.system.domain.vo.SysDictTypeTranslateVO;

/**
 * 字典类型翻译Service接口
 *
 * @author gx_ma
 * @date 2025-01-10
 */
public interface ISysDictTypeTranslateService extends IService<SysDictTypeTranslate>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询字典类型翻译列表
     *
     * @param sysDictTypeTranslate 字典类型翻译
     * @return 字典类型翻译分页集合
     */
    Page<SysDictTypeTranslateVO> pageSysDictTypeTranslateVO(SysDictTypeTranslate sysDictTypeTranslate);

    /**
     * 查询字典类型翻译列表
     *
     * @param sysDictTypeTranslate 字典类型翻译
     * @return 字典类型翻译集合
     */
    List<SysDictTypeTranslateVO> listSysDictTypeTranslateVO(SysDictTypeTranslate sysDictTypeTranslate);

    /**
     * 查询字典类型翻译
     *
     * @param id 主键
     * @return 字典类型翻译
     */
    SysDictTypeTranslate selectSysDictTypeTranslateById(Long id);

    /**
     * 查询字典类型翻译
     *
     * @param id 主键
     * @return 字典类型翻译
     */
    SysDictTypeTranslate queryByIdWithCache(Long id);

    /**
     * 新增字典类型翻译
     *
     * @param sysDictTypeTranslate 字典类型翻译
     * @return 是否新增成功
     */
    Boolean insertWithCache(SysDictTypeTranslate sysDictTypeTranslate);

    /**
     * 修改字典类型翻译
     *
     * @param sysDictTypeTranslate 字典类型翻译
     * @return 是否修改成功
     */
    Boolean updateWithCache(SysDictTypeTranslate sysDictTypeTranslate);

    /**
     * 校验并批量删除字典类型翻译信息
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
