package module.comments

import pattern.ModuleTrait
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import soundtrackmessages.MessageDefines
import CommentsMessages._

import util.dao.from
import util.dao._data_connection
import util.errorcode.ErrorCode
import com.mongodb.casbah.Imports._

import java.util.Date
import module.sercurity.Sercurity

object CommentsModule extends ModuleTrait {

	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_pushComment(data) => pushComment(data)
		case msg_queryTopComments(data) => queryTopComments(data)
		case msg_queryComments(data) => queryComments(data)
		case msg_updateComment(data) => updateComment(data)
		case msg_popComment(data) => popComment(data)
		case msg_likeComment(data) => likeComment(data)
		
		case _ => ???
	}
	
	def pushComment(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val db_obj = Js2DBObject(data)
			_data_connection.getCollection("commends") += db_obj
			(Some(DB2JsValue(db_obj)), None)
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryTopComments(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(3)
			val skip = 0
			
			val lst = (from db() in "comments").selectSkipTop(skip)(take)("likes")(DB2JsValue(_)).toList
			(Some(Map("result" -> toJson(lst), "message" -> toJson("top_comments"))), None)
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def queryComments(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(20)
			val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)
			
			val lst = (from db() in "comments").selectSkipTop(skip)(take)("date")(DB2JsValue(_)).toList
			(Some(Map("result" -> toJson(lst), "message" -> toJson("comments"))), None)
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def updateComment(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val comment_id = (data \ "comment_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			
			(from db() in "comments" where ("comment_id" -> comment_id) select (x => x)).toList match {
				case head :: Nil => {
					(Some(DB2JsValue(head)), None)
				}
				case _ => throw new Exception("service not existing")
			}
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def popComment(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val comment_id = (data \ "comment_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			
			(from db() in "comments" where ("comment_id" -> comment_id) select (x => x)).toList match {
				case head :: Nil => {
					(Some(DB2JsValue(head)), None)
				}
				case _ => throw new Exception("service not existing")
			}
			
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}
	
	def likeComment(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val comment_id = (data \ "comment_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
			
			(from db() in "comments" where ("comment_id" -> comment_id) select (x => x)).toList match {
				case head :: Nil => {
					val likes = head.getAs[Int]("likes").get
					head += "likes" -> (likes + 1).asInstanceOf[Number]

					_data_connection.getCollection("comments").update(DBObject("comment_id" -> comment_id), head)
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

		val content = (data \ "content").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
		builder += "content" -> content
		builder += "owner_id" -> (data \ "owner_id").asOpt[String].map (x => x).getOrElse(throw new Exception("wrong input"))
		builder += "date" -> new Date().getTime
		builder += "likes" -> 0
	
		builder += "comment_id" -> Sercurity.md5Hash(content + Sercurity.getTimeSpanWithMillSeconds)
		
		builder.result
	}
	
	def DB2JsValue(x : MongoDBObject) : Map[String, JsValue] = {
		Map("comment_id" -> toJson(x.getAs[String]("comment_id").get),
			"content" -> toJson(x.getAs[String]("content").get),
			"owner_id" -> toJson(x.getAs[String]("owner_id").get),
			"date" -> toJson(x.getAs[Number]("date").get.longValue),
			"likes" -> toJson(x.getAs[Number]("likes").get.intValue))
	}
}