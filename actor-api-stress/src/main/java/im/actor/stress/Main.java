package im.actor.stress;

import com.droidkit.actors.ActorRef;
import im.actor.stress.tools.ActorTrace;
import im.actor.stress.tools.AppLog;

import java.io.IOException;
import java.util.Random;

import static com.droidkit.actors.ActorSystem.system;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

        AppLog.v("Starting stress testing...");

        system().setTraceInterface(new ActorTrace());
        ActorRef actorRef = system().actorOf(StressActor.class, "stress");

        long baseOffset = (long) (new Random().nextFloat() * 1000000L);
        long baseNumber = 80000000L + baseOffset;

        actorRef.send(new StressActor.StartStress(new Scenario(Scenario.Type.MESSAGES, baseNumber, 50, 0)));

        while (true) {
            Thread.sleep(1000);
        }
    }
}
