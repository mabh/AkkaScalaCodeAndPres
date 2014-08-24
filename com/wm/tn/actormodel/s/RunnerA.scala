package com.wm.tn.actormodel.s

import akka.actor.{ ActorRef, ActorSystem, Props, Actor, Inbox }
import scala.concurrent.duration._


class SimpleActor extends Actor {
	def receive = {
	  case count:Int => println(s"got $count")
	}
}

object RunnerA extends App { // a singleton 
	val system = ActorSystem("system")
	
	//init of actors start the actor since Akka 2.0
	val simple = system.actorOf(Props[SimpleActor], "Simple")
	
	simple ! 20	//fire and forget
	// !! or ? returns a future immediately - want to get some info out of actor
	// !!! returns results internally awaits on future
	
	system.shutdown();
}
