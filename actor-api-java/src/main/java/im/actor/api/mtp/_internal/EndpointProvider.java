package im.actor.api.mtp._internal;

import im.actor.api.mtp.MTProtoEndpoint;

public class EndpointProvider {

    private int roundRobin = 0;
    private MTProtoEndpoint[] endpoints;

    public EndpointProvider(MTProtoEndpoint[] endpoints) {
        this.endpoints = endpoints;
    }

    public synchronized MTProtoEndpoint fetchEndpoint() {
        roundRobin = (roundRobin + 1) % endpoints.length;
        return endpoints[roundRobin];
    }
}
