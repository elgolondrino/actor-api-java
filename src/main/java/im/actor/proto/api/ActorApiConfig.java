package im.actor.proto.api;

import com.droidkit.actors.ActorRef;
import im.actor.proto.mtp.MTProtoEndpoint;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ex3ndr on 07.09.14.
 */
public class ActorApiConfig {
    private final MTProtoEndpoint[] endpoints;
    private final ActorRef stateBroker;
    private final LogInterface logInterface;
    private final ActorApiStorage apiStorage;
    private final boolean isChromeEnabled;
    private final boolean isDebugLog;
    private final boolean isDebugProto;
    private final boolean isDebugTcp;

    public ActorApiConfig(ActorApiStorage apiStorage,
                          MTProtoEndpoint[] endpoints,
                          ActorRef stateBroker,
                          LogInterface logInterface,
                          boolean isDebugLog,
                          boolean isDebugProto,
                          boolean isDebugTcp,
                          boolean isChromeEnabled) {
        this.endpoints = endpoints;
        this.stateBroker = stateBroker;
        this.logInterface = logInterface;
        this.apiStorage = apiStorage;
        this.isDebugLog = isDebugLog;
        this.isDebugProto = isDebugProto;
        this.isDebugTcp = isDebugTcp;
        this.isChromeEnabled = isChromeEnabled;
    }

    public boolean isDebugProto() {
        return isDebugProto;
    }

    public boolean isDebugTcp() {
        return isDebugTcp;
    }

    public boolean isDebugLog() {
        return isDebugLog;
    }

    public boolean isChromeEnabled() {
        return isChromeEnabled;
    }

    public ActorApiStorage getApiStorage() {
        return apiStorage;
    }

    public LogInterface getLogInterface() {
        return logInterface;
    }

    public MTProtoEndpoint[] getEndpoints() {
        return endpoints;
    }

    public ActorRef getStateBroker() {
        return stateBroker;
    }

    public static class Builder {
        private ActorApiStorage storage;
        private ArrayList<MTProtoEndpoint> endpoints = new ArrayList<MTProtoEndpoint>();
        private ActorRef stateBroker;
        private LogInterface logInterface;
        private boolean chromeSupport = false;
        private boolean isDebugLogEnabled = false;
        private boolean isDebugProtoEnabled = false;
        private boolean isDebugTcpEnabled = false;

        public Builder setStateBroker(ActorRef stateBroker) {
            this.stateBroker = stateBroker;
            return this;
        }

        public Builder setStorage(ActorApiStorage storage) {
            this.storage = storage;
            return this;
        }

        public Builder setEndpoints(MTProtoEndpoint[] endpoints) {
            this.endpoints.clear();
            this.endpoints.addAll(Arrays.asList(endpoints));
            return this;
        }

        public Builder addEndpoint(MTProtoEndpoint endpoint) {
            this.endpoints.add(endpoint);
            return this;
        }

        public Builder clearEndpoints() {
            this.endpoints.clear();
            return this;
        }

        public Builder setLog(LogInterface logInterface) {
            this.logInterface = logInterface;
            return this;
        }

        public Builder enableChromeSupport() {
            this.chromeSupport = true;
            return this;
        }

        public Builder enableDebugLog() {
            this.isDebugLogEnabled = true;
            return this;
        }

        public Builder setChromeSupportEnabled(boolean isEnabled) {
            this.chromeSupport = isEnabled;
            return this;
        }

        public Builder setDebugLogEnabled(boolean isEnabled) {
            this.isDebugLogEnabled = isEnabled;
            return this;
        }

        public ActorApiConfig build() {
            if (storage == null) {
                throw new RuntimeException("storage not set");
            }
            if (endpoints.size() == 0) {
                throw new RuntimeException("endpoints not set");
            }
            if (stateBroker == null) {
                throw new RuntimeException("stateBroker not set");
            }
            return new ActorApiConfig(storage, endpoints.toArray(new MTProtoEndpoint[endpoints.size()]), stateBroker,
                    logInterface, isDebugLogEnabled, isDebugProtoEnabled, isDebugTcpEnabled, chromeSupport);
        }
    }
}
