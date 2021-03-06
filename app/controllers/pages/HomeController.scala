package controllers.pages

import play.api._
import play.api.mvc._
import play.api.libs.json.{JsArray, JsValue}
import play.api.libs.json.Json.toJson
import java.net.URLEncoder

import module.auth.AuthMessages._
import module.payload.PayloadMessages._
import module.systemnotify.SystemNotifyMessages._
import pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery._
import controllers.pages.AdminController.{InternalServerError, Ok}
import soundtrackmessages.MessageRoutes
import module.common.http.HTTP

object HomeController extends Controller {
	/**
	 * wechat app id
	 */
	val app_id = "wx86135fc9a5e67ff3"
	val app_secret = "74f13dbccc1b8323aadebda15d35144d"

	def index(t : String) = Action (request => checkAuth(t, request) { user =>
		import pattern.ResultMessage.lst_result
		val routes = MessageRoutes(msg_queryMutiplePayload(toJson(Map("token" -> t))) :: msg_CommonResultMessage() :: Nil, None)
		val result = commonExcution(routes)

		try {
			val status = (result \ "status").asOpt[String].map (x => x).getOrElse(throw  new Exception)
			if (status == "ok") {
				val contents = (result \ "result").asOpt[JsArray].get.value.toList
				Ok(views.html.homepage("每天听见财经资讯")(t)(user)(contents))

			} else throw new Exception

		} catch {
			case ex : Exception => InternalServerError("error 404")
		}
	})
	def payloadPlayer(a : String, t : String) = Action (request => checkAuth(t, request) { user =>
		import pattern.ResultMessage.common_result
		val routes = MessageRoutes(msg_updatePayloadWithPath(toJson(Map("path" -> toJson(a), "play_times" -> toJson(1)))) :: msg_queryPayloadWithPath(toJson(Map("path" -> a))) :: msg_CommonResultMessage() :: Nil, None)
		val result = commonExcution(routes)

		try {
			val status = (result \ "status").asOpt[String].map (x => x).getOrElse(throw new Exception)
			if (status == "ok") {
				val payload = (result \ "result").asOpt[JsValue].get
				(Ok(views.html.audioplay("每天听见财经资讯")(user)(payload)(a)))

			} else throw new Exception

		} catch {
			case _ => BadRequest("invalid input args")
		}
	})
	def sysnotlst(t : String) = Action (request => checkAuth(t, request) { user =>
		import pattern.ResultMessage.lst_result
		val routes = MessageRoutes(msg_allSystemNotify(toJson("")) :: msg_CommonResultMessage() :: Nil, None)
		val result = commonExcution(routes)

		try {
			val status = (result \ "status").asOpt[String].map (x => x).getOrElse(throw new Exception)
			if (status == "ok") {
				val sys_lst = (result \ "result").asOpt[JsArray].get.value.toList
				Ok(views.html.sysnotpage("每天听见财经资讯")(t)(user)(sys_lst))

			} else throw new Exception

		} catch {
			case _ => BadRequest("invalid input args")
		}
	})
	def userprofile(t : String) = Action (request => checkAuth(t, request) { user =>
		import pattern.ResultMessage.common_result
		val routes = MessageRoutes(msg_authWithWechat(toJson(Map("wechat_id" -> t))) :: msg_CommonResultMessage() :: Nil, None)
		val result = commonExcution(routes)

		try {
			val status = (result \ "status").asOpt[String].map (x => x).getOrElse(throw new Exception)
			if (status == "ok") {
				val user = (result \ "result").asOpt[JsValue].get
				(Ok(views.html.profilepage("每天听见财经资讯")(t)(user)))

			} else throw new Exception

		} catch {
			case _ => BadRequest("invalid input args")
		}

	})
	
	def checkAuth(t : String, request : Request[AnyContent])(fr : JsValue => Result) : Result = {
		var token = t
		if(token == "") token = request.cookies.get("token").map (x => x.value).getOrElse("")
		else Unit

		if (token == "") Ok("请先登陆在进行有效操作")
		else {
			import pattern.ResultMessage.common_result
			val routes = MessageRoutes(msg_authWithWechat(toJson(Map("wechat_id" -> token))) :: msg_CommonResultMessage() :: Nil, None)
			val result = commonExcution(routes)

			try {
				val status = (result \ "status").asOpt[String].map (x => x).getOrElse(throw new Exception)
				if (status == "ok") {
					val user = (result \ "result").asOpt[JsValue].get
					if ((user \ "auth").asOpt[Int].get == 0) fr(user)
					else Redirect("/admin/login")

				} else throw new Exception

			} catch {
				/**
				  * 需要一个404或者500的错误界面
				  */
				case _ => Redirect("/mp/queryWechatAuthCode")
			}
		}
	}

	/**
	 * wechat oauth
	 */
	def queryWechatAuthCode = Action {
		val redirect_uri = "http://whq628318.cn/mp/queryWechatOpenID"
		val authCodeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + URLEncoder.encode(app_id) + "&redirect_uri=" + URLEncoder.encode(redirect_uri) + "&response_type=code&scope=snsapi_userinfo#wechat_redirect"
		Redirect(authCodeUrl)
	}
	
	def queryWechatOpenID(code: String, status: String) = Action {
		val url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + app_id + "&secret=" + app_secret + "&code=" + code + "&grant_type=authorization_code"
		val result = ((HTTP(url)).get(null))
		val openid = (result \ "openid").asOpt[String].get
		val auth_token = (result \ "access_token").asOpt[String].get

		val url_user_info = "https://api.weixin.qq.com/sns/userinfo?access_token=" + auth_token + "&openid=" + openid + "&lang=zh_CN"
		val reVal = (HTTP(url_user_info).get(null))

		val screen_name = (reVal \ "nickname").asOpt[String].get
		val screen_photo = (reVal \ "headimgurl").asOpt[String].get
		val gender = (reVal \ "sex").asOpt[Int].get
		val last_date = new java.util.Date().getTime

		val args = toJson(Map("wechat_id" -> toJson(openid),
							  "screen_name" -> toJson(screen_name),
			 				  "screen_photo" -> toJson(screen_photo),
							  "gender" -> toJson(gender),
							  "last_update" -> toJson(last_date)))
		import pattern.ResultMessage.common_result
		val routes = MessageRoutes(msg_authUpdateUser(args) :: msg_CommonResultMessage() :: Nil, None)
		commonExcution(routes)

		Redirect("http://whq628318.cn/mp/index/" + openid)
	}
}