package server;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import message.types.OrderRequest;
import message.types.OrderResponse;
import message.types.SearchRequest;
import message.types.SearchResponse;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class OrderActor extends AbstractActor{

    private ActorRef searchActor;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final String orders = "data/orders.txt";

    public OrderActor(ActorRef searchActor){
        this.searchActor=searchActor;
    }

    @Override
    public Receive createReceive(){
        return receiveBuilder()
                .match(OrderRequest.class, orderRequest -> {
                    SearchRequest searchRequest = new SearchRequest(orderRequest.getName(), orderRequest.getClientPath());
                    searchActor.tell(searchRequest, getSelf());
                    log.info("got order request");
                    log.info("sending search req");
                })
                .match(SearchResponse.class, searchResponse -> {
                    log.info("got search resp");
                    if(searchResponse.getInDatabase()){
                        BufferedWriter writer = null;
                        try {
                            writer = new BufferedWriter(new FileWriter(orders, true));
                            log.info("Saving order, title: " + searchResponse.getName());
                            String msg = searchResponse.getName() + "\n";
                            writer.write(msg);
                            writer.close();
                            log.info("Saving order successful");
                        } catch (Exception e) {
                            log.info("failed saving order");
                        }
                    }
                    OrderResponse orderResponse = new OrderResponse(searchResponse.getInDatabase(), searchResponse.getName(), searchResponse.getPrice());
                    getContext().actorSelection(searchResponse.getClientPath()).tell(orderResponse, getSelf());

                    log.info("sending order response to "+searchResponse.getClientPath());
                })
                .matchAny(o -> {
                    log.warning("received unknown message");
                })
                .build();
    }
}
