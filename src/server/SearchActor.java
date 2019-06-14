package server;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.types.SearchRequest;
import message.types.SearchResponse;

public class SearchActor extends AbstractActor{

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive(){
        return receiveBuilder()
                .match(SearchRequest.class, searchRequest -> {
                    ActorRef child = context().actorOf(Props.create(SearchTitleActor.class), "search"+searchRequest.getName());
                    child.tell(searchRequest, getSender());
                    log.info("got search req for "+searchRequest.getName());
                })
                .match(SearchResponse.class, searchResponse -> {
                    log.info("got search resp, sending it back");
                    getSender().tell(searchResponse, getSelf());

                })
                .matchAny(o->{
                    log.warning("received wrong search req");
                })
                .build();
    }

}
