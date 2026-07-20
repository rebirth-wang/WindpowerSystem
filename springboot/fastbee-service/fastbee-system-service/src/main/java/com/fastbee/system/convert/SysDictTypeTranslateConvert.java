package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.system.domain.SysDictTypeTranslate;
import com.fastbee.system.domain.vo.SysDictTypeTranslateVO;

/**
 * 字典类型翻译Convert转换类
 *
 * @author gx_ma
 * @date 2025-01-10
 */
@Mapper
public interface SysDictTypeTranslateConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysDictTypeTranslateConvert INSTANCE = Mappers.getMapper(SysDictTypeTranslateConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysDictTypeTranslate
     * @return 字典类型翻译集合
     */
    SysDictTypeTranslateVO convertSysDictTypeTranslateVO(SysDictTypeTranslate sysDictTypeTranslate);

    /**
     * VO类转换为实体类集合
     *
     * @param sysDictTypeTranslateVO
     * @return 字典类型翻译集合
     */
    SysDictTypeTranslate convertSysDictTypeTranslate(SysDictTypeTranslateVO sysDictTypeTranslateVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysDictTypeTranslateList
     * @return 字典类型翻译集合
     */
    List<SysDictTypeTranslateVO> convertSysDictTypeTranslateVOList(List<SysDictTypeTranslate> sysDictTypeTranslateList);

    /**
     * VO类转换为实体类
     *
     * @param sysDictTypeTranslateVOList
     * @return 字典类型翻译集合
     */
    List<SysDictTypeTranslate> convertSysDictTypeTranslateList(List<SysDictTypeTranslateVO> sysDictTypeTranslateVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysDictTypeTranslatePage
     * @return 字典类型翻译分页
     */
    Page<SysDictTypeTranslateVO> convertSysDictTypeTranslateVOPage(Page<SysDictTypeTranslate> sysDictTypeTranslatePage);

    /**
     * VO类转换为实体类
     *
     * @param sysDictTypeTranslateVOPage
     * @return 字典类型翻译分页
     */
    Page<SysDictTypeTranslate> convertSysDictTypeTranslatePage(Page<SysDictTypeTranslateVO> sysDictTypeTranslateVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
