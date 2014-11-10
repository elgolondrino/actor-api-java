package im.actor.proto.api._internal;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorSelection;
import com.droidkit.actors.Props;
import com.droidkit.actors.tasks.TaskActor;
import im.actor.proto.api.ActorApiConfig;
import im.actor.proto.mtp.AuthIdRetreiver;
import im.actor.proto.mtp.MTProtoEndpoint;
import im.actor.proto.util.ExponentialBackoff;

/**
 * Created by ex3ndr on 30.08.14.
 */
public class KeyBuildActor extends TaskActor<Long> {

    public static ActorSelection key(final ActorApiConfig config, final ExponentialBackoff backoff) {
        return new ActorSelection(Props.create(KeyBuildActor.class, new ActorCreator<KeyBuildActor>() {
            @Override
            public KeyBuildActor create() {
                return new KeyBuildActor(config, backoff);
            }
        }), "key_buider");
    }

    private ExponentialBackoff backoff;
    private ActorApiConfig config;

    public KeyBuildActor(ActorApiConfig config, ExponentialBackoff backoff) {
        this.backoff = backoff;
        this.config = config;
    }

    @Override
    public void startTask() {
        self().send(new RequestKey());
    }

    @Override
    public void onReceive(Object message) {
        super.onReceive(message);
        if (message instanceof RequestKey) {
            request();
        }
    }

    private void request() {
        AuthIdRetreiver.requestAuthId(config, new AuthIdRetreiver.AuthIdCallback() {
            @Override
            public void onSuccess(long authId) {
                backoff.onSuccess();
                complete(authId);
            }

            @Override
            public void onFailure(Exception e) {
                backoff.onFailure();
                self().send(new RequestKey(), backoff.exponentialWait());
            }
        });
    }

    private static class RequestKey {

    }
}
