package controllers.ajax

import play.api._
import play.api.mvc._

import controllers.common.requestArgsQuery._

import soundtrackmessages.MessageRoutes
import pattern.ResultMessage.msg_CommonResultMessage

import module.auth.AuthMessages._

object AuthController extends Controller {
	def authWithWechat = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_authWithWechat(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def authQueryUser = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_queryUser(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def authUpdateUser = Action (request => requestArgsV2(request) { jv =>
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_authUpdateUser(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
}