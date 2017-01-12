package module.wechatpay

import play.api.libs.json.JsValue
import soundtrackmessages.CommonMessage

abstract class msg_WechatPayCommand extends CommonMessage

object WechatPayMessages {
	case class msg_requestPrepayId(data : JsValue) extends msg_WechatPayCommand
	case class msg_queryAllOrders(data : JsValue) extends msg_WechatPayCommand
}
