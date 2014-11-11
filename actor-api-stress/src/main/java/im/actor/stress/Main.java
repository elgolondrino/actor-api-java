package im.actor.stress;

import com.droidkit.actors.ActorRef;

import static com.droidkit.actors.ActorSystem.system;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        ActorRef actorRef = system().actorOf(StressActor.class, "stress");
        actorRef.send(new StressActor.LoginStress(1000, 80000001L));

        while (true) {
            Thread.sleep(10000);
        }
    }
}
