package com.fastbee.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.mybatis.mapper.BaseMapperX;

/**
 * 部门管理 数据层
 *
 * @author ruoyi
 */
public interface SysDeptMapper extends BaseMapperX<SysDept>
{
    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> selectDeptList(SysDept dept);

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @param deptCheckStrictly 部门树选择项是否关联显示
     * @return 选中部门列表
     */
    public List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId, @Param("deptCheckStrictly") Integer deptCheckStrictly);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    public SysDept selectDeptById(Long deptId);

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int hasChildByDeptId(Long deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int checkDeptExistUser(Long deptId);

    /**
     * 修改所在部门正常状态
     *
     * @param deptIds 部门ID组
     */
    public void updateDeptStatusNormal(Long[] deptIds);

    /**
     * 修改子元素关系
     *
     * @param depts 子元素
     * @return 结果
     */
    public int updateDeptChildren(@Param("depts") List<SysDept> depts);

    /**
     * 更新部门管理员
     * @param deptId 部门id
     * @param: sysUserId 用户id
     * @return int
     */
    int updateDeptUserId(@Param("deptId") Long deptId, @Param("deptUserId") Long deptUserId);

    /**
     * 查询父子部门
     * @param dept 部门
     * @return java.util.List<com.fastbee.common.core.domain.entity.SysDept>
     */
//    List<SysDept> listDeptAndChild(@Param("dept") SysDept dept, @Param("findInSetStr") String findInSetStr);

    /**
     * 查询部门信息
     * @param deptIdList 部门id集合
     * @return java.util.List<com.fastbee.common.core.domain.entity.SysDept>
     */
    List<SysDept> selectDeptByIds(@Param("deptIdList") List<Long> deptIdList);

    /**
     * 查询机构的所有上级绑定租户
     * @param findInSetStr
     * @return
     */
    List<Long> selectSeniorDeptUser(@Param("findInSetStr") String findInSetStr);

    /**
     * 查询web端注册用户机构角色id
     * @param deptId
     * @return java.lang.Long
     */
    Long selectRoleIdByDeptId(Long deptId);

    List<SysRole> selectRegisterRoleByDeptId(Long deptId);
}
