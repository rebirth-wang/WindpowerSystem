package com.fastbee.oss.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.oss.domain.OssDetail;
import com.fastbee.oss.vo.OssDetailVO;

/**
 * 文件记录Service接口
 *
 * @author zhuangpeng.li
 * @date 2024-04-19
 */
public interface IOssDetailService extends IService<OssDetail>
{
    /**
     * 查询文件记录
     *
     * @param id 文件记录主键
     * @return 文件记录
     */
    public OssDetail selectOssDetailById(Integer id);

    /**
     * 查询文件记录列表
     *
     * @param ossDetail 文件记录
     * @return 文件记录集合
     */
    public List<OssDetailVO> selectOssDetailList(OssDetail ossDetail);

    /**
     * 查询文件记录列表
     *
     * @param ossDetail 文件记录
     * @return 文件记录分页集合
     */
    Page<OssDetailVO> pageOssDetailVO(OssDetail ossDetail);

    /**
     * 新增文件记录
     *
     * @param ossDetail 文件记录
     * @return 结果
     */
    public int insertOssDetail(OssDetail ossDetail);

    /**
     * 修改文件记录
     *
     * @param ossDetail 文件记录
     * @return 结果
     */
    public int updateOssDetail(OssDetail ossDetail);

    /**
     * 批量删除文件记录
     *
     * @param ids 需要删除的文件记录主键集合
     * @return 结果
     */
    public int deleteOssDetailByIds(Integer[] ids);

    /**
     * 删除文件记录信息
     *
     * @param id 文件记录主键
     * @return 结果
     */
    public int deleteOssDetailById(Integer id);

    OssDetail upload(MultipartFile file);

    OssDetail upload(File file);

    void download(Integer ossId, HttpServletResponse response) throws IOException;
}
