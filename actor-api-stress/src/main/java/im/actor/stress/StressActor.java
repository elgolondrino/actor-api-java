package im.actor.stress;

import com.droidkit.actors.Actor;
import im.actor.stress.tools.AppLog;

import java.util.HashSet;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class StressActor extends Actor {

    private HashSet<Long> phones = new HashSet<Long>();
    private HashSet<Long> allPhones = new HashSet<Long>();

    @Override
    public void onReceive(Object message) {
        if (message instanceof LoginStress) {
            LoginStress stress = (LoginStress) message;

            String smsCode = stress.startNumber + "".substring(0, 1);
            smsCode += smsCode;
            smsCode += smsCode;
            for (int i = 0; i < stress.count; i++) {
                long phone = Long.parseLong("7555" + (stress.startNumber + stress.count));
                phones.add(phone);
                allPhones.add(phone);
                system().actorOf(AccountActor.account(phone, smsCode, new long[0], self()));
            }
        } else if (message instanceof OnLoggedIn) {
            long phone = ((OnLoggedIn) message).phoneNumber;
            phones.remove(((OnLoggedIn) message).phoneNumber);
            if (phones.size() == 0) {
                AppLog.v("Authentication completed");
            } else {
                AppLog.v("Authenticated " + phone + " " + phones.size() + "/" + allPhones.size());
            }
        }
    }

    public static class LoginStress {
        private int count;
        private long startNumber;

        public LoginStress(int count, long startNumber) {
            this.count = count;
            this.startNumber = startNumber;
        }
    }

    public static class OnLoggedIn {
        private long phoneNumber;

        public OnLoggedIn(long phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}