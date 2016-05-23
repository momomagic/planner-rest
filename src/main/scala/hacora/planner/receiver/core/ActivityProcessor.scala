package hacora.planner.receiver.core

import akka.actor.Actor
import com.datastax.driver.core.Cluster
import hacora.planner.receiver.core.ActivityProcessor.GetActivity
import hacora.planner.receiver.models.Activity

/**
  * Created by momo on 5/20/16.
  */
class ActivityProcessor(cluster: Cluster) extends Actor {

  val session = cluster.connect(KeySpaces.)
  val preparedStatement = session.prepare("INSERT INTO activity(key, user_user, text, createdat) VALUES (?, ?, ?, ?);")

  def addActivity(activity: Option[Activity]): String = {
    activity match {
      case Some(activity) => {
        println(activity)
        return "success"
      }
      case None => "Failed"
    }

  }


  override def receive: Receive = {
    case GetActivity(activity) => addActivity(activity)
    case _ => println("not recognized")
  }

}

object ActivityProcessor {
  case class GetActivity(activity: Option[Activity])

}

