package controllers.pages

import play.api._
import play.api.mvc._

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import java.net.URLEncoder

import module.common.http.HTTP

/**
  * Created by BM on 03/01/2017.
  */
object AdminController extends Controller {
	def AdminLoginPage = Action {
		Ok(views.html.adminlogin("admin login"))
	}
}
