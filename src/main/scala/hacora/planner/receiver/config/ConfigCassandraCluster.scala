package hacora.planner.receiver.config

import akka.actor.ActorSystem
import com.datastax.driver.core.{Cluster, ProtocolOptions}

/**
  * Created by momo on 5/29/16.
  */

trait CassandraCluster {
  def cluster: Cluster
}

trait ConfigCassandraCluster extends CassandraCluster{

  def system: ActorSystem
  private def config = system.settings.config

  import scala.collection.JavaConversions._
  private val cassandraConfig = config.getConfig("akka.cassandra.db")
  private val port = cassandraConfig.getInt("port")
  private val hosts = cassandraConfig.getStringList("hosts").toList

  implicit lazy val cluster: Cluster =
    Cluster.builder().
      addContactPoints(hosts: _*).
      withCompression(ProtocolOptions.Compression.SNAPPY).
      withPort(port).
      build()

}
