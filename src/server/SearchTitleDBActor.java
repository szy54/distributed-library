package server;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.types.DBSearchRequest;
import message.types.SearchResponse;

import java.io.File;
import java.util.Scanner;

public class SearchTitleDBActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive(){
        return receiveBuilder()
                .match(DBSearchRequest.class, dbSearchRequest -> {
                    Scanner scanner = new Scanner(new File(dbSearchRequest.getDbPath()));
                    String line = null;
                    Boolean foundBook=false;
                    while(scanner.hasNext()){
                        line = scanner.nextLine();
                        if(line.startsWith(dbSearchRequest.getSearchRequest().getName())){
                            log.info("found "+line);
                            foundBook=true;
                            break;
                        }
                    }
                    SearchResponse searchResponse;
                    if(foundBook && line!=null){
                        String [] lineSplit = line.split(" ");
                        searchResponse = new SearchResponse(dbSearchRequest.getSearchRequest().getName(), true, dbSearchRequest.getSearchRequest().getClientPath(), Double.parseDouble(lineSplit[1]));
                        log.info(searchResponse.getClientPath());
                    }
                    else{
                        searchResponse = new SearchResponse(dbSearchRequest.getSearchRequest().getName(), false, dbSearchRequest.getSearchRequest().getClientPath(), 0.0);
                    }
                    //getSender().tell(searchResponse, getSelf());
                    getContext().getParent().tell(searchResponse, getSender());
                })
                .matchAny(o -> {
                    log.warning("received unknown message");

                })
                .build();
    }
}
