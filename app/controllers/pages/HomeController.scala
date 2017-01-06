package controllers.pages

import play.api._
import play.api.mvc._
import play.api.libs.json.{JsArray, JsValue}
import play.api.libs.json.Json.toJson
import java.net.URLEncoder

import module.auth.AuthMessages._
import module.payload.PayloadMessages._
import pattern.ResultMessage.msg_CommonResultMessage
import controllers.common.requestArgsQuery._
import controllers.pages.AdminController.{InternalServerError, Ok}
import soundtrackmessages.MessageRoutes
import module.common.http.HTTP

object HomeController extends Controller {
	/**
	 * wechat app id
	 */
	val app_id = "wxcd44bfae640e4288"
	val app_secret = "c5d1a447c27ced348a28a8a113ed22a7"
	
	/**
	 * wechat business id
	 */
	val mch_id = "1270524501"
//	val mch_key = "RataVageTigreConejoDragon8888888"
	val mch_key = "jietengculturejietengcultureabcd"
	val pay_noncestr = "b927722419c52622651a871d1d9ed8b2"
	val pay_body = "会员年费"
	val pay_notify = "http://wxpay.weixin.qq.com/pub_v2/pay/notify.php"
	  
	val weixin_http = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + app_id + "&secret=" + app_secret
	val weixin_jsapi = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token="
	
	def index(t : String) = Action (request => checkAuth(t, request) { user =>
		import pattern.ResultMessage.lst_result
		val routes = MessageRoutes(msg_queryMutiplePayload(toJson(Map("token" -> t))) :: msg_CommonResultMessage() :: Nil, None)
		val result = commonExcution(routes)

		try {
			val status = (result \ "status").asOpt[String].map (x => x).getOrElse(throw  new Exception)
			if (status == "ok") {
				val contents = (result \ "result").asOpt[JsArray].get.value.toList
				Ok(views.html.homepage("每天5分钟听财经资讯")(user)(contents))

			} else throw new Exception

		} catch {
			case ex : Exception => InternalServerError("error 404")
		}
	})
	def payloadPlayer(a : String, t : String) = Action (request => checkAuth(t, request) { user => (Ok(views.html.admin_playload_player("播放试听")(a)))})
	def sysnotlst(t : String) = Action (request => checkAuth(t, request) { user =>
		(Ok(views.html.admin_playload_player("播放试听")("")))
	})
	def userprofile(t : String) = Action (request => checkAuth(t, request) { user =>
		(Ok(views.html.admin_playload_player("播放试听")("")))
	})
	
	def uploadTest = Action {
		Ok(views.html.fileuploadtest("file upload test"))
	}

	def audioplayTest = Action {
		Ok(views.html.audioplay("audio play test")("test"))
	}

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
		val openid = ((HTTP(url)).get(null) \ "openid").asOpt[String].get
		
		Redirect("http://whq628318.cn/mp/index/" + openid)
	}
}