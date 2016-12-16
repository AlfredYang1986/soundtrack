package module.auth

import pattern.ModuleTrait
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import soundtrackmessages.MessageDefines
import AuthMessages._

import util.dao.from
import util.dao._data_connection
import util.errorcode.ErrorCode
import com.mongodb.casbah.Imports._

import util.errorcode.ErrorCode
import module.sercurity.Sercurity
import java.util.Date

object AuthStatus {
	object normal extends AuthDefines(0, "normal")
	object authed extends AuthDefines(1, "auth")
	object admin extends AuthDefines(99, "admin")
}

sealed case class AuthDefines(t : Int, d : String)

object AuthModule extends ModuleTrait {

	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_authCreateUser(data) => authCreateUser(data)
		case msg_authCheck(data) => authCheck(data)
		case msg_queryUser(data) => queryUser(data)
		case msg_authUpdateUser(data) => authUpdateUser(data)
		
		case _ => ???
	}
	
	def initAuthDBS = {
		val builder = MongoDBObject.newBuilder
		val wechat_id = "admin"
		builder += "wechat_id" -> wechat_id

		builder += "screen_name" -> "admin"
		builder += "screen_photo" -> "admin"
		builder += "gender" -> 0
		
		val d = new Date().getTime
		builder += "token" -> Sercurity.md5Hash(wechat_id + Sercurity.getTimeSpanWithMillSeconds)

		builder += "start_in" -> -1
		builder += "expired_in" -> -1
		builder += "register" -> d
		builder += "auth" -> AuthStatus.admin
		
		_data_connection.getCollection("auth") += builder.result
	}
	
	def authCreateUser(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val obj = Js2DBObject(data)
			_data_connection.getCollection("auth") += obj
			(Some(DB2JsValue(obj)), None)
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson("wrong input")))
		}
	}
	
	def authCheck(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val token = (data \ "token").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			val least_auth = (data \ "least_auth").asOpt[Int].map (x => x).getOrElse(throw new Exception("wrong input"))
		
			(from db() in "auth" where ("token" -> token) select (DB2JsValue(_))).toList match {
				case head :: Nil => {
					if (head.get("auth").get.asOpt[Int].get < least_auth) throw new Exception("not allowed")
					else (Some(head), None)
				}
				case _ => throw new Exception("unknown user")
			}
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson("wrong input")))
		}
	}
	
	def queryUser(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val wechat_id = (data \ "wechat_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			
			(from db() in "auth" where ("wechat_id" -> wechat_id) select (DB2JsValue(_))).toList match {
				case head :: Nil => (Some(head), None)
				case _ => throw new Exception("unknown user")
			}
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson("wrong input")))
		}
	}
	
	def authUpdateUser(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val user_id = (data \ "user_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))

			(from db() in "auth" where ("user_id" -> user_id) select (DB2JsValue(_))).toList match {
				case head :: Nil => (Some(head), None)
				case _ => throw new Exception("unknown user")
			}
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson("wrong input")))
		}
	}
	
	def Js2DBObject(data : JsValue) : MongoDBObject = {
		
		val builder = MongoDBObject.newBuilder
		val wechat_id = (data \ "wechat_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
		builder += "wechat_id" -> (data \ "wechat_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))

		builder += "screen_name" -> (data \ "screen_name").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
		builder += "screen_photo" -> (data \ "screen_photo").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
		builder += "gender" -> (data \ "gender").asOpt[Int].map (x => x).getOrElse(throw new Exception("wrong input"))
		
		val d = new Date().getTime
		builder += "token" -> Sercurity.md5Hash(wechat_id + Sercurity.getTimeSpanWithMillSeconds)

		builder += "start_in" -> -1
		builder += "expired_in" -> -1
		builder += "register" -> d
		builder += "auth" -> AuthStatus.normal
		
		builder.result
	}
	
	def DB2JsValue(x : MongoDBObject) : Map[String, JsValue] = {
		Map("wechat_id" -> toJson(x.getAs[String]("wechat_id").get),
			"screen_name" -> toJson(x.getAs[String]("screen_name").get),
			"screen_photo" -> toJson(x.getAs[String]("screen_photo").get),
			"gender" -> toJson(x.getAs[Number]("gender").get.intValue),
			"token" -> toJson(x.getAs[String]("token").get),
			"start_in" -> toJson(x.getAs[Number]("start_in").get.longValue),
			"expired_in" -> toJson(x.getAs[Number]("expired_in").get.longValue),
			"register" -> toJson(x.getAs[Number]("register").get.longValue),
			"auth" -> toJson(x.getAs[Number]("auth").get.intValue))
	}
}