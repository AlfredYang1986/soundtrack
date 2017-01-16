import play.api.GlobalSettings
import module.auth
import util.dao._
import play.api.mvc.Results._
import play.api.mvc.WithFilters
import play.filters.gzip.GzipFilter

object Global extends WithFilters(new GzipFilter) with GlobalSettings {
	
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