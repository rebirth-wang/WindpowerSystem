package com.fastbee.scada.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.scada.domain.ScadaGallery;
import com.fastbee.scada.vo.ScadaGalleryVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图库管理Service接口
 *
 * @author kerwincui
 * @date 2023-11-10
 */
public interface IScadaGalleryService extends IService<ScadaGallery>
{
    /**
     * 查询图库管理
     *
     * @param scadaGallery 图库管理
     * @return 图库管理
     */
    public ScadaGallery selectScadaGalleryById(ScadaGallery scadaGallery);

    /**
     * 查询组态图库列表
     *
     * @param scadaGallery 组态图库
     * @return 组态图库分页集合
     */
    Page<ScadaGalleryVO> pageScadaGalleryVO(ScadaGallery scadaGallery);

    /**
     * 查询组态图库列表
     *
     * @param scadaGallery 组态图库
     * @return 组态图库集合
     */
    List<ScadaGalleryVO> listScadaGalleryVO(ScadaGallery scadaGallery);

    /**
     * 新增图库管理
     *
     * @param scadaGallery 图库管理
     * @return 结果
     */
    public int insertScadaGallery(ScadaGallery scadaGallery);

    /**
     * 修改图库管理
     *
     * @param scadaGallery 图库管理
     * @return 结果
     */
    public int updateScadaGallery(ScadaGallery scadaGallery);

    /**
     * 批量删除图库管理
     *
     * @param ids 需要删除的图库管理主键集合
     * @return 结果
     */
    public int deleteScadaGalleryByIds(Long[] ids);

    /**
     * 删除图库管理信息
     *
     * @param id 图库管理主键
     * @return 结果
     */
    public int deleteScadaGalleryById(Long id);

    /**
     * 上传文件
     * @param file 文件
     * @param categoryName 分类名称
     * @return
     */
    AjaxResult uploadFile(MultipartFile file, String categoryName);
}
