package com.wm.tn.actormodel.s

import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox, ActorContext }
import scala.concurrent.duration._

class StatefulCounter extends Actor {
  var count:Int = 0
  def receive = {
    case "incr" =>
      count += 1
    case "decr" =>
      count -= 1
    case "get" =>
      sender ! count
  }
}

class FunctionalCounter extends Actor {

  //kind of tail recursion
  def counter(n: Int): Receive = {
    case "incr" =>
      context.become(counter(n + 1))
      System.out.println("incremented")
    case "decr" =>
      context.become(counter(n - 1))
      println("decremented")

    //actor trait has an implicit sender which represents the sender of the message to the actor - common pattern
    case "get" => sender ! n
  }
  def receive = counter(0)
}

class CounterMain extends Actor {
  import context._
  
  //create an actor of type Counter and give it a name "counter"
  val scounter = context.actorOf(Props[StatefulCounter], "scounter")
  val fcounter = context.actorOf(Props[FunctionalCounter], "fcounter")

  def stateful: Receive = {
    case "incdec" => {
      scounter ! "incr"
      scounter ! "incr"
      scounter ! "incr"
      scounter ! "decr"
      scounter ! "get"
    }
    //case count:Int => println(s"stateful returned $count")
  }

  def functional: Receive = {
    case "incdec" => {
      fcounter ! "incr"
      fcounter ! "incr"
      fcounter ! "incr"
      fcounter ! "decr"
      fcounter ! "get"
    }
    case count:Int => println(s"functional returned $count")
  }

  def receive = {
    case count: Int => println(s"count was $count")
    //context.stop(self)
    case "func" => become(functional)
    case "state" => become(stateful)
  }
}

object RunnerB extends App {
  val system = ActorSystem("system")

  val cmain = system.actorOf(Props[CounterMain], "cmain")

  cmain ! "func"

  cmain ! "incdec"

  Console.readLine("press Return key to terminate...")
  
  system.shutdown();
}
