package com.fastbee.sip.handler.req.message.response.cmdType;

import java.text.ParseException;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.fastbee.media.model.RecordItem;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.service.IMediaCacheService;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.handler.req.ReqAbstractHandler;
import com.fastbee.sip.handler.req.message.IMessageHandler;
import com.fastbee.sip.handler.req.message.response.ResponseMessageHandler;
import com.fastbee.sip.server.RecordCacheManager;
import com.fastbee.sip.util.SipUtil;
import com.fastbee.sip.util.XmlUtil;

@Slf4j
@Component
public class RecordInfoHandler extends ReqAbstractHandler implements InitializingBean, IMessageHandler {

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    @Autowired
    private IMediaCacheService mediaCacheService;

    @Autowired
    private RecordCacheManager recordCacheManager;

    @Override
    public void handlerCmdType(RequestEvent evt, SipDevice device, Element element) {
        ReentrantLock lock = null;
        boolean locked = false;
        try {
            // 回复200 OK
            responseAck(evt);
            Element rootElement = getRootElement(evt);
            if (rootElement == null) {
                log.error("根元素为空，无法处理请求");
                return;
            }
            String deviceId = XmlUtil.getText(rootElement, "DeviceID");
            String sn = XmlUtil.getText(rootElement, "SN");
            String sumNumStr = XmlUtil.getText(rootElement, "SumNum");
            if (deviceId == null || deviceId.isEmpty() || sn == null || sn.isEmpty()) {
                log.warn("RecordInfo response missing DeviceID or SN, deviceId:{}, sn:{}", deviceId, sn);
                return;
            }
            if (sumNumStr == null || sumNumStr.isEmpty()) {
                log.warn("RecordInfo response missing SumNum, deviceId:{}, sn:{}", deviceId, sn);
                return;
            }

            int sumNum;
            try {
                sumNum = Integer.parseInt(sumNumStr);
            } catch (NumberFormatException e) {
                log.warn("RecordInfo response invalid SumNum, deviceId:{}, sn:{}, sumNum:{}", deviceId, sn, sumNumStr);
                return;
            }
            String recordkey = String.format("%s:%s", deviceId, sn);
            lock = recordCacheManager.getlock(recordkey);
            if (lock == null) {
                log.warn("RecordInfo response has no pending query, recordkey:{}", recordkey);
                return;
            }
            try {
                // 尝试获取锁，如果锁不可用则等待
                lock.lockInterruptibly();
                locked = true;
            } catch (InterruptedException e) {
                // 处理中断异常
                Thread.currentThread().interrupt(); // 恢复中断状态
                log.warn("lock 恢复中断状态: {}", e.getMessage());
                return;
            }
            int recordnum = 0;
            RecordList recordList = recordCacheManager.get(recordkey);
            recordList.setDeviceID(deviceId);
            Element recordListElement = rootElement.element("RecordList");
            if (recordListElement == null){
                log.info("no record data");
            } else {
                Iterator<Element> recordListIterator = recordListElement.elementIterator();
                if (recordListIterator != null) {
                    while (recordListIterator.hasNext()) {
                        Element itemRecord = recordListIterator.next();
                        Element recordElement = itemRecord.element("DeviceID");
                        if (recordElement == null) {
                            continue;
                        }
                        RecordItem item = new RecordItem();
                        item.setStart(SipUtil.ISO8601Totimestamp(XmlUtil.getText(itemRecord, "StartTime")));
                        item.setEnd(SipUtil.ISO8601Totimestamp(XmlUtil.getText(itemRecord, "EndTime")));
                        recordList.addItem(item);
                        recordnum++;
                    }
                }
            }
            int currentNum = recordList.getSumNum() == null ? 0 : recordList.getSumNum();
            int receivedNum = currentNum + recordnum;
            recordList.setSumNum(receivedNum);
            log.info("sn:{}, getSumNum:{}, recordnum:{}, sumNum:{}", sn, currentNum, recordnum, sumNum);
            if (receivedNum >= sumNum) {
                recordList.mergeItems();
                log.info("mergeItems recordList:{}", recordList);
                recordCacheManager.remove(recordkey);
                mediaCacheService.setRecordList(recordkey, recordList);
            } else {
                recordCacheManager.put(recordkey, recordList);
            }
        } catch (DocumentException | SipException | InvalidArgumentException | ParseException | SAXException e) {
            log.error("处理请求时发生异常: {}", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("RecordInfo response process failed: {}", e.getMessage(), e);
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String cmdType = "RecordInfo";
        responseMessageHandler.addHandler(cmdType, this);
    }
}
