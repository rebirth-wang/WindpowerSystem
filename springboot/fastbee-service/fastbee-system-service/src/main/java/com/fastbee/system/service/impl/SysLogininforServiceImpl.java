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

import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.convert.SysLogininforConvert;
import com.fastbee.system.domain.SysLogininfor;
import com.fastbee.system.domain.vo.SysLogininforVO;
import com.fastbee.system.mapper.SysLogininforMapper;
import com.fastbee.system.service.ISysLogininforService;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysLogininforServiceImpl extends ServiceImpl<SysLogininforMapper,SysLogininfor> implements ISysLogininforService
{

    @Resource
    private SysLogininforMapper logininforMapper;

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininfor logininfor)
    {
        logininfor.setLoginTime(DateUtils.getNowDate());
        logininforMapper.insert(logininfor);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor)
    {
        LambdaQueryWrapper<SysLogininfor> wrapper = buildQueryWrapper(logininfor);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 查询系统访问记录分页列表
     *
     * @param sysLogininfor 系统访问记录
     * @return 系统访问记录
     */
    @Override
    public Page<SysLogininforVO> pageSysLogininforVO(SysLogininfor sysLogininfor) {
        LambdaQueryWrapper<SysLogininfor> lqw = buildQueryWrapper(sysLogininfor);
        Page<SysLogininfor> sysLogininforPage = baseMapper.selectPage(new Page<>(sysLogininfor.getPageNum(), sysLogininfor.getPageSize()), lqw);
        return SysLogininforConvert.INSTANCE.convertSysLogininforVOPage(sysLogininforPage);
    }


    private LambdaQueryWrapper<SysLogininfor> buildQueryWrapper(SysLogininfor query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<SysLogininfor> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getInfoId() != null, SysLogininfor::getInfoId, query.getInfoId());
        lqw.like(StringUtils.isNotBlank(query.getUserName()), SysLogininfor::getUserName, query.getUserName());
        lqw.like(StringUtils.isNotBlank(query.getIpaddr()), SysLogininfor::getIpaddr, query.getIpaddr());
        lqw.eq(StringUtils.isNotBlank(query.getLoginLocation()), SysLogininfor::getLoginLocation, query.getLoginLocation());
        lqw.eq(StringUtils.isNotBlank(query.getBrowser()), SysLogininfor::getBrowser, query.getBrowser());
        lqw.eq(StringUtils.isNotBlank(query.getOs()), SysLogininfor::getOs, query.getOs());
        lqw.eq(query.getStatus() != null, SysLogininfor::getStatus, query.getStatus());
        lqw.eq(StringUtils.isNotBlank(query.getMsg()), SysLogininfor::getMsg, query.getMsg());
        lqw.eq(query.getLoginTime() != null, SysLogininfor::getLoginTime, query.getLoginTime());

        if (!Objects.isNull(params.get("beginTime")) &&
                !Objects.isNull(params.get("endTime"))) {
            lqw.between(SysLogininfor::getLoginTime, params.get("beginTime"), params.get("endTime"));
        }
        return lqw;
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int deleteLogininforByIds(Long[] infoIds)
    {
        return logininforMapper.deleteBatchIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor()
    {
        logininforMapper.cleanLogininfor();
    }

    @Override
    public List<SysLogininfor> getUserCount() {
        return logininforMapper.getUserCount();
    }
}
