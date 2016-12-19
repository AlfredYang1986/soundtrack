package controllers.ajax

import play.api._
import play.api.mvc._

import controllers.common.requestArgsQuery._

import soundtrackmessages.MessageRoutes
import pattern.ResultMessage.msg_CommonResultMessage

import module.payload.PayloadMessages._

object PayloadController extends Controller {
	def pushPayload = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_pushPayload(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def queryPayload = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_queryPayload(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def searchPayload = Action (request => requestArgsV2(request) { jv =>
		import pattern.ResultMessage.lst_result
		MessageRoutes(msg_queryMutiplePayload(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def updatePayload = Action (request => requestArgsV2(request) { jv =>
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_updatePayload(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def popPayload = Action (request => requestArgsV2(request) { jv =>
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_popPayload(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
}