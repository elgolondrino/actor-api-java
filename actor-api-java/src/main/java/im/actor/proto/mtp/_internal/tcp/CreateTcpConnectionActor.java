package im.actor.proto.mtp._internal.tcp;

import com.droidkit.actors.ActorCreator;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.ActorSelection;
import com.droidkit.actors.Props;
import com.droidkit.actors.tasks.TaskActor;

import im.actor.proto.mtp.MTProtoEndpoint;
import im.actor.proto.mtp.MTProtoParams;

import java.util.UUID;

/**
 * Created by ex3ndr on 02.09.14.
 */
public class CreateTcpConnectionActor extends TaskActor<RawTcpConnection> {

    public static Props props(final MTProtoEndpoint endpoint, final MTProtoParams params, final ActorRef reciever) {
        return Props.create(CreateTcpConnectionActor.class, new ActorCreator<CreateTcpConnectionActor>() {
            @Override
            public CreateTcpConnectionActor create() {
                return new CreateTcpConnectionActor(endpoint, params, reciever);
            }
        });
    }

    private ActorRef reciever;
    private MTProtoEndpoint endpoint;
    private MTProtoParams params;

    public CreateTcpConnectionActor(MTProtoEndpoint endpoint, MTProtoParams params, ActorRef reciever) {
        this.endpoint = endpoint;
        this.reciever = reciever;
        this.params = params;
    }

    @Override
    public void startTask() {
        new Thread() {
            @Override
            public void run() {
                try {
                    TcpConnection context = new TcpConnection(endpoint, params, reciever);
                    complete(context);
                } catch (Throwable t) {
                    t.printStackTrace();
                    error(t);
                }
            }
        }.start();
    }
}
