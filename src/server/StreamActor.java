package server;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import message.types.StreamRequest;
import scala.concurrent.duration.Duration;

import java.io.FileNotFoundException;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class StreamActor extends AbstractActor{
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef searchActor;

    public StreamActor(ActorRef searchActor){
        this.searchActor=searchActor;
    }

    @Override
    public Receive createReceive(){
        return receiveBuilder()
                .match(StreamRequest.class, streamRequest -> {
                    log.info("got stream req, creating streamerACtor for "+streamRequest.getName());
                    context().actorOf(Props.create(StreamerActor.class)).tell(streamRequest, getSender());
                })
                .matchAny(o -> {
                    log.warning("received unknown message");
                })
                .build();
    }
    private static SupervisorStrategy strategy
            = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            match(FileNotFoundException.class, e -> resume())
            .match(NullPointerException.class, e -> restart())
            .matchAny(o -> restart()).
                    build());


    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
