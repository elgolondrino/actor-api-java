package im.actor.stress;

import com.droidkit.actors.ActorRef;
import im.actor.stress.tools.AppLog;

import java.io.IOException;

import static com.droidkit.actors.ActorSystem.system;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        System.out.println("Press key for stress test...");
        System.in.read();
        AppLog.v("Starting stress testing...");

        ActorRef actorRef = system().actorOf(StressActor.class, "stress");
        actorRef.send(new StressActor.LoginStress(100, 80000001L));

        while (true) {
            Thread.sleep(10000);
        }
    }
}
