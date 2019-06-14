package client;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import message.types.OrderRequest;
import message.types.SearchRequest;
import message.types.StreamRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import scala.concurrent.duration.Duration;
import java.util.concurrent.Executors;

import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.*;

public class ClientApp {
    public static void main(String[] args) throws Exception{
        File configFile = new File("client_app.conf");
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem system = ActorSystem.create("client_system", config);
        final ActorRef clientActor = system.actorOf(Props.create(ClientActor.class, "akka.tcp://server_system@127.0.0.1:3552/user/serverActor"), "clientActor");

        final String clientActorPath = system.provider().getDefaultAddress().toString() + "/user/clientActor";
        System.out.println(clientActorPath);
        //System.out.println(clientActor);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("q")) {
                break;
            }
            String name = line.split(" ")[1];
            if(line.startsWith("order")){
                OrderRequest req = new OrderRequest(name, clientActorPath);
                clientActor.tell(req, ActorRef.noSender());
            }
            else if(line.startsWith("search")){
                SearchRequest req = new SearchRequest(name, clientActorPath);
                clientActor.tell(req, ActorRef.noSender());
            }
            else if(line.startsWith("stream")){
                StreamRequest req = new StreamRequest(name, clientActorPath);
                clientActor.tell(req, ActorRef.noSender());
            }
            else{
                System.out.println("Unknown operation :c");
            }
        }

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
