/**
  * Created by BM on 03/01/2017.
  */

package module.admin

import pattern.ModuleTrait
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import soundtrackmessages.MessageDefines
import util.dao.from
import util.dao._data_connection
import util.errorcode.ErrorCode
import com.mongodb.casbah.Imports._
import util.errorcode.ErrorCode
import module.sercurity.Sercurity
import java.util.Date

import module.admin.AdminMessages._

object AdminModule extends ModuleTrait {
	override def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
//		case msg_pushAdminUser(data) => adminLogin(data)
//		case msg_popAdminUser(data) => adminLogin(data)
//		case msg_queryAdminUser(data) => adminLogin(data)
		case msg_AdminLogin(data) => adminLogin(data)
		case _ => ???
	}

	def adminLogin(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val user_name = (data \ "user_name").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			val password = (data \ "password").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))

			(from db() in "users" where ("wechat_id" -> user_name, "pwd" -> password) select (x => x)).toList match {
				case head :: Nil => (Some(DB2JsValue(head)), None)
				case _ => throw new Exception("user not existing")
			}

		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

//	def pushAdminUser(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
//		null
//	}
//
//	def popAdminUser(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
//		null
//	}
//
//	def queryAdminUser(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
//		null
//	}

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
