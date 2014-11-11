package im.actor.stress.tools;

import com.droidkit.actors.Actor;
import com.droidkit.actors.ActorRef;
import com.droidkit.actors.debug.TraceInterface;
import com.droidkit.actors.mailbox.Envelope;

/**
 * Created by ex3ndr on 11.11.14.
 */
public class ActorTrace implements TraceInterface {
    @Override
    public void onEnvelopeDelivered(Envelope envelope) {

    }

    @Override
    public void onEnvelopeProcessed(Envelope envelope, long duration) {

    }

    @Override
    public void onDrop(ActorRef sender, Object message, Actor actor) {

    }

    @Override
    public void onDeadLetter(ActorRef receiver, Object message) {

    }

    @Override
    public void onActorDie(ActorRef ref, Exception e) {
        e.printStackTrace();
    }
}
