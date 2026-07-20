package com.fastbee.system.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.system.domain.SysDictType;
import com.fastbee.system.domain.vo.SysDictTypeVO;

/**
 * 字典表 数据层
 *
 * @author ruoyi
 */
public interface SysDictTypeMapper extends BaseMapperX<SysDictType>
{
    /**
     * 根据条件分页查询字典类型
     *
     * @param sysDictTypeVO 字典类型信息
     * @return 字典类型集合信息
     */
    Page<SysDictTypeVO> selectSysDictTypeVOPage(Page<SysDictTypeVO> page, @Param("sysDictTypeVO") SysDictTypeVO sysDictTypeVO);
//    public List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 根据所有字典类型
     *
     * @param language 语言
     * @return 字典类型集合信息
     */
    public List<SysDictType> selectDictTypeAll(@Param("language") String language);

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @param language 语言
     * @return 字典类型
     */
    public SysDictType selectDictTypeById(@Param("dictId") Long dictId, @Param("language") String language);

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @param language 语言
     * @return 字典类型
     */
    public SysDictType selectDictTypeByType(@Param("dictType") String dictType, @Param("language") String language);

    /**
     * 新增字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    public int insertDictType(SysDictType dictType);

    /**
     * 修改字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    public int updateDictType(SysDictType dictType);

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @param language 语言
     * @return 结果
     */
    public List<SysDictType> checkDictTypeUnique(@Param("dictType") String dictType, @Param("language") String language);
}
