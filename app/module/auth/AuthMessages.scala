package module.auth

import play.api.libs.json.JsValue
import soundtrackmessages.CommonMessage

abstract class msg_AuthCommand extends CommonMessage

object AuthMessages {
	case class msg_authWithWechat(data : JsValue) extends msg_AuthCommand
	case class msg_authCreateUser(data : JsValue) extends msg_AuthCommand
	case class msg_authCheck(data : JsValue) extends msg_AuthCommand
	case class msg_queryUser(data : JsValue) extends msg_AuthCommand
	case class msg_queryMultipleUsers(data : JsValue) extends msg_AuthCommand
	case class msg_queryUserByToken(data : JsValue) extends msg_AuthCommand
	case class msg_authUpdateUser(data : JsValue) extends msg_AuthCommand
}