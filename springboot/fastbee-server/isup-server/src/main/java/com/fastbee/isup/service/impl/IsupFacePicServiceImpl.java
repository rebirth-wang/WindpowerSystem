package com.fastbee.isup.service.impl;

import static com.fastbee.isup.sdk.service.IHikISUPStorage.MAX_URL_LEN_SS;
import static com.fastbee.isup.sdk.service.IHikISUPStorage.NET_EHOME_SS_CLIENT_TYPE_KMS;

import java.util.*;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.StringUtils;
import com.fastbee.isup.config.HikIsupProperties;
import com.fastbee.isup.convert.IsupFacePicConvert;
import com.fastbee.isup.domain.IsupFacePic;
import com.fastbee.isup.domain.vo.IsupFacePicVO;
import com.fastbee.isup.mapper.IsupFacePicMapper;
import com.fastbee.isup.model.IsupDevInfo;
import com.fastbee.isup.sdk.isapi.ISAPIService;
import com.fastbee.isup.sdk.service.IHikISUPStorage;
import com.fastbee.isup.sdk.structure.NET_EHOME_SS_CLIENT_PARAM;
import com.fastbee.isup.service.DeviceCacheService;
import com.fastbee.isup.service.IIsupFacePicService;

/**
 * 人脸库图片Service业务层处理
 *
 * @author fastbee
 * @date 2026-03-05
 */
@Slf4j
@Profile("isup")
@Service
public class IsupFacePicServiceImpl extends ServiceImpl<IsupFacePicMapper, IsupFacePic> implements IIsupFacePicService {

    @Resource
    private ISAPIService isapiService;

//    @Resource
//    private ISysWorkerService workerService;

    @Resource
    private IHikISUPStorage hikIsupSs;

    @Resource
    private HikIsupProperties hikIsupProperties;

    @Resource
    private DeviceCacheService deviceCacheService;

    /** 代码生成区域 可直接覆盖**/
    /**
     * 查询人脸库图片
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param id 主键
     * @return 人脸库图片
     */
    @Override
    @Cacheable(cacheNames = "IsupFacePic", key = "#id")
    public IsupFacePic queryByIdWithCache(Long id) {
        return this.getById(id);
    }

    /**
     * 查询人脸库图片
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     *
     * @param id 主键
     * @return 人脸库图片
     */
    @Override
    @Cacheable(cacheNames = "IsupFacePic", key = "#id")
    public IsupFacePic selectIsupFacePicById(Long id) {
        return this.getById(id);
    }

    /**
     * 查询人脸库图片分页列表
     *
     * @param isupFacePic 人脸库图片
     * @return 人脸库图片
     */
    @Override
    public Page<IsupFacePicVO> pageIsupFacePicVO(IsupFacePic isupFacePic) {
        LambdaQueryWrapper<IsupFacePic> lqw = buildQueryWrapper(isupFacePic);
        Page<IsupFacePic> isupFacePicPage = baseMapper.selectPage(new Page<>(isupFacePic.getPageNum(), isupFacePic.getPageSize()), lqw);
        return IsupFacePicConvert.INSTANCE.convertIsupFacePicVOPage(isupFacePicPage);
    }

    /**
     * 查询人脸库图片列表
     *
     * @param isupFacePic 人脸库图片
     * @return 人脸库图片
     */
    @Override
    public List<IsupFacePicVO> listIsupFacePicVO(IsupFacePic isupFacePic) {
        LambdaQueryWrapper<IsupFacePic> lqw = buildQueryWrapper(isupFacePic);
        List<IsupFacePic> isupFacePicList = baseMapper.selectList(lqw);
        return IsupFacePicConvert.INSTANCE.convertIsupFacePicVOList(isupFacePicList);
    }

    private LambdaQueryWrapper<IsupFacePic> buildQueryWrapper(IsupFacePic query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<IsupFacePic> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, IsupFacePic::getId, query.getId());
        lqw.eq(query.getWorkerId() != null, IsupFacePic::getWorkerId, query.getWorkerId());
        lqw.eq(StringUtils.isNotBlank(query.getSerialNumber()), IsupFacePic::getSerialNumber, query.getSerialNumber());
        lqw.eq(StringUtils.isNotBlank(query.getFdid()), IsupFacePic::getFdid, query.getFdid());
        lqw.eq(StringUtils.isNotBlank(query.getPid()), IsupFacePic::getPid, query.getPid());
        lqw.eq(StringUtils.isNotBlank(query.getPicUrl()), IsupFacePic::getPicUrl, query.getPicUrl());
        lqw.eq(query.getExpireTime() != null, IsupFacePic::getExpireTime, query.getExpireTime());
        lqw.eq(query.getDelFlag() != null, IsupFacePic::getDelFlag, query.getDelFlag());
        lqw.eq(StringUtils.isNotBlank(query.getCreateBy()), IsupFacePic::getCreateBy, query.getCreateBy());
        lqw.eq(query.getCreateTime() != null, IsupFacePic::getCreateTime, query.getCreateTime());
        lqw.eq(StringUtils.isNotBlank(query.getUpdateBy()), IsupFacePic::getUpdateBy, query.getUpdateBy());
        lqw.eq(query.getUpdateTime() != null, IsupFacePic::getUpdateTime, query.getUpdateTime());
        lqw.eq(StringUtils.isNotBlank(query.getRemark()), IsupFacePic::getRemark, query.getRemark());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(IsupFacePic::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 新增人脸库图片
     *
     * @param add 人脸库图片
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(IsupFacePic add) {
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改人脸库图片
     *
     * @param update 人脸库图片
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "IsupFacePic", key = "#update.id")
    public Boolean updateWithCache(IsupFacePic update) {
        validEntityBeforeSave(update);
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(IsupFacePic entity) {
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除人脸库图片信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "IsupFacePic", keyGenerator = "deleteKeyGenerator")
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }
    /** 代码生成区域 可直接覆盖END**/

//    /**
//     * 自定义代码区域
//     **/
//    public Boolean syncFaceDB(AccessControl accessControl) {
//        // 获取工人信息
//        SysWorker worker = workerService.lambdaQuery()
//                .eq(SysWorker::getId, accessControl.getWorkerId())
//                .one();
//        if (!Objects.isNull(worker)) {
//            // 上传ss存储获取图片地址
//            String filePath = worker.getWorkerPhoto().replace("/profile", RuoYiConfig.getProfile());
//
//            String picurl = this.ssUploadPic(filePath);
//            if (StringUtils.isBlank(picurl)) {
//                log.error("图片上传失败，filePath: {}", filePath);
//                return false;
//            }
//            // 获取设备loginId
//            Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(accessControl.getSerialNumber());
//            if (!deviceOpt.isPresent()) {
//                log.error("设备不存在");
//                return false;
//            }
//            Integer loginId = deviceOpt.get().getLoginId();
//            String FDID = "-1";
//            FDLibBaseCfgList FDlib = isapiService.getFDlib(loginId);
//            for (FDLibBaseCfg fdLibBaseCfg : FDlib.getFDLibBaseCfg()) {
//                if (fdLibBaseCfg.getName().equals("isupFaceLib")) {
//                    FDID = fdLibBaseCfg.getFDID();
//                }
//            }
//
//            if (FDID.equals("-1")) {
//                FDLibInfoList fDLibInfoList = isapiService.addFDlib(loginId);
//                if (fDLibInfoList != null) {
//                    FDID = fDLibInfoList.getFDLibInfo().get(0).getFDID();
//                }
//                isapiService.putPersonlnfoExtend(loginId);
//            }
//
//            // 同步人脸库
//            MaskInfo maskInfo = isapiService.uploadPicture(loginId, FDID, picurl);
//            if (maskInfo == null || StringUtils.isBlank(maskInfo.getPid())) {
//                log.error("上传人脸图片失败，loginId: {}, FDID: {}, picurl: {}", loginId, FDID, picurl);
//                return false;
//            }
//            IsupFacePic add = new IsupFacePic();
//            add.setWorkerId(accessControl.getWorkerId());
//            add.setSerialNumber(accessControl.getSerialNumber());
//            add.setFdid(FDID);
//            add.setPid(maskInfo.getPid());
//            add.setPicUrl(picurl);
//            add.setExpireTime(accessControl.getAccessEndTime());
//            this.insertWithCache(add);
//        } else {
//            return false;
//        }
//        return true;
//    }

    public Boolean delFacePic(IsupFacePic isupFacePic) {
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(isupFacePic.getSerialNumber());
        if (!deviceOpt.isPresent()) {
            log.error("设备不存在");
            return false;
        }
        Integer loginId = deviceOpt.get().getLoginId();
        isapiService.delFDlibPicture(loginId,isupFacePic.getFdid(), isupFacePic.getPid(),null);
        this.removeById(isupFacePic.getId());
        return true;
    }

    @Override
    public String ssUploadPic(String filePath) {
        int client = -1;
        NET_EHOME_SS_CLIENT_PARAM pClientParam = new NET_EHOME_SS_CLIENT_PARAM();
        pClientParam.enumType = NET_EHOME_SS_CLIENT_TYPE_KMS;
        pClientParam.struAddress.szIP = hikIsupProperties.getExtendIp().getBytes();
        pClientParam.struAddress.wPort = Short.parseShort(hikIsupProperties.getPicServer().getListenPort());
        pClientParam.byHttps = 0;
        pClientParam.write();
        client = hikIsupSs.NET_ESS_CreateClient(pClientParam);
        if (client < 0) {
            int err = hikIsupSs.NET_ESS_GetLastError();
            log.error("创建图片上传/下载客户端出错,错误号：" + err + "  ,client=" + client);
        } else {
            if (!hikIsupSs.NET_ESS_ClientSetTimeout(client, 6000, 6000)) {
                int err = hikIsupSs.NET_ESS_GetLastError();
                log.error("NET_ESS_ClientSetTimeout失败,错误号：" + err);
            }
            boolean bSetParam = hikIsupSs.NET_ESS_ClientSetParam(client, "File-Path", "F:\\uploadPath\\avatar\\2026\\03\\02\\blob_20260302161419A001.png");
            boolean bKMS_UserName = hikIsupSs.NET_ESS_ClientSetParam(client, "KMS-Username", hikIsupProperties.getPicServer().getKmsUserName());
            boolean bKMS_PassWord = hikIsupSs.NET_ESS_ClientSetParam(client, "KMS-Password", hikIsupProperties.getPicServer().getKmsPassword());
            log.info("ssCreateClient: bSetParam:  " + bSetParam +
                    "  bKMS_UserName" + bKMS_UserName +
                    "  bKMS_PassWord" + bKMS_PassWord);
        }
        byte[] szUrl = new byte[MAX_URL_LEN_SS];
        boolean doUpload = hikIsupSs.NET_ESS_ClientDoUpload(client, szUrl, hikIsupSs.MAX_URL_LEN_SS - 1);
        if (!doUpload) {
            int err = hikIsupSs.NET_ESS_GetLastError();
            log.error("NET_ESS_ClientDoUpload失败，错误号：" + err);
        } else {
            String url = "http://" + hikIsupProperties.getExtendIp() + ":" + hikIsupProperties.getPicServer().getListenPort() + new String(szUrl).trim();
            log.info("NET_ESS_ClientDoUpload succeed,Pic Url: " + url);
            return url;
        }
        //释放资源
        hikIsupSs.NET_ESS_DestroyClient(client);
        return "";
    }
    /** 自定义代码区域 END**/
}
