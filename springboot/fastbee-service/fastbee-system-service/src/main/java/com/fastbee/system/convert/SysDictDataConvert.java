package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.common.extend.core.domin.entity.SysDictData;
import com.fastbee.system.domain.vo.SysDictDataVO;

/**
 * 字典数据Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@Mapper
public interface SysDictDataConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysDictDataConvert INSTANCE = Mappers.getMapper(SysDictDataConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysDictData
     * @return 字典数据集合
     */
    SysDictDataVO convertSysDictDataVO(SysDictData sysDictData);

    /**
     * VO类转换为实体类集合
     *
     * @param sysDictDataVO
     * @return 字典数据集合
     */
    SysDictData convertSysDictData(SysDictDataVO sysDictDataVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysDictDataList
     * @return 字典数据集合
     */
    List<SysDictDataVO> convertSysDictDataVOList(List<SysDictData> sysDictDataList);

    /**
     * VO类转换为实体类
     *
     * @param sysDictDataVOList
     * @return 字典数据集合
     */
    List<SysDictData> convertSysDictDataList(List<SysDictDataVO> sysDictDataVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysDictDataPage
     * @return 字典数据分页
     */
    Page<SysDictDataVO> convertSysDictDataVOPage(Page<SysDictData> sysDictDataPage);

    /**
     * VO类转换为实体类
     *
     * @param sysDictDataVOPage
     * @return 字典数据分页
     */
    Page<SysDictData> convertSysDictDataPage(Page<SysDictDataVO> sysDictDataVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
