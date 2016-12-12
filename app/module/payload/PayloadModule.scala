package module.payload

import pattern.ModuleTrait
import play.api.libs.json.JsValue
import soundtrackmessages.MessageDefines
import PayloadMessages._

import util.dao.from
import util.dao._data_connection
import util.errorcode.ErrorCode
import com.mongodb.casbah.Imports._

object PayloadModule {

	def dispatchMsg(msg : MessageDefines)(pr : Option[Map[String, JsValue]]) : (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_pushPayload(data) => pushPayload(data)
		case msg_queryPayload(data) => queryPayload(data)
		case msg_queryMutiplePayload(data) => queryMultiPayload(data)
		case msg_updatePayload(data) => updatePayload(data)
		case msg_popPayload(data) => popPayload(data)
		
		case _ => ???
	}
	
	def pushPayload(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def queryPayload(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def queryMultiPayload(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def updatePayload(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def popPayload(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		null
	}
	
	def Js2DBObject(data : JsValue) : MongoDBObject = {
		null
	}
	
	def DB2JsValue(x : MongoDBObject) : Map[String, JsValue] = {
		null
	}
}