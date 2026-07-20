package com.fastbee.system.convert;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fastbee.system.domain.SysPost;
import com.fastbee.system.domain.vo.SysPostVO;

/**
 * 岗位信息Convert转换类
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */
@Mapper
public interface SysPostConvert
{
    /** 代码生成区域 可直接覆盖**/
    SysPostConvert INSTANCE = Mappers.getMapper(SysPostConvert.class);

    /**
     * 实体类转换为VO类
     *
     * @param sysPost
     * @return 岗位信息集合
     */
    SysPostVO convertSysPostVO(SysPost sysPost);

    /**
     * VO类转换为实体类集合
     *
     * @param sysPostVO
     * @return 岗位信息集合
     */
    SysPost convertSysPost(SysPostVO sysPostVO);

    /**
     * 实体类转换为VO类集合
     *
     * @param sysPostList
     * @return 岗位信息集合
     */
    List<SysPostVO> convertSysPostVOList(List<SysPost> sysPostList);

    /**
     * VO类转换为实体类
     *
     * @param sysPostVOList
     * @return 岗位信息集合
     */
    List<SysPost> convertSysPostList(List<SysPostVO> sysPostVOList);

    /**
     * 实体类转换为VO类分页
     *
     * @param sysPostPage
     * @return 岗位信息分页
     */
    Page<SysPostVO> convertSysPostVOPage(Page<SysPost> sysPostPage);

    /**
     * VO类转换为实体类
     *
     * @param sysPostVOPage
     * @return 岗位信息分页
     */
    Page<SysPost> convertSysPostPage(Page<SysPostVO> sysPostVOPage);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
