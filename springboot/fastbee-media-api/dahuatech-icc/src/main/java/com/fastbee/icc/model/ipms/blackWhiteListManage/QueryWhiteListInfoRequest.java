package com.fastbee.icc.model.ipms.blackWhiteListManage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @className QueryWhiteListInfoRequest
 * @Author 355079
 * @Date 2024/12/9
 * @Description 查询白名单信息请求参数
 */
@Data
@Slf4j
public class QueryWhiteListInfoRequest {
    /**
     * 页码
     */
    private Integer pageNum=1;
    /**
     *每页条数
     */
    private Integer pageSize=10;
    /**
     * 车牌号（模糊搜索）
     */
    private String carNumLikeStr;

    /**
     * 获取URL后缀
     *
     * @return URL后缀
     */
    public String getUrlSuffix() {
        StringBuilder urlSuffix = new StringBuilder();
        urlSuffix.append("?");
        urlSuffix.append("pageNum=" + pageNum);
        urlSuffix.append("&pageSize=" + pageSize);
        if (!StringUtils.isEmpty(carNumLikeStr)) {
            try {
                urlSuffix.append("&carNumLikeStr=" + URLEncoder.encode(carNumLikeStr, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(),e);
            }
        }
        return urlSuffix.toString();
    }
}
