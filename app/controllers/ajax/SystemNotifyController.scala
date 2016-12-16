package controllers.ajax

import play.api._
import play.api.mvc._

import controllers.common.requestArgsQuery._

import soundtrackmessages.MessageRoutes
import pattern.ResultMessage.msg_CommonResultMessage

import module.systemnotify.SystemNotifyMessages._

object SystemNotifyController extends Controller {
	def pushSysNot = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_pushSystemNotify(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def updateSysNot = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_updateSystemNotify(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def querySysNot = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_querySystemNotify(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def searchSysNot = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.lst_result
		MessageRoutes(msg_queryMultipleSystemNotify(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def popSysNot = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_popSystemNotify(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
}