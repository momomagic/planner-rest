package hacora.planner.receiver.core

import akka.actor.{ActorSystem, Actor}
import com.datastax.driver.core._
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.utils.UUIDs
import hacora.planner.receiver.config.ConfigCassandraCluster
import hacora.planner.receiver.models._

import scala.collection.mutable.ListBuffer
import akka.event.Logging

import collection.JavaConverters._

/**
  * Created by momo on 5/20/16.
  */
class ActivityProcessor extends Actor with ConfigCassandraCluster {
  val log = Logging(context.system, this)

  def receive = {

    case x: Activity => {
      log.debug("processor");
      sender() ! addActivity(x)
    }
    case _ => sender() ! "Not recognized"
  }

  import ActivityProcessor._

  def addActivity(activity: Activity): Boolean = {
    log.info(s"received activity ${activity}")
    val insertedValue = insertActivity(activity)
    log.debug(s"${insertedValue}")
    cluster.connect(Constants.KeySpeace).executeAsync(insertedValue)
    return true
  }

  implicit lazy val system = ActorSystem()
}


object ActivityProcessor {

  def getUDTType(name: String)(implicit cluster: Cluster): UserType = {
    import Constants.{KeySpeace => keyspace}
    name match {
      case "LOCATION" => cluster.getMetadata().getKeyspace(keyspace).getUserType("location")
      case "ADDRESS" => cluster.getMetadata.getKeyspace(keyspace).getUserType("address")
      case "MOTION" => cluster.getMetadata.getKeyspace(keyspace).getUserType("motion_event")
      case _ => throw new IllegalArgumentException
    }

  }

  implicit def getAddressesUDTValue(addresses: Option[List[Address]])(implicit cluster: Cluster): java.util.List[UDTValue] = {

    def getAddressValue(address: Address): UDTValue = {
      val value = getUDTType("ADDRESS").newValue()
      address.adminArea foreach (value.setString("adminArea", _))
      address.countryCode.foreach(value.setString("countryCode", _))
      address.countryName.foreach(value.setString("countryName", _))
      address.featureName.foreach(value.setString("featureName", _))
      address.locality.foreach(value.setString("locality", _))
      address.postalCode.foreach(value.setString("postalCode", _))
      address.premises.foreach(value.setString("premises", _))
      return value
    }

    if (addresses.isDefined)
      return addresses.get.map(address => getAddressValue(address)).to[ListBuffer].asJava
    else
      return null
  }


  implicit def locationToUDTValue(location: Location)(implicit cluster: Cluster): UDTValue =
    getUDTType("LOCATION").
      newValue().
      setDouble("altitude", location.latitude).
      setDouble("longitude", location.longitude).
      setFloat("speed", location.speed).
      setList("addresses", location.addressList)

  implicit def sensorsToUDTValue(sensor: SensorEvent)(implicit cluster: Cluster): UDTValue = {
    if(sensor.values.isDefined) {
      return getUDTType("MOTION").newValue().
        setString("sensorType", sensor.sensorType).
        setInt("accuracy", sensor.accuracy).
        setList("values", sensor.values.get.to[ListBuffer].asJava)
    } else {
      return null
    }

  }

  def insertActivity(activity: Activity)(implicit cluster: Cluster): Statement = {
    val insert = QueryBuilder.
      insertInto(Constants.table).
      value("user_id", activity.userId).
      value("created", UUIDs.timeBased()).
      value("location", locationToUDTValue(activity.location)).
      value("event", sensorsToUDTValue(activity.sensor))
    return insert
  }
}




