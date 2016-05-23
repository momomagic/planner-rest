package hacora.planner.receiver

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import hacora.planner.receiver.client.ActivityStream
import spray.can.Http

import scala.concurrent.duration._


/**
  * Created by momo on 5/19/16.
  */
object Boot extends App{
  val config = ConfigFactory.load()

  implicit val actorSystem = ActorSystem("activities")
  val activitiesListener = actorSystem.actorOf(Props[ActivityStream],"activityListener")


  implicit val timeout = Timeout(20.seconds)



  actorSystem.registerOnTermination {
    actorSystem.log.info("Actor system shutdown")
  }

  IO(Http) ? Http.Bind(activitiesListener, interface = "localhost",config.getInt("port"))


}
