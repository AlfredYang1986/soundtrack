package controllers.ajax

import play.api._
import play.api.mvc._

import controllers.common.requestArgsQuery._

import soundtrackmessages.MessageRoutes
import pattern.ResultMessage.msg_CommonResultMessage

import module.comments.CommentsMessages._

object CommentsController extends Controller {
	def pushComment = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_pushComment(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def popComment = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.common_result
		MessageRoutes(msg_popComment(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def queryTopComments = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.lst_result
		MessageRoutes(msg_queryTopComments(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def queryComments = Action (request => requestArgsV2(request) { jv => 
		import pattern.ResultMessage.lst_result
		MessageRoutes(msg_queryComments(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def updataComment = Action (request => requestArgsV2(request) { jv =>
		import pattern.ResultMessage.lst_result
		MessageRoutes(msg_updateComment(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
	def likeComment = Action (request => requestArgsV2(request) { jv =>
		import pattern.ResultMessage.lst_result
		MessageRoutes(msg_likeComment(jv) :: msg_CommonResultMessage() :: Nil, None)
	})
}