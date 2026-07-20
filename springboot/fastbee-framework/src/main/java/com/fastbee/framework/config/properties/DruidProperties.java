//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.config.properties;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = "spring.datasource.dynamic.druid"
)
public class DruidProperties {
    private Integer initialSize;
    private Integer minIdle;
    private Integer maxActive;
    private Integer maxWait;
    private Long timeBetweenEvictionRunsMillis;
    private Long minEvictableIdleTimeMillis;
    private Long maxEvictableIdleTimeMillis;
    private String validationQuery;
    private Boolean testWhileIdle;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;

    public DruidDataSource dataSource(DruidDataSource datasource) {
        datasource.setInitialSize(this.initialSize);
        datasource.setMaxActive(this.maxActive);
        datasource.setMinIdle(this.minIdle);
        datasource.setMaxWait((long)this.maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
        datasource.setMaxEvictableIdleTimeMillis(this.maxEvictableIdleTimeMillis);
        datasource.setValidationQuery(this.validationQuery);
        datasource.setTestWhileIdle(this.testWhileIdle);
        datasource.setTestOnBorrow(this.testOnBorrow);
        datasource.setTestOnReturn(this.testOnReturn);
        return datasource;
    }

    public Integer getInitialSize() {
        return this.initialSize;
    }

    public Integer getMinIdle() {
        return this.minIdle;
    }

    public Integer getMaxActive() {
        return this.maxActive;
    }

    public Integer getMaxWait() {
        return this.maxWait;
    }

    public Long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }

    public Long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }

    public Long getMaxEvictableIdleTimeMillis() {
        return this.maxEvictableIdleTimeMillis;
    }

    public String getValidationQuery() {
        return this.validationQuery;
    }

    public Boolean getTestWhileIdle() {
        return this.testWhileIdle;
    }

    public Boolean getTestOnBorrow() {
        return this.testOnBorrow;
    }

    public Boolean getTestOnReturn() {
        return this.testOnReturn;
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }

    public void setTimeBetweenEvictionRunsMillis(Long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public void setMaxEvictableIdleTimeMillis(Long maxEvictableIdleTimeMillis) {
        this.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void setTestWhileIdle(Boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof DruidProperties)) {
            return false;
        } else {
            DruidProperties other = (DruidProperties)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$initialSize = this.getInitialSize();
                Object other$initialSize = other.getInitialSize();
                if (this$initialSize == null) {
                    if (other$initialSize != null) {
                        return false;
                    }
                } else if (!this$initialSize.equals(other$initialSize)) {
                    return false;
                }

                Object this$minIdle = this.getMinIdle();
                Object other$minIdle = other.getMinIdle();
                if (this$minIdle == null) {
                    if (other$minIdle != null) {
                        return false;
                    }
                } else if (!this$minIdle.equals(other$minIdle)) {
                    return false;
                }

                Object this$maxActive = this.getMaxActive();
                Object other$maxActive = other.getMaxActive();
                if (this$maxActive == null) {
                    if (other$maxActive != null) {
                        return false;
                    }
                } else if (!this$maxActive.equals(other$maxActive)) {
                    return false;
                }

                Object this$maxWait = this.getMaxWait();
                Object other$maxWait = other.getMaxWait();
                if (this$maxWait == null) {
                    if (other$maxWait != null) {
                        return false;
                    }
                } else if (!this$maxWait.equals(other$maxWait)) {
                    return false;
                }

                Object this$timeBetweenEvictionRunsMillis = this.getTimeBetweenEvictionRunsMillis();
                Object other$timeBetweenEvictionRunsMillis = other.getTimeBetweenEvictionRunsMillis();
                if (this$timeBetweenEvictionRunsMillis == null) {
                    if (other$timeBetweenEvictionRunsMillis != null) {
                        return false;
                    }
                } else if (!this$timeBetweenEvictionRunsMillis.equals(other$timeBetweenEvictionRunsMillis)) {
                    return false;
                }

                Object this$minEvictableIdleTimeMillis = this.getMinEvictableIdleTimeMillis();
                Object other$minEvictableIdleTimeMillis = other.getMinEvictableIdleTimeMillis();
                if (this$minEvictableIdleTimeMillis == null) {
                    if (other$minEvictableIdleTimeMillis != null) {
                        return false;
                    }
                } else if (!this$minEvictableIdleTimeMillis.equals(other$minEvictableIdleTimeMillis)) {
                    return false;
                }

                Object this$maxEvictableIdleTimeMillis = this.getMaxEvictableIdleTimeMillis();
                Object other$maxEvictableIdleTimeMillis = other.getMaxEvictableIdleTimeMillis();
                if (this$maxEvictableIdleTimeMillis == null) {
                    if (other$maxEvictableIdleTimeMillis != null) {
                        return false;
                    }
                } else if (!this$maxEvictableIdleTimeMillis.equals(other$maxEvictableIdleTimeMillis)) {
                    return false;
                }

                Object this$testWhileIdle = this.getTestWhileIdle();
                Object other$testWhileIdle = other.getTestWhileIdle();
                if (this$testWhileIdle == null) {
                    if (other$testWhileIdle != null) {
                        return false;
                    }
                } else if (!this$testWhileIdle.equals(other$testWhileIdle)) {
                    return false;
                }

                Object this$testOnBorrow = this.getTestOnBorrow();
                Object other$testOnBorrow = other.getTestOnBorrow();
                if (this$testOnBorrow == null) {
                    if (other$testOnBorrow != null) {
                        return false;
                    }
                } else if (!this$testOnBorrow.equals(other$testOnBorrow)) {
                    return false;
                }

                Object this$testOnReturn = this.getTestOnReturn();
                Object other$testOnReturn = other.getTestOnReturn();
                if (this$testOnReturn == null) {
                    if (other$testOnReturn != null) {
                        return false;
                    }
                } else if (!this$testOnReturn.equals(other$testOnReturn)) {
                    return false;
                }

                Object this$validationQuery = this.getValidationQuery();
                Object other$validationQuery = other.getValidationQuery();
                if (this$validationQuery == null) {
                    if (other$validationQuery != null) {
                        return false;
                    }
                } else if (!this$validationQuery.equals(other$validationQuery)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof DruidProperties;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $initialSize = this.getInitialSize();
        result = result * 59 + ($initialSize == null ? 43 : $initialSize.hashCode());
        Object $minIdle = this.getMinIdle();
        result = result * 59 + ($minIdle == null ? 43 : $minIdle.hashCode());
        Object $maxActive = this.getMaxActive();
        result = result * 59 + ($maxActive == null ? 43 : $maxActive.hashCode());
        Object $maxWait = this.getMaxWait();
        result = result * 59 + ($maxWait == null ? 43 : $maxWait.hashCode());
        Object $timeBetweenEvictionRunsMillis = this.getTimeBetweenEvictionRunsMillis();
        result = result * 59 + ($timeBetweenEvictionRunsMillis == null ? 43 : $timeBetweenEvictionRunsMillis.hashCode());
        Object $minEvictableIdleTimeMillis = this.getMinEvictableIdleTimeMillis();
        result = result * 59 + ($minEvictableIdleTimeMillis == null ? 43 : $minEvictableIdleTimeMillis.hashCode());
        Object $maxEvictableIdleTimeMillis = this.getMaxEvictableIdleTimeMillis();
        result = result * 59 + ($maxEvictableIdleTimeMillis == null ? 43 : $maxEvictableIdleTimeMillis.hashCode());
        Object $testWhileIdle = this.getTestWhileIdle();
        result = result * 59 + ($testWhileIdle == null ? 43 : $testWhileIdle.hashCode());
        Object $testOnBorrow = this.getTestOnBorrow();
        result = result * 59 + ($testOnBorrow == null ? 43 : $testOnBorrow.hashCode());
        Object $testOnReturn = this.getTestOnReturn();
        result = result * 59 + ($testOnReturn == null ? 43 : $testOnReturn.hashCode());
        Object $validationQuery = this.getValidationQuery();
        result = result * 59 + ($validationQuery == null ? 43 : $validationQuery.hashCode());
        return result;
    }

    public String toString() {
        return "DruidProperties(initialSize=" + this.getInitialSize() + ", minIdle=" + this.getMinIdle() + ", maxActive=" + this.getMaxActive() + ", maxWait=" + this.getMaxWait() + ", timeBetweenEvictionRunsMillis=" + this.getTimeBetweenEvictionRunsMillis() + ", minEvictableIdleTimeMillis=" + this.getMinEvictableIdleTimeMillis() + ", maxEvictableIdleTimeMillis=" + this.getMaxEvictableIdleTimeMillis() + ", validationQuery=" + this.getValidationQuery() + ", testWhileIdle=" + this.getTestWhileIdle() + ", testOnBorrow=" + this.getTestOnBorrow() + ", testOnReturn=" + this.getTestOnReturn() + ")";
    }
}
