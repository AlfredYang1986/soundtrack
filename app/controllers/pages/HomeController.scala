package controllers.pages

import play.api._
import play.api.mvc._

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

object HomeController extends Controller {
	def index = Action {
		Ok(views.html.index("soundtrack")
				(toJson(Map("name" -> "abcde")))
				(toJson(Map("name" -> "abcde")) :: toJson(Map("name" -> "abcde")) :: Nil))
	}
	
	def uploadTest = Action {
		Ok(views.html.fileuploadtest("file upload test"))
	}
}