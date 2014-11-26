package im.actor.stress;

import com.droidkit.actors.ActorRef;
import im.actor.stress.tools.ActorTrace;
import im.actor.stress.tools.AppLog;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

import static com.droidkit.actors.ActorSystem.system;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        byte[] data = FileUtils.readFileToByteArray(new File("/Users/ex3ndr/Downloads/Untitled"));
        im.actor.api.scheme.rpc.ResponseGetDifference.fromBytes(data);
//        if (args.length < 3) {
//            System.out.println("USAGE [auth|msg] <threads> <delay>");
//            return;
//        }
//
//        Scenario.Type type;
//        if (args[0].equals("auth")) {
//            type = Scenario.Type.AUTH;
//        } else if (args[0].equals("msg")) {
//            type = Scenario.Type.MESSAGES;
//        } else {
//            System.out.println("Incorrect scenario");
//            return;
//        }
//
//        int threads = Integer.parseInt(args[1]);
//        int delay = Integer.parseInt(args[1]);
//
//        AppLog.v("Starting stress testing...");
//
//        system().setTraceInterface(new ActorTrace());
//
//
//        ActorRef actorRef = system().actorOf(StressActor.class, "stress");
//
//        long baseNumber = 80000000L + (long) (new Random().nextFloat() * 1000000L);
//
//        actorRef.send(new StressActor.StartStress(new Scenario(type, baseNumber, threads, delay)));
//
//        while (true) {
//            Thread.sleep(1000);
//        }
    }
}