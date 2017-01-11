package module.systemnotify

import play.api.libs.json.JsValue
import soundtrackmessages.CommonMessage

abstract class msg_SystemNotifyCommand extends CommonMessage

object SystemNotifyMessages {
	case class msg_pushSystemNotify(data : JsValue) extends msg_SystemNotifyCommand
	case class msg_querySystemNotify(data : JsValue) extends msg_SystemNotifyCommand
	case class msg_queryMultipleSystemNotify(data : JsValue) extends msg_SystemNotifyCommand
	case class msg_updateSystemNotify(data : JsValue) extends msg_SystemNotifyCommand
	case class msg_popSystemNotify(data : JsValue) extends msg_SystemNotifyCommand
	case class msg_allSystemNotify(data : JsValue) extends msg_SystemNotifyCommand
}