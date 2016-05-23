package hacora.planner.receiver.core

import akka.actor.ActorSystem
import com.datastax.driver.core.{ProtocolOptions, Cluster}

/**
  * Created by momo on 5/23/16.
  */

trait CassandraCluster {
  def cluster: Cluster
}
trait ConfigCassandraCluster extends CassandraCluster{
  def system: ActorSystem

  private def config = system.settings.config

  import scala.collection.JavaConversions._
  private val cassandraConfig = config.getConfig("akka.db.cassandra")
  private val port = cassandraConfig.getInt("port")
  private val hosts = cassandraConfig.getStringList("hosts").toList

  lazy val cluster: Cluster =
    Cluster.builder().
      addContactPoints(hosts: _*).
      withCompression(ProtocolOptions.Compression.SNAPPY).
      withPort(port).
      build()

}
