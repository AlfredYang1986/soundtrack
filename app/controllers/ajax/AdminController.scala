package controllers.ajax

import play.api._
import play.api.mvc._

import controllers.common.requestArgsQuery._

import soundtrackmessages.MessageRoutes
import pattern.ResultMessage.msg_CommonResultMessage

import module.admin.AdminMessages._

object AdminController extends Controller {
	def AdminLogin = Action (request => requestArgsV2(request) { jv =>
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_AdminLogin(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
}