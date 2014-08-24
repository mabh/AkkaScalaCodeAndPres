package com.wm.tn.actormodel.j;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Inbox;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class HelloAkkaJava {
    public static class Greet implements Serializable {}
    public static class WhoToGreet implements Serializable {
        public final String who;
        public WhoToGreet(String who) {
            this.who = who;
        }
    }
    public static class Greeting implements Serializable {
        public final String message;
        public Greeting(String message) {
            this.message = message;
        }
    }

    public static class Greeter extends UntypedActor {
        String greeting = "";

        public void onReceive(Object message) throws InterruptedException {
            if (message instanceof WhoToGreet) {
                greeting = "hello, " + ((WhoToGreet) message).who;
            	Thread.sleep(3000);
            }
            else if (message instanceof Greet)
                // Send the current greeting back to the sender
                getSender().tell(new Greeting(greeting), getSelf());

            else unhandled(message);
        }
    }

    public static void main(String[] args) {
        // Create the 'helloakka' actor system
        final ActorSystem system = ActorSystem.create("helloakka");

        // Create the 'greeter' actor
        final ActorRef greeter = system.actorOf(Props.create(Greeter.class), "greeter");

        // Create the "actor-in-a-box"
        final Inbox inbox = Inbox.create(system);

        // Tell the 'greeter' to change its 'greeting' message
        System.out.println("1");
        greeter.tell(new WhoToGreet("akka"));
        System.out.println("2");
        
        // Ask the 'greeter for the latest 'greeting'
        // Reply should go to the "actor-in-a-box"
        inbox.send(greeter, new Greet());

        // Wait 5 seconds for the reply with the 'greeting' message
        Greeting greeting1 = (Greeting) inbox.receive(Duration.create(5, TimeUnit.SECONDS));
        System.out.println("Greeting: " + greeting1.message);

        // Change the greeting and ask for it again
        greeter.tell(new WhoToGreet("typesafe"));
        inbox.send(greeter, new Greet());
        Greeting greeting2 = (Greeting) inbox.receive(Duration.create(5, TimeUnit.SECONDS));	//deadline of 1 s to wait
        System.out.println("Greeting: " + greeting2.message);

        system.shutdown();
    }
}
