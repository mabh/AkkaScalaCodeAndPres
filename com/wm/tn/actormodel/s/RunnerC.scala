package com.wm.tn.actormodel.s

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorSystem
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import scala.util.Success
import scala.util.Failure

case class CustomerId(id: Long)
case class Customer(id: CustomerId, lastname: String)

object db {
  def readCustomerNo(id: CustomerId) = {
    val lastName = if (id.id == 42) "Smith" else "Bush"
    Future[String](lastName)
  }
}

class DBActor extends Actor {
  val log = Logging(context.system, DBActor.this)
  def receive = {
    case id: CustomerId => db.readCustomerNo(id) pipeTo sender
    case _ => log.error("unknow message")
  }
}

object RunnerC extends App {
  
  val system = ActorSystem("mySystem")

  val dbActor = system.actorOf(Props[DBActor])
  implicit val timeout = Timeout(25 seconds)
  
  val future = dbActor ? (CustomerId(43))
  
  future onComplete {
    case Success(customer) => println(s"customer is: $customer")
    case Failure(t: Throwable) => println(s"Shit, something went wrong: $t")
  }
  
  
   // an Actor System needs to be shut down
  Console.readLine(s"Hit return to stop\n")
  system.shutdown()
}








