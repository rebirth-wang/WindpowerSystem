package com.fastbee.oss.controller;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.oss.domain.OssDetail;
import com.fastbee.oss.service.IOssDetailService;
import com.fastbee.oss.vo.OssDetailVO;

/**
 * 文件记录Controller
 *
 * @author zhuangpeng.li
 * @date 2024-04-22
 */
@RestController
@RequestMapping("/oss/detail")
public class OssDetailController extends BaseController {
    @Autowired
    private IOssDetailService ossDetailService;

    /**
     * 查询文件记录列表
     */
    @PreAuthorize("@ss.hasPermi('oss:detail:list')")
    @GetMapping("/list")
    public TableDataInfo list(OssDetail ossDetail) {
        Page<OssDetailVO> voPage = ossDetailService.pageOssDetailVO(ossDetail);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出文件记录列表
     */
    @PreAuthorize("@ss.hasPermi('oss:detail:export')")
    @Log(title = "文件记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OssDetail ossDetail) {
        Page<OssDetailVO> voPage = ossDetailService.pageOssDetailVO(ossDetail);
        ExcelUtil<OssDetailVO> util = new ExcelUtil<OssDetailVO>(OssDetailVO.class);
        util.exportExcel(response, voPage.getRecords(), "文件记录数据");
    }

    /**
     * 获取文件记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('oss:detail:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id) {
        return success(ossDetailService.selectOssDetailById(id));
    }

    /**
     * 新增文件记录
     */
    @PreAuthorize("@ss.hasPermi('oss:detail:add')")
    @Log(title = "文件记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OssDetail ossDetail) {
        return toAjax(ossDetailService.insertOssDetail(ossDetail));
    }

    /**
     * 修改文件记录
     */
    @PreAuthorize("@ss.hasPermi('oss:detail:edit')")
    @Log(title = "文件记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OssDetail ossDetail) {
        return toAjax(ossDetailService.updateOssDetail(ossDetail));
    }

    /**
     * 删除文件记录
     */
    @PreAuthorize("@ss.hasPermi('oss:detail:remove')")
    @Log(title = "文件记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids) {
        return toAjax(ossDetailService.deleteOssDetailByIds(ids));
    }


    /**
     * 上传OSS对象存储
     *
     * @param file 文件
     */
    @PreAuthorize("@ss.hasPermi('oss:detail:upload')")
    @Log(title = "OSS对象存储", businessType = BusinessType.INSERT)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult upload(@RequestPart("file") MultipartFile file) {
        if (ObjectUtil.isNull(file)) {
            return error(MessageUtils.message("ossDetail.fail.file.not.empty"));
        }
        return toAjax(ossDetailService.upload(file));
    }

    /**
     * 下载OSS对象
     *
     * @param ossId OSS对象ID
     */
    @PreAuthorize("@ss.hasPermi('oss:detail:download')")
    @GetMapping("/download/{ossId}")
    public void download(@PathVariable Integer ossId, HttpServletResponse response) throws IOException {
        ossDetailService.download(ossId, response);
    }

}
