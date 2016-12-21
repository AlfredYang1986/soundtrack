package controllers.pages

import play.api._
import play.api.mvc._

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import java.net.URLEncoder

import module.common.http.HTTP

object HomeController extends Controller {
	/**
	 * wechat app id
	 */
	val app_id = "wxb46efccede9f5a76"
	val app_secret = "06ff8eb4765422c073f555284e227a9f"
	
	/**
	 * wechat business id
	 */
	val mch_id = "1270524501"
//	val mch_key = "RataVageTigreConejoDragon8888888"
	val mch_key = "jietengculturejietengcultureabcd"
	val pay_noncestr = "b927722419c52622651a871d1d9ed8b2"
	val pay_body = "答主咨询费"
	val pay_notify = "http://wxpay.weixin.qq.com/pub_v2/pay/notify.php"
	  
	val weixin_http = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + app_id + "&secret=" + app_secret
	val weixin_jsapi = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token="
	
	def index(wechatid : String) = Action {
		Ok(views.html.index("soundtrack")
				(toJson(Map("name" -> "abcde")))
				(toJson(Map("name" -> "abcde")) :: toJson(Map("name" -> "abcde")) :: Nil))
	}
	
	def uploadTest = Action {
		Ok(views.html.fileuploadtest("file upload test"))
	}

	def audioplayTest = Action {
		Ok(views.html.audioplay("audio play test")("test"))
	}
	
	/**
	 * wechat oauth
	 */
	def queryWechatAuthCode = Action {
		val redirect_uri = "http://10.6.20.42:9000/queryWechatOpenID"
		val authCodeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + URLEncoder.encode(app_id) + "&redirect_uri=" + URLEncoder.encode(redirect_uri) + "&response_type=code&scope=snsapi_base"
		
		Redirect(authCodeUrl)
	}
	
	def queryWechatOpenID(code: String, status: String) = Action {
		val url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + app_id + "&secret=" + app_secret + "&code=" + code + "&grant_type=authorization_code"
		val openid = ((HTTP(url)).get(null) \ "openid").asOpt[String].get
		
		Redirect("http://10.6.20.42:9000/index/" + openid)
	}
}