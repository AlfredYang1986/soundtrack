package controllers.ajax

import play.api._
import play.api.mvc._
import controllers.common.requestArgsQuery._
import module.systemnotify.SystemNotifyMessages.msg_popSystemNotify
import soundtrackmessages.MessageRoutes
import pattern.ResultMessage.msg_CommonResultMessage
import module.wechatpay.WechatPayMessages._

object WechatPayController extends Controller {
	def requestPrepayId = Action (request => requestArgsV2(request) { jv =>
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_requestPrepayId(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
}
