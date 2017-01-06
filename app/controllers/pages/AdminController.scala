package controllers.pages

import play.api._
import play.api.mvc._
import play.api.libs.json.{JsArray, JsValue}
import play.api.libs.json.Json.toJson
import java.net.URLEncoder

import module.auth.AuthMessages._
import module.payload.PayloadMessages._
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

	def AdminIndex(t : String) = Action (request => checkAdmin(t, request)(Ok(views.html.admin_index("管理员登录"))))
	def AdminPushPayload(t : String) = Action (request => checkAdmin(t, request)(Ok(views.html.admin_push_payload("管理员发布音频"))))
	def AdminPayloadlst(t : String) = Action (request => checkAdmin(t, request){
		import pattern.ResultMessage.lst_result
		val routes = MessageRoutes(msg_queryMutiplePayload(toJson(Map("token" -> t))) :: msg_CommonResultMessage() :: Nil, None)
		val result = commonExcution(routes)

		try {
			val status = (result \ "status").asOpt[String].map (x => x).getOrElse(throw  new Exception)
			if (status == "ok") {
				val contents = (result \ "result").asOpt[JsArray].get.value.toList
				Ok(views.html.admin_payload_lst("已发布音频")(contents))

			} else throw new Exception

		} catch {
			case ex : Exception => InternalServerError("error 404")
		}
	})
	def AdminUserLst(t : String) = Action (request => checkAdmin(t, request){
		import pattern.ResultMessage.lst_result
		val routes = MessageRoutes(msg_queryMultipleUsers(toJson(Map("token" -> t))) :: msg_CommonResultMessage() :: Nil, None)
		val result = commonExcution(routes)

		try {
			val status = (result \ "status").asOpt[String].map (x => x).getOrElse(throw  new Exception)
			if (status == "ok") {
				val contents = (result \ "result").asOpt[JsArray].get.value.toList
				println(contents)
				Ok(views.html.admin_user_lst("用户列表")(contents))

			} else throw new Exception

		} catch {
			case ex : Exception => InternalServerError("error 404")
		}
	})
	def AdminPayloadPlay(a : String, t : String) = Action (request => checkAdmin(t, request)(Ok(views.html.admin_playload_player("播放试听")(a))))

	def checkAdmin(t : String, request : Request[AnyContent])(r : Result) : Result = {
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
					if ((user \ "auth").asOpt[Int].get == 99) r
					else Redirect("/admin/login")

				} else throw new Exception

			} catch {
				/**
				  * 需要一个404或者500的错误界面
				  */
				case ex : Exception => Unauthorized("error 404")
			}
		}
	}

}
