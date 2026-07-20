package com.fastbee.iot.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.osgi.framework.ServiceException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.extend.core.card.CardPlatformConfigContent;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.GroupCount;
import com.fastbee.common.extend.enums.CardOperatorEnum;
import com.fastbee.common.extend.enums.CardPlatformEnum;
import com.fastbee.common.extend.enums.CardStatusEnum;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.card.factory.CardPlatformFactory;
import com.fastbee.iot.card.service.ICardPlatformFactoryService;
import com.fastbee.iot.convert.CardConvert;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.domain.CardPlatform;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.mapper.CardMapper;
import com.fastbee.iot.mapper.CardPlatformMapper;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.model.dto.TrafficInfoDTO;
import com.fastbee.iot.model.vo.CardStatisticsVO;
import com.fastbee.iot.model.vo.CardVO;
import com.fastbee.iot.service.ICardService;

/**
 * 物联网卡Service业务层处理
 *
 * @author fastbee
 * @date 2025-11-12
 */
@Slf4j
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper,Card> implements ICardService {

    @Resource
    private CardPlatformFactory cardPlatformFactory;
    @Resource
    private CardPlatformMapper cardPlatformMapper;
    @Resource
    private DeviceMapper deviceMapper;

    /**
     * 查询物联网卡
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param param 物联网卡
     * @return 物联网卡
     */
    @Override
    @DataScope
    public CardVO queryByIdWithCache(Card param){
        LambdaQueryWrapper<Card> queryWrapper = this.buildQueryWrapper(param);
        Card card = this.getOne(queryWrapper);
        if (Objects.isNull(card)) {
            return null;
        }
        CardVO cardVO = CardConvert.INSTANCE.convertCardVO(card);
        CardPlatform cardPlatform = cardPlatformMapper.selectById(cardVO.getCardPlatformId());
        cardVO.setCardPlatformName(cardPlatform.getName());
        if (Objects.isNull(cardVO.getDeviceId())) {
            return cardVO;
        }
        Device device = deviceMapper.selectById(cardVO.getDeviceId());
        cardVO.setDeviceName(device.getDeviceName());
        cardVO.setSerialNumber(device.getSerialNumber());
        cardVO.setProductId(device.getProductId());
        cardVO.setProductName(device.getProductName());
        return cardVO;
    }

    /**
     * 查询物联网卡
     * 查询时更新key缓存，更新和删除时删除缓存，新增时不更新，下一次查询会更新缓存
     * @param id 主键
     * @return 物联网卡
     */
    @Override
    @Cacheable(cacheNames = "Card", key = "#id")
    public Card selectCardById(Long id){
        return this.getById(id);
    }

    /**
     * 查询物联网卡分页列表
     *
     * @param card 物联网卡
     * @return 物联网卡
     */
    @Override
    @DataScope
    public Page<CardVO> pageCardVO(Card card) {
        LambdaQueryWrapper<Card> lqw = buildQueryWrapper(card);
        lqw.orderByDesc(Card::getCreateTime);
        Page<Card> cardPage = baseMapper.selectPage(new Page<>(card.getPageNum(), card.getPageSize()), lqw);
        return CardConvert.INSTANCE.convertCardVOPage(cardPage);
    }

    /**
     * 查询物联网卡列表
     *
     * @param card 物联网卡
     * @return 物联网卡
     */
    @Override
    public List<CardVO> listCardVO(Card card) {
        LambdaQueryWrapper<Card> lqw = buildQueryWrapper(card);
        List<Card> cardList = baseMapper.selectList(lqw);
        return CardConvert.INSTANCE.convertCardVOList(cardList);
    }

    private LambdaQueryWrapper<Card> buildQueryWrapper(Card query) {
        Map<String, Object> params = query.getParams();
        LambdaQueryWrapper<Card> lqw = Wrappers.lambdaQuery();
        lqw.eq(query.getId() != null, Card::getId, query.getId());
                    lqw.eq(StringUtils.isNotBlank(query.getIccid()), Card::getIccid, query.getIccid());
                    lqw.eq(StringUtils.isNotBlank(query.getImsi()), Card::getImsi, query.getImsi());
                    lqw.eq(StringUtils.isNotBlank(query.getMsisdn()), Card::getMsisdn, query.getMsisdn());
                    lqw.eq(query.getOperator() != null, Card::getOperator, query.getOperator());
                    lqw.eq(query.getCardStatus() != null, Card::getCardStatus, query.getCardStatus());
                    lqw.eq(query.getDeviceId() != null, Card::getDeviceId, query.getDeviceId());
                    lqw.eq(query.getCardPlatformId() != null, Card::getCardPlatformId, query.getCardPlatformId());

        if (!Objects.isNull(params.get("beginTime")) &&
        !Objects.isNull(params.get("endTime"))) {
            lqw.between(Card::getCreateTime, params.get("beginTime"), params.get("endTime"));
        }
        // 数据范围过滤
        if (ObjectUtil.isNotEmpty(query.getParams().get(DataScopeAspect.DATA_SCOPE))) {
            lqw.apply((String) query.getParams().get(DataScopeAspect.DATA_SCOPE));
        }
        return lqw;
    }

    /**
     * 新增物联网卡
     *
     * @param add 物联网卡
     * @return 是否新增成功
     */
    @Override
    public Boolean insertWithCache(Card add) {
        SysUser sysUser = getLoginUser().getUser();
        // 归属为机构管理员
        add.setTenantId(sysUser.getDept().getDeptUserId());
        add.setTenantName(sysUser.getDept().getDeptName());
        add.setCreateBy(sysUser.getUserName());
        validEntityBeforeSave(add);
        return this.save(add);
    }

    /**
     * 修改物联网卡
     *
     * @param update 物联网卡
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "Card", key = "#update.id")
    public Boolean updateWithCache(Card update) {
        validEntityBeforeSave(update);
        // 校验绑定设备
        if (null != update.getDeviceId()) {
            LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Card::getDeviceId, update.getDeviceId());
            Card one = this.getOne(queryWrapper);
            if (Objects.nonNull(one) && !update.getId().equals(one.getId())) {
                throw new ServiceException("该设备已经绑定其他物联网卡，请重新选择设备！");
            }
        }
        return this.updateById(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Card entity){
        // 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除物联网卡信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "Card", keyGenerator = "deleteKeyGenerator" )
    public Boolean deleteWithCacheByIds(Long[] ids, Boolean isValid) {
        if(isValid){
            // 做一些业务上的校验,判断是否需要校验
        }
        return this.removeByIds(Arrays.asList(ids));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Card syncCardInfo(CardVO cardVO) {
        if (StringUtils.isEmpty(cardVO.getIccid()) || null == cardVO.getCardPlatformId()) {
            return null;
        }
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getIccid, cardVO.getIccid());
        Card card = baseMapper.selectOne(queryWrapper);
        if (Objects.nonNull(card)) {
            return card;
        }

        try {
            CardPlatform cardPlatform = cardPlatformMapper.selectById(cardVO.getCardPlatformId());
            if (Objects.isNull(cardPlatform)) {
                throw new ServiceException("未找到对应的物联网卡平台");
            }
            CardPlatformConfigContent configModel = JSONObject.parseObject(cardPlatform.getConfigContent(), CardPlatformConfigContent.class);
            CardPlatformEnum platformEnum = CardPlatformEnum.getEnumByPlatform(cardPlatform.getPlatform());
            if (Objects.isNull(platformEnum)) {
                throw new ServiceException("未找到对应的物联网卡平台字典");
            }
            ICardPlatformFactoryService cardPlatformService = cardPlatformFactory.getService(platformEnum);
            Card remoteCard = cardPlatformService.syncCardInfo(cardVO.getIccid(), configModel);
            if (Objects.isNull(remoteCard)) {
                throw new ServiceException("未获取到卡信息");
            }

            // 查询本地是否存在
            Card localCard = lambdaQuery().eq(Card::getIccid, cardVO.getIccid()).one();;

            if (localCard == null) {
                // 新增
                if (StringUtils.isEmpty(remoteCard.getOperator())) {
                    remoteCard.setOperator(Objects.requireNonNull(CardOperatorEnum.getByIccid(cardVO.getIccid())).getOperator());
                }
                remoteCard.setCardPlatformId(cardVO.getCardPlatformId());
                remoteCard.setTenantId(cardVO.getTenantId());
                remoteCard.setTenantName(cardVO.getTenantName());
                remoteCard.setCreateBy(cardVO.getCreateBy());
                if (null != cardVO.getDeviceId()) {
                    remoteCard.setDeviceId(cardVO.getDeviceId());
                }
                save(remoteCard);
                return remoteCard;
            } else {
                // 更新
                remoteCard.setId(localCard.getId());
                updateById(remoteCard);
                log.info("更新物联卡信息: {}", cardVO.getIccid());
                return localCard;
            }

        } catch (Exception e) {
            log.error("同步卡信息失败, ICCID: {}", cardVO.getIccid(), e);
            throw new RuntimeException("同步卡信息失败: " + e.getMessage());
        }
    }

    @Override
    public Card syncTrafficInfo(String iccid, Long cardPlatformId) {
        try {
            CardPlatform cardPlatform = cardPlatformMapper.selectById(cardPlatformId);
            if (Objects.isNull(cardPlatform)) {
                throw new ServiceException("未找到对应的物联网卡平台");
            }
            CardPlatformConfigContent configModel = JSONObject.parseObject(cardPlatform.getConfigContent(), CardPlatformConfigContent.class);
            CardPlatformEnum platformEnum = CardPlatformEnum.getEnumByPlatform(cardPlatform.getPlatform());
            if (Objects.isNull(platformEnum)) {
                throw new ServiceException("未找到对应的物联网卡平台字典");
            }
            ICardPlatformFactoryService cardPlatformService = cardPlatformFactory.getService(platformEnum);
            TrafficInfoDTO trafficInfoDTO = cardPlatformService.syncTrafficInfo(iccid, configModel);
            if (Objects.isNull(trafficInfoDTO)) {
                return null;
            }

            // 更新本地数据库
            Card card = lambdaQuery().eq(Card::getIccid, iccid).one();
            if (card != null) {
                card.setTotalData(trafficInfoDTO.getTotalData());
                card.setDataUsed(trafficInfoDTO.getDataUsed());
                card.setDataRemaining(trafficInfoDTO.getDataRemaining());
                card.setDataPlan(trafficInfoDTO.getDataPlan());
                updateById(card);
            }

            return card;

        } catch (Exception e) {
            log.error("查询流量信息失败, ICCID: {}", iccid, e);
            throw new RuntimeException("查询流量信息失败: " + e.getMessage());
        }
    }


    @Override
    public CardVO syncStatus(String iccid, Long cardPlatformId) throws Exception {
        CardPlatform cardPlatform = cardPlatformMapper.selectById(cardPlatformId);
        if (Objects.isNull(cardPlatform)) {
            throw new ServiceException("未找到对应的物联网卡平台");
        }
        CardPlatformConfigContent configModel = JSONObject.parseObject(cardPlatform.getConfigContent(), CardPlatformConfigContent.class);
        CardPlatformEnum platformEnum = CardPlatformEnum.getEnumByPlatform(cardPlatform.getPlatform());
        if (Objects.isNull(platformEnum)) {
            throw new ServiceException("未找到对应的物联网卡平台字典");
        }
        ICardPlatformFactoryService cardPlatformService = cardPlatformFactory.getService(platformEnum);
        Integer status = cardPlatformService.syncStatus(iccid, configModel);
        // 更新本地数据库
        Card card = lambdaQuery().eq(Card::getIccid, iccid).one();;
        if (card != null && null != status) {
            card.setCardStatus(status);
            updateById(card);
        }
        CardVO cardVO = CardConvert.INSTANCE.convertCardVO(card);
        cardVO.setCardPlatformName(cardPlatform.getName());
        return cardVO;
    }

    @Override
    public List<CardVO> selectCardAndPlatformList(CardVO cardVO) {
        return baseMapper.selectCardAndPlatformList(cardVO);
    }

    @Override
    public boolean activate(String iccid) {
        try {
            ICardPlatformFactoryService cardPlatformService = cardPlatformFactory.getServiceByIccid(iccid);
            boolean result = cardPlatformService.activateCard(iccid);

            if (result) {
                // 更新本地状态
                lambdaUpdate()
                        .eq(Card::getIccid, iccid)
                        .set(Card::getCardStatus, CardStatusEnum.NORMAL.getStatus())
                        .update();
                log.info("卡片激活成功: {}", iccid);
            }

            return result;

        } catch (Exception e) {
            log.error("激活卡片失败, ICCID: {}", iccid, e);
            throw new RuntimeException("激活卡片失败: " + e.getMessage());
        }
    }

    @Override
    public boolean suspend(String iccid) {
        try {
            ICardPlatformFactoryService cardPlatformService = cardPlatformFactory.getServiceByIccid(iccid);
            boolean result = cardPlatformService.suspendCard(iccid);

            if (result) {
                // 更新本地状态
                lambdaUpdate()
                        .eq(Card::getIccid, iccid)
                        .set(Card::getCardStatus, CardStatusEnum.SHUTDOWN.getStatus())
                        .update();
                log.info("卡片停用成功: {}", iccid);
            }

            return result;

        } catch (Exception e) {
            log.error("停用卡片失败, ICCID: {}", iccid, e);
            throw new RuntimeException("停用卡片失败: " + e.getMessage());
        }
    }

    @Override
    public void checkFlowWarning() {
        log.info("开始检查流量告警...");

        List<Card> cards = list();
        for (Card card : cards) {
            if (card.getDataAlertThreshold() != null &&
                    card.getTotalData() != null &&
                    card.getDataUsed() != null) {

                BigDecimal usageRate = card.getDataUsed()
                        .divide(card.getTotalData(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                if (usageRate.compareTo(card.getDataAlertThreshold()) >= 0) {
                    // 触发告警
                    log.warn("流量告警 - ICCID: {}, 使用率: {}%, 阈值: {}%",
                            card.getIccid(), usageRate, card.getDataAlertThreshold());
                    // 这里可以发送邮件、短信等告警通知
                }
            }
        }

        log.info("流量告警检查完成");
    }

    @Override
    public CardStatisticsVO statistics() {
        CardStatisticsVO cardStatisticsVO = new CardStatisticsVO();
        List<GroupCount> statusList = baseMapper.countCardStatusGroup();
        if (CollectionUtils.isNotEmpty(statusList)) {
            Map<Object, Integer> statusCountMap = statusList.stream().collect(Collectors.toMap(GroupCount::getKey, GroupCount::getCount));
            cardStatisticsVO.setNormalCount(null != statusCountMap.get(CardStatusEnum.NORMAL.getStatus()) ? statusCountMap.get(CardStatusEnum.NORMAL.getStatus()) : 0);
            cardStatisticsVO.setPendingActivationCount(null != statusCountMap.get(CardStatusEnum.PENDING_ACTIVATION.getStatus()) ? statusCountMap.get(CardStatusEnum.PENDING_ACTIVATION.getStatus()) : 0);
            cardStatisticsVO.setShutdownCount(null != statusCountMap.get(CardStatusEnum.SHUTDOWN.getStatus()) ? statusCountMap.get(CardStatusEnum.SHUTDOWN.getStatus()) : 0);
            cardStatisticsVO.setAccountTerminationCount(null != statusCountMap.get(CardStatusEnum.ACCOUNT_TERMINATION.getStatus()) ? statusCountMap.get(CardStatusEnum.ACCOUNT_TERMINATION.getStatus()) : 0);
            cardStatisticsVO.setUnknownCount(null != statusCountMap.get(CardStatusEnum.UNKNOWN.getStatus()) ? statusCountMap.get(CardStatusEnum.UNKNOWN.getStatus()) : 0);
            cardStatisticsVO.setTotalCount(cardStatisticsVO.getNormalCount() + cardStatisticsVO.getPendingActivationCount() + cardStatisticsVO.getShutdownCount() + cardStatisticsVO.getAccountTerminationCount() + cardStatisticsVO.getUnknownCount());
        }

        List<GroupCount> operatorList = baseMapper.countCardOperatorGroup();
        if (CollectionUtils.isNotEmpty(operatorList)) {
            Map<Object, Integer> operatorCountMap = operatorList.stream().collect(Collectors.toMap(GroupCount::getKey, GroupCount::getCount));
            List<CardStatisticsVO.OperatorVO> operatorVOList = new ArrayList<>();
            for (CardOperatorEnum operatorEnum : CardOperatorEnum.values()) {
                CardStatisticsVO.OperatorVO operatorVO = new CardStatisticsVO.OperatorVO();
                operatorVO.setOperator(operatorEnum.getOperator());
                operatorVO.setName(operatorEnum.getDescription());
                operatorVO.setCount(null != operatorCountMap.get(operatorEnum.getOperator()) ? operatorCountMap.get(operatorEnum.getOperator()) : 0);
                operatorVOList.add(operatorVO);
            }
            cardStatisticsVO.setOperatorVOList(operatorVOList);
        }

        List<GroupCount> platformList = baseMapper.countCardPlatformGroup();
        if (CollectionUtils.isNotEmpty(platformList)) {
            Map<Object, Integer> platformCountMap = platformList.stream().collect(Collectors.toMap(GroupCount::getKey, GroupCount::getCount));
            List<CardStatisticsVO.PlatformVO> platformVOList = new ArrayList<>();
            for (CardPlatformEnum cardPlatformEnum : CardPlatformEnum.values()) {
                CardStatisticsVO.PlatformVO platformVO = new CardStatisticsVO.PlatformVO();
                platformVO.setPlatform(cardPlatformEnum.getPlatform());
                platformVO.setName(cardPlatformEnum.getDescription());
                platformVO.setCount(null != platformCountMap.get(cardPlatformEnum.getPlatform()) ? platformCountMap.get(cardPlatformEnum.getPlatform()) : 0);
                platformVOList.add(platformVO);
            }
            cardStatisticsVO.setPlatformVOList(platformVOList);
        }

        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Card::getMsisdn, Card::getTotalData, Card::getDataUsed, Card::getDataAlertThreshold);
        queryWrapper.orderByDesc(Card::getDataUsed);
        Page<Card> cardPage = baseMapper.selectPage(new Page<>(1, 10), queryWrapper);
        if (0 == cardPage.getTotal()) {
            cardStatisticsVO.setCardVOList(new ArrayList<>());
            return cardStatisticsVO;
        }
        List<Card> cardList = cardPage.getRecords();
        List<CardVO> cardVOList = CardConvert.INSTANCE.convertCardVOList(cardList);
        for (CardVO cardVO : cardVOList) {
            if (null == cardVO.getTotalData() || null == cardVO.getDataUsed()) {
                continue;
            }
            BigDecimal usageRate = cardVO.getDataUsed()
                    .divide(cardVO.getTotalData(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            cardVO.setTrafficProportion(usageRate);
            if (null == cardVO.getDataAlertThreshold()) {
                cardVO.setAlertFlag(false);
            } else {
                cardVO.setAlertFlag(usageRate.compareTo(cardVO.getDataAlertThreshold()) >= 0);
            }
        }
        cardStatisticsVO.setCardVOList(cardVOList);
        return cardStatisticsVO;
    }

    @Override
    public void deviceReportIccId(Device device, String iccid, Long cardPlatformId) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getIccid, iccid);
        Card card = baseMapper.selectOne(queryWrapper);
        if (Objects.nonNull(card)) {
            card.setDeviceId(device.getDeviceId());
            baseMapper.updateById(card);
            return;
        }
        LambdaUpdateWrapper<Card> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Card::getDeviceId, device.getDeviceId());
        updateWrapper.set(Card::getDeviceId, null);
        baseMapper.update(null, updateWrapper);
        CardVO cardVO = new CardVO();
        cardVO.setDeviceId(device.getDeviceId());
        cardVO.setIccid(iccid);
        cardVO.setCardPlatformId(cardPlatformId);
        cardVO.setTenantId(device.getTenantId());
        cardVO.setTenantName(device.getTenantName());
        cardVO.setCreateBy(device.getCreateBy());
        if (null != cardPlatformId) {
            this.syncCardInfo(cardVO);
        } else {
            List<CardPlatform> cardPlatformList = cardPlatformMapper.selectList();
            for (CardPlatform cardPlatform : cardPlatformList) {
                cardVO.setCardPlatformId(cardPlatform.getId());
                Card syncCardInfo = this.syncCardInfo(cardVO);
                if (Objects.nonNull(syncCardInfo)) {
                    break;
                }
            }
        }
    }

    @Override
    public String selectIccIdByDeviceId(Long deviceId) {
        LambdaQueryWrapper<Card> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Card::getDeviceId, deviceId);
        Card card = baseMapper.selectOne(queryWrapper);
        if (Objects.nonNull(card)) {
            return card.getIccid();
        }
        return null;
    }

    @Override
    public boolean resume(String iccid) {
        try {
            ICardPlatformFactoryService cardPlatformService = cardPlatformFactory.getServiceByIccid(iccid);
            boolean result = cardPlatformService.resumeCard(iccid);

            if (result) {
                // 更新本地状态
                lambdaUpdate()
                        .eq(Card::getIccid, iccid)
                        .set(Card::getCardStatus, CardStatusEnum.NORMAL.getStatus())
                        .update();
                log.info("卡片恢复成功: {}", iccid);
            }

            return result;

        } catch (Exception e) {
            log.error("恢复卡片失败, ICCID: {}", iccid, e);
            throw new RuntimeException("恢复卡片失败: " + e.getMessage());
        }
    }
}
