package server;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import message.types.DBSearchRequest;
import message.types.SearchRequest;
import message.types.SearchResponse;
import scala.concurrent.duration.Duration;

import java.io.FileNotFoundException;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class SearchTitleActor extends AbstractActor {
    private int dbResponses;
    private ActorRef db1Searcher, db2Searcher;
    private SearchResponse searchResponse;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive(){
        return receiveBuilder()
                .match(SearchRequest.class, searchRequest -> {
                    log.info("got search req, sending to both DBsearchers");
                    dbResponses=0;
                    db1Searcher = context().actorOf(Props.create(SearchTitleDBActor.class));
                    db2Searcher = context().actorOf(Props.create(SearchTitleDBActor.class));
                    DBSearchRequest dbSearchRequest = new DBSearchRequest(searchRequest, "data/db1");
                    db1Searcher.tell(dbSearchRequest, getSender());
                    dbSearchRequest = new DBSearchRequest(searchRequest, "data/db2");
                    db2Searcher.tell(dbSearchRequest, getSender());
                })
                .match(SearchResponse.class, searchResponse -> {
                    log.info("got search resp");
                    if(dbResponses==0){
                        this.searchResponse = searchResponse;
                        dbResponses++;
                    }
                    else if(dbResponses==1){
                        if(searchResponse.getInDatabase()){
                            this.searchResponse=searchResponse;
                        }
                        //context().actorSelection(this.searchResponse.getClientPath()).tell(this.searchResponse, getSelf());
                        getContext().getParent().tell(this.searchResponse, getSender());
                        log.info("sending search response back");
                        //stop both searchers and self...
                        context().stop(getSelf());
                    }
                })
                .matchAny(o->{
                    log.warning("received unknown message");
                    context().stop(getSelf());
                })
                .build();
    }
    private static SupervisorStrategy strategy
            = new AllForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            match(FileNotFoundException.class, e -> resume())
            .match(NullPointerException.class, e -> restart())
            .matchAny(o -> restart()).
                    build());


    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

}
