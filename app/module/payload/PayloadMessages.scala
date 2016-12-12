package module.payload

import play.api.libs.json.JsValue
import soundtrackmessages.CommonMessage

abstract class msg_PayloadCommand extends CommonMessage

object PayloadMessages {
	case class msg_pushPayload(data : JsValue) extends msg_PayloadCommand
	case class msg_queryPayload(data : JsValue) extends msg_PayloadCommand
	case class msg_queryMutiplePayload(data : JsValue) extends msg_PayloadCommand
	case class msg_updatePayload(data : JsValue) extends msg_PayloadCommand
	case class msg_popPayload(data : JsValue) extends msg_PayloadCommand
}