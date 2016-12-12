package module.systemnotify

import pattern.ModuleTrait
import play.api.libs.json.JsValue
import soundtrackmessages.MessageDefines
import SystemNotifyMessages._

import util.dao.from
import util.dao._data_connection
import util.errorcode.ErrorCode
import com.mongodb.casbah.Imports._

class SystemNotifyModule {
	
 	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_pushSystemNotify(data) => pushSystemNotify(data)
		case msg_querySystemNotify(data) => querySystemNotify(data)
		case msg_queryMultipleSystemNotify(data) => queryMultipleSystemNotify(data)
		case msg_updateSystemNotify(data) => updateSystemNotify(data)
		case msg_popSystemNotify(data) => popSystemNotify(data)
		
		case _ => ???
	}
 	
 	def pushSystemNotify(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
 		null
 	}
 	
 	def querySystemNotify(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
 		null
 	}
 	
 	def queryMultipleSystemNotify(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
 		null
 	}
 	
 	def updateSystemNotify(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
 		null
 	}
 	
 	def popSystemNotify(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
 		null
 	}
 	
 	def Js2DBObject(data : JsValue) : MongoDBObject = {
 		null
 	}
 	
 	def DB2JsValue(x : MongoDBObject) : Map[String, JsValue] = {
 		null
 	}
}