package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.system.domain.vo.SysUserVO;

/**
 * 用户信息Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@Mapper
public interface SysUserConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysUserConvert INSTANCE = Mappers.getMapper(SysUserConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysUser
     * @return 用户信息集合
     */
    SysUserVO convertSysUserVO(SysUser sysUser);

    /**
     * VO类转换为实体类集合
     *
     * @param sysUserVO
     * @return 用户信息集合
     */
    SysUser convertSysUser(SysUserVO sysUserVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysUserList
     * @return 用户信息集合
     */
    List<SysUserVO> convertSysUserVOList(List<SysUser> sysUserList);

    /**
     * VO类转换为实体类
     *
     * @param sysUserVOList
     * @return 用户信息集合
     */
    List<SysUser> convertSysUserList(List<SysUserVO> sysUserVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysUserPage
     * @return 用户信息分页
     */
    Page<SysUserVO> convertSysUserVOPage(Page<SysUser> sysUserPage);

    /**
     * VO类转换为实体类
     *
     * @param sysUserVOPage
     * @return 用户信息分页
     */
    Page<SysUser> convertSysUserPage(Page<SysUserVO> sysUserVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
