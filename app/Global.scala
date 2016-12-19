import play.api.GlobalSettings

import module.auth
import util.dao._

object Global extends GlobalSettings {
	
	override def onStart(application: play.api.Application)  = {
		import scala.concurrent.duration._
		import play.api.Play.current
		println("application started")
		if (_data_connection.getCollection("soundtrack").isEmpty
			&& !_data_connection.isExisted("users")) {
			println("application db inits")
			auth.AuthModule.initAuthDBS
		}
	}
	
	override def onStop(application: play.api.Application) = {
		println("application stoped")
	}
}