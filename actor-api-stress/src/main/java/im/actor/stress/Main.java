package im.actor.stress;

import com.droidkit.actors.Actor;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.debug.TraceInterface;
import com.droidkit.actors.mailbox.Envelope;
import im.actor.stress.tools.ActorTrace;
import im.actor.stress.tools.AppLog;

import java.io.IOException;

import static com.droidkit.actors.ActorSystem.system;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

        AppLog.v("Starting stress testing...");

        system().setTraceInterface(new ActorTrace());
        ActorRef actorRef = system().actorOf(StressActor.class, "stress");
        actorRef.send(new StressActor.LoginStress(1000, 80000001L));

        while (true) {
            Thread.sleep(10000);
        }
    }
}
