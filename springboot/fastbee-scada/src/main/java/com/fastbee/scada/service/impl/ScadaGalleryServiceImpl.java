package com.fastbee.scada.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.file.FileUploadUtils;
import com.fastbee.scada.convert.ScadaGalleryConvert;
import com.fastbee.scada.domain.ScadaGallery;
import com.fastbee.scada.mapper.ScadaGalleryMapper;
import com.fastbee.scada.service.IScadaGalleryService;
import com.fastbee.scada.vo.ScadaGalleryVO;

/**
 * 图库管理Service业务层处理
 *
 * @author kerwincui
 * @date 2023-11-10
 */
@Service
public class ScadaGalleryServiceImpl extends ServiceImpl<ScadaGalleryMapper,ScadaGallery> implements IScadaGalleryService
{
    @Resource
    private ScadaGalleryMapper scadaGalleryMapper;

    /**
     * 查询图库管理
     *
     * @param scadaGallery 图库管理
     * @return 图库管理
     */
    @Override
    @DataScope
    public ScadaGallery selectScadaGalleryById(ScadaGallery scadaGallery)
    {
        LambdaQueryWrapper<ScadaGallery> queryWrapper = this.buildQueryWrapper(scadaGallery);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 查询组态图库分页列表
     *
     * @param scadaGallery 组态图库
     * @return 组态图库
     */
    @Override
    @DataScope()
    public Page<ScadaGalleryVO> pageScadaGalleryVO(ScadaGallery scadaGallery) {
        // 多租户版本使用
//        SysUser user = getLoginUser().getUser();
//        if (null != user.getDept()) {
//            scada.setTenantId(user.getDept().getDeptUserId());
//        }
        LambdaQueryWrapper<ScadaGallery> lqw = buildQueryWrapper(scadaGallery);
        lqw.orderByDesc(ScadaGallery::getCreateTime).orderByAsc(ScadaGallery::getId);
        if (null == scadaGallery.getPageNum()) {
            scadaGallery.setPageNum(1);
        }
        if (null == scadaGallery.getPageSize()) {
            scadaGallery.setPageSize(10);
        }
        Page<ScadaGallery> scadaGalleryPage = baseMapper.selectPage(new Page<>(scadaGallery.getPageNum(), scadaGallery.getPageSize()), lqw);
        return ScadaGalleryConvert.INSTANCE.convertScadaGalleryVOPage(scadaGalleryPage);
    }

    /**
     * 查询组态图库列表
     *
     * @param scadaGallery 组态图库
     * @return 组态图库
     */
    @Override
    public List<ScadaGalleryVO> listScadaGalleryVO(ScadaGallery scadaGallery) {
        LambdaQueryWrapper<ScadaGallery> lqw = buildQueryWrapper(scadaGallery);
        List<ScadaGallery> scadaGalleryList = baseMapper.selectList(lqw);
        return ScadaGalleryConvert.INSTANCE.convertScadaGalleryVOList(scadaGalleryList);
    }

    /**
     * 新增图库管理
     *
     * @param scadaGallery 图库管理
     * @return 结果
     */
    @Override
    public int insertScadaGallery(ScadaGallery scadaGallery)
    {
        SysUser user = getLoginUser().getUser();
        scadaGallery.setCreateBy(user.getCreateBy());
        scadaGallery.setCreateTime(DateUtils.getNowDate());
        if (isAdmin(user.getUserId())) {
            scadaGallery.setSysFlag(1);
        } else {
            scadaGallery.setSysFlag(0);
        }
        // 多租户版本使用
//        SysUser user = getLoginUser().getUser();
//        if (null != user.getDept()) {
//            scada.setTenantId(user.getDept().getDeptUserId());
//        } else {
//            scada.setTenantId(user.getUserId());
//            scada.setTenantName(user.getUserName());
//        }
        return baseMapper.insert(scadaGallery);
    }

    /**
     * 修改图库管理
     *
     * @param scadaGallery 图库管理
     * @return 结果
     */
    @Override
    public int updateScadaGallery(ScadaGallery scadaGallery)
    {
        scadaGallery.setUpdateBy(getUsername());
        return baseMapper.updateById(scadaGallery);
    }

    /**
     * 批量删除图库管理
     *
     * @param ids 需要删除的图库管理主键
     * @return 结果
     */
    @Override
    public int deleteScadaGalleryByIds(Long[] ids)
    {
        return baseMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 删除图库管理信息
     *
     * @param id 图库管理主键
     * @return 结果
     */
    @Override
    public int deleteScadaGalleryById(Long id)
    {
        return baseMapper.deleteById(id);
    }

    @Override
    public AjaxResult uploadFile(MultipartFile file, String categoryName) {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error(MessageUtils.message("scada.please.login"));
        }
        SysUser user = loginUser.getUser();
        // 上传文件路径
        String filePath = "";
        // 上传并返回新文件名称
        try{
            filePath = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file);
        }catch (Exception e){
            return AjaxResult.error(500,StringUtils.format(MessageUtils.message("scada.upload.gallery.file.fail"), e.getMessage()));
        }
        String fileName = file.getOriginalFilename();
        ScadaGallery scadaGallery = new ScadaGallery();
        scadaGallery.setFileName(fileName);
        scadaGallery.setCategoryName(categoryName);
        scadaGallery.setResourceUrl(filePath);
        scadaGallery.setSysFlag(isAdmin(user.getUserId()) ? 1 : 0);
        scadaGallery.setCreateBy(user.getUserName());
        scadaGallery.setTenantId(user.getDept().getDeptUserId());
        scadaGallery.setTenantName(user.getDept().getDeptName());
        return baseMapper.insert(scadaGallery) > 0 ? AjaxResult.success(MessageUtils.message("upload.success")) : AjaxResult.error(MessageUtils.message("scada.upload.fail"));
    }

    private LambdaQueryWrapper<ScadaGallery> buildQueryWrapper(ScadaGallery query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<ScadaGallery> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, ScadaGallery::getId, query.getId());
        lqw.like(StringUtils.isNotBlank(query.getFileName()), ScadaGallery::getFileName, query.getFileName());
        lqw.eq(StringUtils.isNotBlank(query.getCategoryName()), ScadaGallery::getCategoryName, query.getCategoryName());
        lqw.eq(StringUtils.isNotBlank(query.getResourceUrl()), ScadaGallery::getResourceUrl, query.getResourceUrl());
        lqw.eq(query.getTenantId() != null, ScadaGallery::getTenantId, query.getTenantId());
        lqw.like(StringUtils.isNotBlank(query.getTenantName()), ScadaGallery::getTenantName, query.getTenantName());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), ScadaGallery::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, ScadaGallery::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), ScadaGallery::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, ScadaGallery::getUpdateTime, query.getUpdateTime());
        lqw.eq(query.getDelFlag() != null, ScadaGallery::getDelFlag, query.getDelFlag());
        lqw.eq(query.getSysFlag() != null, ScadaGallery::getSysFlag, query.getSysFlag());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(ScadaGallery::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }

        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))){
            lqw.and(wq -> wq.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE)).or().eq(ScadaGallery::getSysFlag, 1));
        }

        return lqw;
    }
}
