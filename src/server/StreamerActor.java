package server;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.OverflowStrategy;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.Timeout;
import message.types.StreamRequest;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.io.File;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class StreamerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private String booksPath = "data/books";
    private File[] fileList;
    private File file = null;
    private ActorSelection client;

    @Override
    public Receive createReceive(){
        return receiveBuilder()
                .match(StreamRequest.class, streamRequest -> {
                    log.info("got stream req for "+streamRequest.getName());
                    log.info("proceed to stream");
                    client = context().actorSelection(streamRequest.getClientPath());
                    fileList = getFileList(booksPath);
                    for(File f : fileList){
                        if(f.toString().contains(streamRequest.getName())){
                            //System.out.println("Found file to stream: " + f.toString());
                            file = f;
                            break;
                        }
                    }
                    if(file!=null){
                        ActorMaterializer mat = ActorMaterializer.create(getContext());
                        Future<ActorRef> futureClient = client.resolveOne(new Timeout(5, TimeUnit.SECONDS));
                        ActorRef clientRef = Await.result(futureClient, Duration.create(5, "seconds"));
                        ActorRef run = Source.actorRef(1000, OverflowStrategy.dropNew())
                                .throttle(1, FiniteDuration.create(1, TimeUnit.SECONDS), 1, ThrottleMode.shaping())
                                .to(Sink.actorRef(clientRef, NotUsed.getInstance()))
                                .run(mat);

                        java.util.stream.Stream<String> lines = Files.lines(file.toPath());
                        lines.forEachOrdered(
                                line -> run.tell(line, getSelf()));
                        //run.tell(akka.actor.PoisonPill.getInstance(), ActorRef.noSender());
                    }
                    //getSelf().tell(akka.actor.PoisonPill.getInstance(), ActorRef.noSender());
                })
                .matchAny(o -> {
                    log.warning("received unknown message");
                    context().stop(getSelf());
                })
                .build();
    }
    private File[] getFileList(String directory_name){
        File directory = new File(directory_name);
        return directory.listFiles();
    }
}
