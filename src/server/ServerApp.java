package server;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.io.FileNotFoundException;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class ServerApp {
    public static void main(String[] args) throws Exception{
        File configFile = new File("server_app.conf");
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem system = ActorSystem.create("server_system", config);
        final ActorRef serverActor = system.actorOf(Props.create(ServerActor.class), "serverActor");

        if (System.in.read()==0) system.terminate();
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
