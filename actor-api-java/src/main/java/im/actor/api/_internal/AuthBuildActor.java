package im.actor.api._internal;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.Props;
import com.droidkit.actors.tasks.TaskActor;
import im.actor.api.ActorApiConfig;
import im.actor.api.mtp.AuthIdRetreiver;
import im.actor.api.util.ExponentialBackoff;

/**
 * Created by ex3ndr on 30.08.14.
 */
public class AuthBuildActor extends TaskActor<Long> {

    public static Props<AuthBuildActor> auth(final ActorApiConfig config, final ExponentialBackoff backoff) {
        return Props.create(AuthBuildActor.class, new ActorCreator<AuthBuildActor>() {
            @Override
            public AuthBuildActor create() {
                return new AuthBuildActor(config, backoff);
            }
        });
    }

    private ExponentialBackoff backoff;
    private ActorApiConfig config;

    public AuthBuildActor(ActorApiConfig config, ExponentialBackoff backoff) {
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
