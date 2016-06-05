package hacora.planner.receiver.models

/**
  * Created by momo on 5/20/16.
  */


case class Activity(
                     val userId: Long,
                     val sensor: SensorEvent,
                     val location: Location)
case class SensorEvent(
                        val sensorType: String,
                        val accuracy: Int,
                        val values: Option[List[Float]])

case class Location(
                     val latitude: Double,
                     val longitude: Double,
                     val speed: Float = 0,
                     val addressList: Option[List[Address]])

case class Address(
                    val addressArea: Option[String],
                    val adminArea: Option[String],
                    val countryCode: Option[String],
                    val countryName: Option[String],
                    val featureName: Option[String],
                    val locality: Option[String],
                    val postalCode: Option[String],
                    val premises: Option[String])