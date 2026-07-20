package com.fastbee.system.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import com.fastbee.common.constant.UserConstants;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.convert.SysPostConvert;
import com.fastbee.system.domain.SysPost;
import com.fastbee.system.domain.vo.SysPostVO;
import com.fastbee.system.mapper.SysPostMapper;
import com.fastbee.system.mapper.SysUserPostMapper;
import com.fastbee.system.service.ISysPostService;

/**
 * 岗位信息 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper,SysPost> implements ISysPostService
{
    @Resource
    private SysPostMapper postMapper;

    @Resource
    private SysUserPostMapper userPostMapper;

    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPostVO> selectPostList(SysPost post)
    {
        LambdaQueryWrapper<SysPost> lqw = buildQueryWrapper(post);
        List<SysPost> postList = postMapper.selectList(lqw);
        return SysPostConvert.INSTANCE.convertSysPostVOList(postList);
    }

    /**
     * 查询岗位信息分页列表
     *
     * @param sysPost 岗位信息
     * @return 岗位信息
     */
    @Override
    public Page<SysPostVO> pageSysPostVO(SysPost sysPost) {
        LambdaQueryWrapper<SysPost> lqw = buildQueryWrapper(sysPost);
        Page<SysPost> sysPostPage = baseMapper.selectPage(new Page<>(sysPost.getPageNum(), sysPost.getPageSize()), lqw);
        return SysPostConvert.INSTANCE.convertSysPostVOPage(sysPostPage);
    }

    private LambdaQueryWrapper<SysPost> buildQueryWrapper(SysPost query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysPost> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getPostId() != null, SysPost::getPostId, query.getPostId());
        lqw.like(StringUtils.isNotBlank(query.getPostCode()), SysPost::getPostCode, query.getPostCode());
        lqw.like(StringUtils.isNotBlank(query.getPostName()), SysPost::getPostName, query.getPostName());
        lqw.eq(query.getPostSort() != null, SysPost::getPostSort, query.getPostSort());
        lqw.eq(query.getStatus() != null, SysPost::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), SysPost::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, SysPost::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), SysPost::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, SysPost::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), SysPost::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysPost::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll()
    {
        return postMapper.selectList();
    }

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(Long postId)
    {
        return postMapper.selectById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Long> selectPostListByUserId(Long userId)
    {
        return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostNameUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        LambdaQueryWrapper<SysPost> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysPost::getPostName, post.getPostName());
        SysPost info = postMapper.selectOne(lqw);
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public String checkPostCodeUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        LambdaQueryWrapper<SysPost> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysPost::getPostCode, post.getPostCode());
        SysPost info = postMapper.selectOne(lqw);
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int countUserPostById(Long postId)
    {
        return userPostMapper.countUserPostById(postId);
    }

    /**
     * 删除岗位信息
     *
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int deletePostById(Long postId)
    {
        return postMapper.deleteById(postId);
    }

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    @Override
    public int deletePostByIds(Long[] postIds)
    {
        for (Long postId : postIds)
        {
            SysPost post = selectPostById(postId);
            if (countUserPostById(postId) > 0)
            {
                throw new ServiceException(String.format("%1$s已分配,不能删除", post.getPostName()));
            }
        }
        return postMapper.deleteBatchIds(Arrays.asList(postIds));
    }

    /**
     * 新增保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPost post)
    {
        post.setCreateTime(DateUtils.getNowDate());
        return postMapper.insert(post);
    }

    /**
     * 修改保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPost post)
    {
        post.setUpdateTime(DateUtils.getNowDate());
        return postMapper.updateById(post);
    }
}
