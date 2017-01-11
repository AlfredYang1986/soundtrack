package module.payload

import pattern.ModuleTrait
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import soundtrackmessages.MessageDefines
import PayloadMessages._

import util.dao.from
import util.dao._data_connection
import util.errorcode.ErrorCode
import com.mongodb.casbah.Imports._

import java.util.Date
import module.sercurity.Sercurity

object PayloadModule extends ModuleTrait {

	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_pushPayload(data) => pushPayload(data)
		case msg_queryPayload(data) => queryPayload(data)
		case msg_queryMutiplePayload(data) => queryMultiPayload(data)
		case msg_updatePayload(data) => updatePayload(data)
		case msg_popPayload(data) => popPayload(data)
		case msg_queryPayloadWithPath(data) => queryPayladWithPath(data)

		case _ => ???
	}
	
	def pushPayload(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val db_object = Js2DBObject(data)
			_data_connection.getCollection("payload") += db_object
			(Some(DB2JsValue(db_object)), None)
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryPayload(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val payload_id = (data \ "payload_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			
			(from db() in "payload" where ("payload_id" -> payload_id) select (x => x)).toList match {
				case head :: Nil => {
					(Some(DB2JsValue(head)), None)		
				}
				case _ => throw new Exception("service not existing")
			}
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def queryPayladWithPath(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val path = (data \ "path").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))

			(from db() in "payload" where ("path" -> path) select (x => x)).toList match {
				case head :: Nil => {
					(Some(DB2JsValue(head)), None)
				}
				case _ => throw new Exception("service not existing")
			}

		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def queryMultiPayload(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(20)
			val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
			
			val lst = (from db() in "payload").selectSkipTop(skip)(take)("date")(DB2JsValue(_)).toList
			(Some(Map("result" -> toJson(lst),
				 "message" -> toJson("multi_payload"))), None)
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def updatePayload(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val payload_id = (data \ "payload_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
		
			(from db() in "payload" where ("payload_id" -> payload_id) select (x => x)).toList match {
				case head :: Nil => {
					(data \ "title").asOpt[String].map (x => head += "title" -> x).getOrElse(Unit) 
					(data \ "sub_title").asOpt[String].map (x => head += "sub_title" -> x).getOrElse(Unit) 
					(data \ "cover_pic").asOpt[String].map (x => head += "cover_pic" -> x).getOrElse(Unit) 
					(data \ "path").asOpt[String].map (x => head += "path" -> x).getOrElse(Unit) 
					(data \ "message_id").asOpt[String].map (x => head += "message_id" -> x).getOrElse(Unit) 
					(Some(DB2JsValue(head)), None)		
				}
				case _ => throw new Exception("service not existing")
			}
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def popPayload(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val payload_id = (data \ "payload_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
	
			(from db() in "payload" where ("payload_id" -> payload_id) select (x => x)).toList match {
				case head :: Nil => {
					_data_connection.getCollection("payload") -= head
					(Some(DB2JsValue(head)), None)
				}
				case _ => throw new Exception("service not existing")
			}
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def Js2DBObject(data : JsValue) : MongoDBObject = {
		val builder = MongoDBObject.newBuilder
	
		val title = (data \ "title").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
		builder += "title" -> title
		builder += "sub_title" -> (data \ "sub_title").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
		builder += "cover_pic" -> (data \ "cover_pic").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))

		builder += "play_times" -> (data \ "play_times").asOpt[Int].map (x => x).getOrElse(0)
		builder += "path" -> (data \ "path").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
		
		builder += "message_id" -> (data \ "message_id").asOpt[String].map (x => x).getOrElse("")
	
		builder += "date" -> new Date().getTime
		builder += "payload_id" -> Sercurity.md5Hash(title + Sercurity.getTimeSpanWithMillSeconds)
		
		builder.result
	}
	
	def DB2JsValue(x : MongoDBObject) : Map[String, JsValue] = {
		Map("title" -> toJson(x.getAs[String]("title").get),
			"sub_title" -> toJson(x.getAs[String]("sub_title").get),
			"cover_pic" -> toJson(x.getAs[String]("cover_pic").get),
			"play_times" -> toJson(x.getAs[Number]("play_times").get.intValue),
			"path" -> toJson(x.getAs[String]("path").get),
			"date" -> toJson(x.getAs[Number]("date").get.longValue),
			"message_id" -> toJson(x.getAs[String]("message_id").get))
	}
}