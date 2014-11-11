package im.actor.stress;

import com.droidkit.actors.Actor;
import com.droidkit.actors.ActorRef;
import im.actor.proto.crypto.KeyTools;
import im.actor.stress.tools.AppLog;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class StressActor extends Actor {

    private HashMap<Long, ActorRef> accounts = new HashMap<Long, ActorRef>();
    private HashSet<Long> authenticated = new HashSet<Long>();

    private Scenario scenario;

    @Override
    public void onReceive(Object message) {
        if (message instanceof StartStress) {
            scenario = ((StartStress) message).scenario;

            String smsCode = (scenario.getStartNumber() + "").substring(0, 1);
            smsCode += smsCode;
            smsCode += smsCode;

            long[] phonesList = new long[scenario.getAccountCount()];
            long[] destPhonesList = new long[scenario.getAccountCount()];
            for (int i = 0; i < scenario.getAccountCount(); i++) {
                phonesList[i] = Long.parseLong("7555" + (scenario.getStartNumber() + i));
                destPhonesList[i] = Long.parseLong("7555" + (80000000L + i));
            }
            // destPhonesList = new long[]{75552212121L};

            AppLog.v("Generating keys...");

            KeyPair keyPair = KeyTools.generateNewRsaKey();

            AppLog.v("Keys generated");

            for (int i = 0; i < scenario.getAccountCount(); i++) {
                ActorRef actorRef = system().actorOf(AccountActor.account(phonesList[i], smsCode, destPhonesList, self(), keyPair));
                actorRef.send(new AccountActor.PerformAuth());
                accounts.put(phonesList[i], actorRef);
            }
        } else if (message instanceof OnLoggedIn) {
            long phone = ((OnLoggedIn) message).phoneNumber;
            if (scenario.getType() == Scenario.Type.AUTH) {
                AppLog.v("Authenticated " + phone);
                accounts.get(phone).send(new AccountActor.PerformAuth(), scenario.getDelay());
            } else {
                authenticated.add(phone);
                if (authenticated.size() != scenario.getAccountCount()) {

                    AppLog.v("Authenticated " + phone + " " + authenticated.size() + "/" + scenario.getAccountCount());
                    return;
                }

                AppLog.v("Authentication completed");
                if (scenario.getType() == Scenario.Type.MESSAGES) {
                    for (ActorRef actorRef : accounts.values()) {
                        actorRef.send(new AccountActor.PerformUserLoad());
                    }
                }
            }
        } else if (message instanceof OnUsersLoaded) {
            long phone = ((OnUsersLoaded) message).phoneNumber;
            if (scenario.getType() == Scenario.Type.MESSAGES) {
                accounts.get(phone).send(new AccountActor.PerformSend(), scenario.getDelay());
            }
        } else if (message instanceof OnMessageSent) {
            long phone = ((OnMessageSent) message).phoneNumber;
            if (scenario.getType() == Scenario.Type.MESSAGES) {
                accounts.get(phone).send(new AccountActor.PerformSend(), scenario.getDelay());
            }
        }
    }

    public static class StartStress {
        private Scenario scenario;

        public StartStress(Scenario scenario) {
            this.scenario = scenario;
        }
    }

    public static class OnLoggedIn {
        private long phoneNumber;

        public OnLoggedIn(long phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    public static class OnUsersLoaded {
        private long phoneNumber;

        public OnUsersLoaded(long phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    public static class OnMessageSent {
        private long phoneNumber;

        public OnMessageSent(long phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}