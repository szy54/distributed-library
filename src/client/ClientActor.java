package client;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.sun.deploy.util.SessionState;
import message.types.*;

public class ClientActor extends AbstractActor{

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorSelection server;

    public ClientActor(String serverPath){
        this.server=getContext().actorSelection(serverPath);
    }

    @Override
    public AbstractActor.Receive createReceive() {

        return receiveBuilder()
                .match(OrderRequest.class, order ->{
                    server.tell(order, getSelf());
                    log.info("Sent order req");
                })
                .match(SearchRequest.class, search ->{
                    server.tell(search, getSelf());
                    log.info("Sent search req");
                })
                .match(StreamRequest.class, stream ->{
                    server.tell(stream, getSelf());
                    log.info("Sent stream req");
                })
                .match(OrderResponse.class, orderResponse ->{
                    if(orderResponse.getSuccesful()){
                        log.info("price of " +  orderResponse.getName() + " is " + orderResponse.getPrice());
                    }
                    else{
                        log.error("Order unsuccesful");
                    }
                })
                .match(SearchResponse.class, searchResponse -> {
                    log.info(searchResponse.getName());
                    log.info(searchResponse.getInDatabase()?
                            " is in the DB and costs " + searchResponse.getPrice().toString():
                            " is not in the DB");

                })
                .match(String.class, response ->{
                    log.info(response);
                })
                //match(Error.class, error->{}
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

}
