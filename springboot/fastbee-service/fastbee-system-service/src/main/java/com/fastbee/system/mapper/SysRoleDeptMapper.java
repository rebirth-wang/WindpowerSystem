package com.fastbee.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fastbee.common.mybatis.mapper.BaseMapperX;
import com.fastbee.system.domain.SysRoleDept;
import com.fastbee.system.domain.vo.SysRoleDeptVO;

/**
 * 角色与部门关联表 数据层
 *
 * @author ruoyi
 */
public interface SysRoleDeptMapper extends BaseMapperX<SysRoleDept>
{

    /**
     * 查询部门使用数量
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int selectCountRoleDeptByDeptId(Long deptId);

    /**
     * 查询部门角色
     * @param deptIdList 部门id
     * @return java.util.List<com.fastbee.system.domain.SysRoleDept>
     */
    List<SysRoleDeptVO> selectRoleDeptByDeptIds(@Param("deptIdList") List<Long> deptIdList);

    /**
     * 查询部门角色
     * @param deptId 部门id
     * @return java.util.List<com.fastbee.common.core.domain.entity.SysRole>
     */
    List<Long> selectByDeptId(Long deptId);
}
