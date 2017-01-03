package module.admin

import play.api.libs.json.JsValue
import soundtrackmessages.CommonMessage

abstract class msg_AdminCommand extends CommonMessage

/**
  * Created by BM on 03/01/2017.
  */
object AdminMessages {
	case class msg_AdminLogin(data : JsValue) extends msg_AdminCommand
//	case class msg_pushAdminUser(data : JsValue) extends msg_AdminCommand
//	case class msg_popAdminUser(data : JsValue) extends msg_AdminCommand
//	case class msg_queryAdminUser(data : JsValue) extends msg_AdminCommand
}
