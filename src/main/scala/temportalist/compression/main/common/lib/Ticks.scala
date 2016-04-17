package temportalist.compression.main.common.lib

/**
  *
  * Created by TheTemportalist on 4/16/2016.
  *
  * @author TheTemportalist
  */
object Ticks {

	def getTicks(ticks: Int = 0, seconds: Int = 0, minutes: Int = 0, hours: Int = 0): Int = {
		val totalHours = hours
		val totalMinutes = totalHours * 60 + minutes
		val totalSeconds = totalMinutes * 60 + seconds
		totalSeconds * 20 + ticks
	}

}
