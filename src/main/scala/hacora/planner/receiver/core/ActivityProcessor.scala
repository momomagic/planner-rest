package hacora.planner.receiver.core

import akka.actor.Actor
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.utils.UUIDs
import com.datastax.driver.core.{BoundStatement, Cluster, PreparedStatement}
import hacora.planner.receiver.models.{Activity, SensorEvent}

/**
  * Created by momo on 5/20/16.
  */
class ActivityProcessor(cluster: Cluster) extends Actor {
  import ActivityProcessor._


  def addActivity(activity: Option[Activity]): Option[Activity] = {
    session(cluster).executeAsync(preparedStatement.bind(act))
    return activity
  }


  override def receive: Receive = {
    case GetActivity(activity) => addActivity(activity)
    case _ => println("not recognized")
  }

}

object ActivityProcessor {
  case class GetActivity(activity: Option[Activity])

  def prepareStatement(): BoundStatement{

  }

  private val session =  (cluster: Cluster) => cluster.connect(Constants.KeySpeace)

  def insertActivity(cluster: Cluster,activity: Activity): PreparedStatement = {
    import QueryBuilder.bindMarker
    val insert = QueryBuilder.
                  insertInto(Constants.table).
                  value("user_id", activity.userId).
                  value("created", UUIDs.timeBased())

    activity.source foreach {source =>
      insert.value("source",source)
    }

    activity.sensors foreach {events =>
      SensorEvent(events.sensorType,events.accuracy,events.values.getOrElse(null))
    }

    source  text,
    events set<frozen <motion_event>>,
      location frozen<location>,
  }
}

