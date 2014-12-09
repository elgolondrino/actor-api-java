package im.actor.api;

import im.actor.api.mtp.MTProtoEndpoint;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ex3ndr on 07.09.14.
 */
public class ActorApiConfig {
    private final MTProtoEndpoint[] endpoints;
    private final ActorApiCallback apiCallback;
    private final LogInterface logInterface;
    private final ActorApiStorage apiStorage;
    private final boolean isChromeEnabled;
    private final boolean isDebugLog;
    private final boolean isDebugProto;
    private final boolean isDebugTcp;
    private final ActorApiProxy proxy;

    public ActorApiConfig(ActorApiStorage apiStorage,
                          MTProtoEndpoint[] endpoints,
                          ActorApiCallback apiCallback,
                          LogInterface logInterface,
                          boolean isDebugLog,
                          boolean isDebugProto,
                          boolean isDebugTcp,
                          boolean isChromeEnabled,
                          ActorApiProxy proxy) {
        this.endpoints = endpoints;
        this.apiCallback = apiCallback;
        this.logInterface = logInterface;
        this.apiStorage = apiStorage;
        this.isDebugLog = isDebugLog;
        this.isDebugProto = isDebugProto;
        this.isDebugTcp = isDebugTcp;
        this.isChromeEnabled = isChromeEnabled;
        this.proxy = proxy;
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

    public ActorApiCallback getApiCallback() {
        return apiCallback;
    }

    public ActorApiProxy getProxy() {
        return proxy;
    }

    public static class Builder {
        private ActorApiStorage storage;
        private ArrayList<MTProtoEndpoint> endpoints = new ArrayList<MTProtoEndpoint>();
        private ActorApiCallback apiCallback;
        private LogInterface logInterface;
        private boolean chromeSupport = false;
        private boolean isDebugLogEnabled = false;
        private boolean isDebugProtoEnabled = false;
        private boolean isDebugTcpEnabled = false;
        private ActorApiProxy proxy = null;

        public Builder setProxy(ActorApiProxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder setApiCallback(ActorApiCallback apiCallback) {
            this.apiCallback = apiCallback;
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
            if (apiCallback == null) {
                throw new RuntimeException("apiCallback not set");
            }
            return new ActorApiConfig(storage, endpoints.toArray(new MTProtoEndpoint[endpoints.size()]), apiCallback,
                    logInterface, isDebugLogEnabled, isDebugProtoEnabled, isDebugTcpEnabled, chromeSupport, proxy);
        }
    }
}
