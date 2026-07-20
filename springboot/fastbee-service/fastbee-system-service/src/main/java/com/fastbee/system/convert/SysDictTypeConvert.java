package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.system.domain.SysDictType;
import com.fastbee.system.domain.vo.SysDictTypeVO;

/**
 * 字典类型Convert转换类
 *
 * @author gx_ma
 * @date 2024-12-26
 */
@Mapper
public interface SysDictTypeConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysDictTypeConvert INSTANCE = Mappers.getMapper(SysDictTypeConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysDictType
     * @return 字典类型集合
     */
    SysDictTypeVO convertSysDictTypeVO(SysDictType sysDictType);

    /**
     * VO类转换为实体类集合
     *
     * @param sysDictTypeVO
     * @return 字典类型集合
     */
    SysDictType convertSysDictType(SysDictTypeVO sysDictTypeVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysDictTypeList
     * @return 字典类型集合
     */
    List<SysDictTypeVO> convertSysDictTypeVOList(List<SysDictType> sysDictTypeList);

    /**
     * VO类转换为实体类
     *
     * @param sysDictTypeVOList
     * @return 字典类型集合
     */
    List<SysDictType> convertSysDictTypeList(List<SysDictTypeVO> sysDictTypeVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysDictTypePage
     * @return 字典类型分页
     */
    Page<SysDictTypeVO> convertSysDictTypeVOPage(Page<SysDictType> sysDictTypePage);

    /**
     * VO类转换为实体类
     *
     * @param sysDictTypeVOPage
     * @return 字典类型分页
     */
    Page<SysDictType> convertSysDictTypePage(Page<SysDictTypeVO> sysDictTypeVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
