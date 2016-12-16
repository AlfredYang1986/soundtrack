package module.systemnotify

import pattern.ModuleTrait
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import soundtrackmessages.MessageDefines
import SystemNotifyMessages._

import util.dao.from
import util.dao._data_connection
import util.errorcode.ErrorCode
import com.mongodb.casbah.Imports._

import java.util.Date
import module.sercurity.Sercurity

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
 		try {
 			val db_obj = Js2DBObject(data)
 			_data_connection.getCollection("sys_not") += db_obj
 			(Some(DB2JsValue(db_obj)), None)
 		
 		} catch {
 			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
 		}
 	}
 	
 	def querySystemNotify(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
 		try {
 			val sn_id = (data \ "sn_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
 			
 			(from db() in "sys_not" where ("sn_id" -> sn_id) select (x => x)).toList match {
 				case head :: Nil => {
		 			(Some(DB2JsValue(head)), None)
 				}
 				case _ => throw new Exception("service not existing")
 			}
 			
 		} catch {
 			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
 		}
 	}
 	
 	def queryMultipleSystemNotify(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
 		try {
 			val payload_id = (data \ "payload_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
 			
 			val lst = (from db() in "sys_not" where ("payload_id" -> payload_id) select (DB2JsValue(_))).toList
 			(Some(Map("result" -> toJson(lst), "message" -> toJson("multi_sys_not"))), None)	
 		
 		} catch {
 			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
 		}
 	}
 	
 	def updateSystemNotify(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
 		try {
 			val sn_id = (data \ "sn_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))		
 		
 			(from db() in "sys_not" where ("sn_id" -> sn_id) select (x => x)).toList match {
 				case head :: Nil => {
		 			(Some(DB2JsValue(head)), None)
 				}
 				case _ => throw new Exception("service not existing")
 			}
 			
 		} catch {
 			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
 		}
 	}
 	
 	def popSystemNotify(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
 		try {
 			val sn_id = (data \ "sn_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
 		
 			(from db() in "sys_not" where ("sn_id" -> sn_id) select (x => x)).toList match {
 				case head :: Nil => {
	 				_data_connection.getCollection("sys_not") -= head
		 			(Some(DB2JsValue(head)), None)	
 				}
 				case _ => throw new Exception("service not existing")
 			}
 			
 		} catch {
 			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
 		}
 	}
 	
 	def Js2DBObject(data : JsValue) : MongoDBObject = {
 		val builder  = MongoDBObject.newBuilder
 		val title = (data \ "title").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
 		builder += "title" -> title
 		builder += "content" -> (data \ "content").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
 		builder += "date" -> new Date().getTime
 		builder += "sn_id" -> Sercurity.md5Hash(title + Sercurity.getTimeSpanWithMillSeconds)
 		
 		builder.result
 	}
 	
 	def DB2JsValue(x : MongoDBObject) : Map[String, JsValue] = {
 		Map("sn_id" -> toJson(x.getAs[String]("sn_id").get),
 			"title" -> toJson(x.getAs[String]("title").get),
 			"content" -> toJson(x.getAs[String]("content").get),
 			"date" -> toJson(x.getAs[Number]("date").get.longValue))
 	}
}