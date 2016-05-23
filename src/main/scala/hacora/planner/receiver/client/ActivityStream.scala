package hacora.planner.receiver.client

import akka.actor.{Actor, Props}
import hacora.planner.receiver.core.ActivityProcessor
import hacora.planner.receiver.models._
import ActivityProcessor.GetActivity
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing._

class ActivityStream extends HttpService with Actor {

  val activityProcessor = context.actorOf(Props[ActivityProcessor])


  def actorRefFactory = context

  def receive = runRoute(routes)


  def sendActivity(activity: Activity) =  activityProcessor ! GetActivity(Some(activity))

  object CustomJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val addressFormat = jsonFormat8(Address)
    implicit val geoCodeFormat = jsonFormat5(GeoCode)
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