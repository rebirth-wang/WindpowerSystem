package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.system.domain.vo.SysDeptVO;

/**
 * 机构Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-25
 */
@Mapper
public interface SysDeptConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysDeptConvert INSTANCE = Mappers.getMapper(SysDeptConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysDept
     * @return 机构集合
     */
    SysDeptVO convertSysDeptVO(SysDept sysDept);

    /**
     * VO类转换为实体类集合
     *
     * @param sysDeptVO
     * @return 机构集合
     */
    SysDept convertSysDept(SysDeptVO sysDeptVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysDeptList
     * @return 机构集合
     */
    List<SysDeptVO> convertSysDeptVOList(List<SysDept> sysDeptList);

    /**
     * VO类转换为实体类
     *
     * @param sysDeptVOList
     * @return 机构集合
     */
    List<SysDept> convertSysDeptList(List<SysDeptVO> sysDeptVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysDeptPage
     * @return 机构分页
     */
    Page<SysDeptVO> convertSysDeptVOPage(Page<SysDept> sysDeptPage);

    /**
     * VO类转换为实体类
     *
     * @param sysDeptVOPage
     * @return 机构分页
     */
    Page<SysDept> convertSysDeptPage(Page<SysDeptVO> sysDeptVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
