package hacora.planner.receiver.server

import akka.actor.{Actor, Props}
import akka.event.Logging
import hacora.planner.receiver.core.ActivityProcessor
import hacora.planner.receiver.models._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing._

class ActivityStream extends HttpService with Actor {

  val activityProcessor = context.actorOf(Props[ActivityProcessor])

  val log = Logging(context.system, this)

  def actorRefFactory = context

  def receive = runRoute(routes)


  def sendActivity(activity: Activity) = {
    log.debug("received activity will start sending it ")
    activityProcessor ! activity
  }


  object CustomJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val addressFormat = jsonFormat8(Address)
    implicit val locationFormat = jsonFormat4(Location)
    implicit val sensorFormat = jsonFormat3(SensorEvent)
    implicit val ActivityFormat = jsonFormat3(Activity)
  }


  import CustomJsonProtocol._

  def routes = {
    post {
      path("activity") {
        entity(as[Activity]) { activity =>
          sendActivity(activity)
          complete("Success")

        }
      }

    }
  }


}