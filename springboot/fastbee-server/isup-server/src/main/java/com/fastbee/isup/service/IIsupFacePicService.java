package com.fastbee.isup.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.isup.domain.IsupFacePic;
import com.fastbee.isup.domain.vo.IsupFacePicVO;

/**
 * 人脸库图片Service接口
 *
 * @author fastbee
 * @date 2026-03-05
 */
public interface IIsupFacePicService extends IService<IsupFacePic>
{
    /** 代码生成区域 可直接覆盖**/

    /**
     * 查询人脸库图片列表
     *
     * @param isupFacePic 人脸库图片
     * @return 人脸库图片分页集合
     */
    Page<IsupFacePicVO> pageIsupFacePicVO(IsupFacePic isupFacePic);

    /**
     * 查询人脸库图片列表
     *
     * @param isupFacePic 人脸库图片
     * @return 人脸库图片集合
     */
    List<IsupFacePicVO> listIsupFacePicVO(IsupFacePic isupFacePic);

    /**
     * 查询人脸库图片
     *
     * @param id 主键
     * @return 人脸库图片
     */
     IsupFacePic selectIsupFacePicById(Long id);

    /**
     * 查询人脸库图片
     *
     * @param id 主键
     * @return 人脸库图片
     */
    IsupFacePic queryByIdWithCache(Long id);

    /**
     * 新增人脸库图片
     *
     * @param isupFacePic 人脸库图片
     * @return 是否新增成功
     */
    Boolean insertWithCache(IsupFacePic isupFacePic);

    /**
     * 修改人脸库图片
     *
     * @param isupFacePic 人脸库图片
     * @return 是否修改成功
     */
    Boolean updateWithCache(IsupFacePic isupFacePic);

    /**
     * 校验并批量删除人脸库图片信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid);
    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
//    Boolean syncFaceDB(AccessControl accessControl);
    Boolean delFacePic(IsupFacePic isupFacePic);

    String ssUploadPic(String filePath);
    /** 自定义代码区域 END**/

}
