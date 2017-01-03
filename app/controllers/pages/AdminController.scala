package controllers.pages

import play.api._
import play.api.mvc._
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import java.net.URLEncoder

import module.auth.AuthMessages._
import module.common.http.HTTP
import pattern.ResultMessage.msg_CommonResultMessage
import soundtrackmessages.MessageRoutes

import controllers.common.requestArgsQuery._

/**
  * Created by BM on 03/01/2017.
  */
object AdminController extends Controller {
	def AdminLoginPage = Action {
		Ok(views.html.adminlogin("admin login"))
	}

	def AdminIndex(t : String) = Action { request =>
		var token = t
		if(token == "") token = request.cookies.get("token").map (x => x.value).getOrElse("")
		else Unit

		if (token == "") Ok("请先登陆在进行有效操作")
		else {
			import pattern.ResultMessage.common_result
			val routes = MessageRoutes(msg_queryUserByToken(toJson(Map("token" -> token))) :: msg_CommonResultMessage() :: Nil, None)
			val result = commonExcution(routes)

			try {
				val status = (result \ "status").asOpt[String].map (x => x).getOrElse(throw new Exception)
				if (status == "ok") {
					val user = (result \ "result").asOpt[JsValue].get
					if ((user \ "auth").asOpt[Int].get == 99) Ok(views.html.admin_index("test"))
					else Redirect("/admin/login")

				} else throw new Exception

			} catch {
				/**
				 * 需要一个404或者500的错误界面
				 */
				case ex : Exception => Ok("error 404")
			}

		}
	}
}
