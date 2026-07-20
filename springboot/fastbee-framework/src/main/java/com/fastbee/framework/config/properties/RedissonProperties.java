//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.config.properties;

import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "redisson"
)
public class RedissonProperties {
    private String keyPrefix;
    private int threads;
    private int nettyThreads;
    private SingleServerConfig singleServerConfig;
    private ClusterServersConfig clusterServersConfig;

    public String getKeyPrefix() {
        return this.keyPrefix;
    }

    public int getThreads() {
        return this.threads;
    }

    public int getNettyThreads() {
        return this.nettyThreads;
    }

    public SingleServerConfig getSingleServerConfig() {
        return this.singleServerConfig;
    }

    public ClusterServersConfig getClusterServersConfig() {
        return this.clusterServersConfig;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setNettyThreads(int nettyThreads) {
        this.nettyThreads = nettyThreads;
    }

    public void setSingleServerConfig(SingleServerConfig singleServerConfig) {
        this.singleServerConfig = singleServerConfig;
    }

    public void setClusterServersConfig(ClusterServersConfig clusterServersConfig) {
        this.clusterServersConfig = clusterServersConfig;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof RedissonProperties)) {
            return false;
        } else {
            RedissonProperties other = (RedissonProperties)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getThreads() != other.getThreads()) {
                return false;
            } else if (this.getNettyThreads() != other.getNettyThreads()) {
                return false;
            } else {
                Object this$keyPrefix = this.getKeyPrefix();
                Object other$keyPrefix = other.getKeyPrefix();
                if (this$keyPrefix == null) {
                    if (other$keyPrefix != null) {
                        return false;
                    }
                } else if (!this$keyPrefix.equals(other$keyPrefix)) {
                    return false;
                }

                Object this$singleServerConfig = this.getSingleServerConfig();
                Object other$singleServerConfig = other.getSingleServerConfig();
                if (this$singleServerConfig == null) {
                    if (other$singleServerConfig != null) {
                        return false;
                    }
                } else if (!this$singleServerConfig.equals(other$singleServerConfig)) {
                    return false;
                }

                Object this$clusterServersConfig = this.getClusterServersConfig();
                Object other$clusterServersConfig = other.getClusterServersConfig();
                if (this$clusterServersConfig == null) {
                    if (other$clusterServersConfig != null) {
                        return false;
                    }
                } else if (!this$clusterServersConfig.equals(other$clusterServersConfig)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof RedissonProperties;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getThreads();
        result = result * 59 + this.getNettyThreads();
        Object $keyPrefix = this.getKeyPrefix();
        result = result * 59 + ($keyPrefix == null ? 43 : $keyPrefix.hashCode());
        Object $singleServerConfig = this.getSingleServerConfig();
        result = result * 59 + ($singleServerConfig == null ? 43 : $singleServerConfig.hashCode());
        Object $clusterServersConfig = this.getClusterServersConfig();
        result = result * 59 + ($clusterServersConfig == null ? 43 : $clusterServersConfig.hashCode());
        return result;
    }

    public String toString() {
        return "RedissonProperties(keyPrefix=" + this.getKeyPrefix() + ", threads=" + this.getThreads() + ", nettyThreads=" + this.getNettyThreads() + ", singleServerConfig=" + this.getSingleServerConfig() + ", clusterServersConfig=" + this.getClusterServersConfig() + ")";
    }

    public static class SingleServerConfig {
        private String clientName;
        private int connectionMinimumIdleSize;
        private int connectionPoolSize;
        private int idleConnectionTimeout;
        private int timeout;
        private int subscriptionConnectionPoolSize;

        public String getClientName() {
            return this.clientName;
        }

        public int getConnectionMinimumIdleSize() {
            return this.connectionMinimumIdleSize;
        }

        public int getConnectionPoolSize() {
            return this.connectionPoolSize;
        }

        public int getIdleConnectionTimeout() {
            return this.idleConnectionTimeout;
        }

        public int getTimeout() {
            return this.timeout;
        }

        public int getSubscriptionConnectionPoolSize() {
            return this.subscriptionConnectionPoolSize;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public void setConnectionMinimumIdleSize(int connectionMinimumIdleSize) {
            this.connectionMinimumIdleSize = connectionMinimumIdleSize;
        }

        public void setConnectionPoolSize(int connectionPoolSize) {
            this.connectionPoolSize = connectionPoolSize;
        }

        public void setIdleConnectionTimeout(int idleConnectionTimeout) {
            this.idleConnectionTimeout = idleConnectionTimeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public void setSubscriptionConnectionPoolSize(int subscriptionConnectionPoolSize) {
            this.subscriptionConnectionPoolSize = subscriptionConnectionPoolSize;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof SingleServerConfig)) {
                return false;
            } else {
                SingleServerConfig other = (SingleServerConfig)o;
                if (!other.canEqual(this)) {
                    return false;
                } else if (this.getConnectionMinimumIdleSize() != other.getConnectionMinimumIdleSize()) {
                    return false;
                } else if (this.getConnectionPoolSize() != other.getConnectionPoolSize()) {
                    return false;
                } else if (this.getIdleConnectionTimeout() != other.getIdleConnectionTimeout()) {
                    return false;
                } else if (this.getTimeout() != other.getTimeout()) {
                    return false;
                } else if (this.getSubscriptionConnectionPoolSize() != other.getSubscriptionConnectionPoolSize()) {
                    return false;
                } else {
                    Object this$clientName = this.getClientName();
                    Object other$clientName = other.getClientName();
                    if (this$clientName == null) {
                        if (other$clientName != null) {
                            return false;
                        }
                    } else if (!this$clientName.equals(other$clientName)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof SingleServerConfig;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getConnectionMinimumIdleSize();
            result = result * 59 + this.getConnectionPoolSize();
            result = result * 59 + this.getIdleConnectionTimeout();
            result = result * 59 + this.getTimeout();
            result = result * 59 + this.getSubscriptionConnectionPoolSize();
            Object $clientName = this.getClientName();
            result = result * 59 + ($clientName == null ? 43 : $clientName.hashCode());
            return result;
        }

        public String toString() {
            return "RedissonProperties.SingleServerConfig(clientName=" + this.getClientName() + ", connectionMinimumIdleSize=" + this.getConnectionMinimumIdleSize() + ", connectionPoolSize=" + this.getConnectionPoolSize() + ", idleConnectionTimeout=" + this.getIdleConnectionTimeout() + ", timeout=" + this.getTimeout() + ", subscriptionConnectionPoolSize=" + this.getSubscriptionConnectionPoolSize() + ")";
        }
    }

    public static class ClusterServersConfig {
        private String clientName;
        private int masterConnectionMinimumIdleSize;
        private int masterConnectionPoolSize;
        private int slaveConnectionMinimumIdleSize;
        private int slaveConnectionPoolSize;
        private int idleConnectionTimeout;
        private int timeout;
        private int subscriptionConnectionPoolSize;
        private ReadMode readMode;
        private SubscriptionMode subscriptionMode;

        public String getClientName() {
            return this.clientName;
        }

        public int getMasterConnectionMinimumIdleSize() {
            return this.masterConnectionMinimumIdleSize;
        }

        public int getMasterConnectionPoolSize() {
            return this.masterConnectionPoolSize;
        }

        public int getSlaveConnectionMinimumIdleSize() {
            return this.slaveConnectionMinimumIdleSize;
        }

        public int getSlaveConnectionPoolSize() {
            return this.slaveConnectionPoolSize;
        }

        public int getIdleConnectionTimeout() {
            return this.idleConnectionTimeout;
        }

        public int getTimeout() {
            return this.timeout;
        }

        public int getSubscriptionConnectionPoolSize() {
            return this.subscriptionConnectionPoolSize;
        }

        public ReadMode getReadMode() {
            return this.readMode;
        }

        public SubscriptionMode getSubscriptionMode() {
            return this.subscriptionMode;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public void setMasterConnectionMinimumIdleSize(int masterConnectionMinimumIdleSize) {
            this.masterConnectionMinimumIdleSize = masterConnectionMinimumIdleSize;
        }

        public void setMasterConnectionPoolSize(int masterConnectionPoolSize) {
            this.masterConnectionPoolSize = masterConnectionPoolSize;
        }

        public void setSlaveConnectionMinimumIdleSize(int slaveConnectionMinimumIdleSize) {
            this.slaveConnectionMinimumIdleSize = slaveConnectionMinimumIdleSize;
        }

        public void setSlaveConnectionPoolSize(int slaveConnectionPoolSize) {
            this.slaveConnectionPoolSize = slaveConnectionPoolSize;
        }

        public void setIdleConnectionTimeout(int idleConnectionTimeout) {
            this.idleConnectionTimeout = idleConnectionTimeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public void setSubscriptionConnectionPoolSize(int subscriptionConnectionPoolSize) {
            this.subscriptionConnectionPoolSize = subscriptionConnectionPoolSize;
        }

        public void setReadMode(ReadMode readMode) {
            this.readMode = readMode;
        }

        public void setSubscriptionMode(SubscriptionMode subscriptionMode) {
            this.subscriptionMode = subscriptionMode;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof ClusterServersConfig)) {
                return false;
            } else {
                ClusterServersConfig other = (ClusterServersConfig)o;
                if (!other.canEqual(this)) {
                    return false;
                } else if (this.getMasterConnectionMinimumIdleSize() != other.getMasterConnectionMinimumIdleSize()) {
                    return false;
                } else if (this.getMasterConnectionPoolSize() != other.getMasterConnectionPoolSize()) {
                    return false;
                } else if (this.getSlaveConnectionMinimumIdleSize() != other.getSlaveConnectionMinimumIdleSize()) {
                    return false;
                } else if (this.getSlaveConnectionPoolSize() != other.getSlaveConnectionPoolSize()) {
                    return false;
                } else if (this.getIdleConnectionTimeout() != other.getIdleConnectionTimeout()) {
                    return false;
                } else if (this.getTimeout() != other.getTimeout()) {
                    return false;
                } else if (this.getSubscriptionConnectionPoolSize() != other.getSubscriptionConnectionPoolSize()) {
                    return false;
                } else {
                    Object this$clientName = this.getClientName();
                    Object other$clientName = other.getClientName();
                    if (this$clientName == null) {
                        if (other$clientName != null) {
                            return false;
                        }
                    } else if (!this$clientName.equals(other$clientName)) {
                        return false;
                    }

                    Object this$readMode = this.getReadMode();
                    Object other$readMode = other.getReadMode();
                    if (this$readMode == null) {
                        if (other$readMode != null) {
                            return false;
                        }
                    } else if (!this$readMode.equals(other$readMode)) {
                        return false;
                    }

                    Object this$subscriptionMode = this.getSubscriptionMode();
                    Object other$subscriptionMode = other.getSubscriptionMode();
                    if (this$subscriptionMode == null) {
                        if (other$subscriptionMode != null) {
                            return false;
                        }
                    } else if (!this$subscriptionMode.equals(other$subscriptionMode)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof ClusterServersConfig;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getMasterConnectionMinimumIdleSize();
            result = result * 59 + this.getMasterConnectionPoolSize();
            result = result * 59 + this.getSlaveConnectionMinimumIdleSize();
            result = result * 59 + this.getSlaveConnectionPoolSize();
            result = result * 59 + this.getIdleConnectionTimeout();
            result = result * 59 + this.getTimeout();
            result = result * 59 + this.getSubscriptionConnectionPoolSize();
            Object $clientName = this.getClientName();
            result = result * 59 + ($clientName == null ? 43 : $clientName.hashCode());
            Object $readMode = this.getReadMode();
            result = result * 59 + ($readMode == null ? 43 : $readMode.hashCode());
            Object $subscriptionMode = this.getSubscriptionMode();
            result = result * 59 + ($subscriptionMode == null ? 43 : $subscriptionMode.hashCode());
            return result;
        }

        public String toString() {
            return "RedissonProperties.ClusterServersConfig(clientName=" + this.getClientName() + ", masterConnectionMinimumIdleSize=" + this.getMasterConnectionMinimumIdleSize() + ", masterConnectionPoolSize=" + this.getMasterConnectionPoolSize() + ", slaveConnectionMinimumIdleSize=" + this.getSlaveConnectionMinimumIdleSize() + ", slaveConnectionPoolSize=" + this.getSlaveConnectionPoolSize() + ", idleConnectionTimeout=" + this.getIdleConnectionTimeout() + ", timeout=" + this.getTimeout() + ", subscriptionConnectionPoolSize=" + this.getSubscriptionConnectionPoolSize() + ", readMode=" + this.getReadMode() + ", subscriptionMode=" + this.getSubscriptionMode() + ")";
        }
    }
}
