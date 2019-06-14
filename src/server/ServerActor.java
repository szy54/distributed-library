package server;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import message.types.*;
import scala.concurrent.duration.Duration;

import java.io.FileNotFoundException;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class ServerActor extends AbstractActor{
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public void preStart() throws Exception{
        ActorRef searchActor = context().actorOf(Props.create(SearchActor.class), "searchActor");
        ActorRef orderActor = context().actorOf(Props.create(OrderActor.class, searchActor), "orderActor");
        ActorRef streamActor = context().actorOf(Props.create(StreamActor.class, searchActor), "streamActor");
    }

    @Override
    public Receive createReceive(){
        return receiveBuilder()
                .match(OrderRequest.class, orderRequest -> {
                    context().child("orderActor").get().tell(orderRequest, getSender());
                    log.info("sent order req to an actor");
                })
                .match(StreamRequest.class, streamRequest -> {
                    context().child("streamActor").get().tell(streamRequest, getSender());
                    log.info("sent stream req to an actor");
                })
                .match(SearchRequest.class, searchRequest -> {
                    context().child("searchActor").get().tell(searchRequest, getSender());
                    log.info("sent search req to an actor, client path: "+searchRequest.getClientPath());
                })
                .matchAny(o->log.warning("Server App received unknown message"))
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
