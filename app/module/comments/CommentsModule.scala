package module.comments

import pattern.ModuleTrait
import play.api.libs.json.JsValue
import soundtrackmessages.MessageDefines
import CommentsMessages._

import util.dao.from
import util.dao._data_connection
import util.errorcode.ErrorCode
import com.mongodb.casbah.Imports._

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
		null
	}
	
	def queryTopComments(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def queryComments(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def updateComment(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def popComment(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def likeComment(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def Js2DBObject(data : JsValue) : MongoDBObject = {
		null
	}
	
	def DB2JsValue(x : MongoDBObject) : Map[String, JsValue] = {
		null
	}
}