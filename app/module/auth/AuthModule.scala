package module.auth

import pattern.ModuleTrait
import play.api.libs.json.JsValue
import soundtrackmessages.MessageDefines
import AuthMessages._

import util.dao.from
import util.dao._data_connection
import util.errorcode.ErrorCode
import com.mongodb.casbah.Imports._

object AuthModule extends ModuleTrait {

	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_authCreateUser(data) => authCreateUser(data)
		case msg_authCheck(data) => authCheck(data)
		case msg_queryUser(data) => queryUser(data)
		case msg_authUpdateUser(data) => authUpdateUser(data)
		
		case _ => ???
	}
	
	def authCreateUser(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def authCheck(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def queryUser(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def authUpdateUser(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def Js2DBObject(data : JsValue) : MongoDBObject = {
		null
	}
	
	def DB2JsValue(x : MongoDBObject) : Map[String, JsValue] = {
		null
	}
}