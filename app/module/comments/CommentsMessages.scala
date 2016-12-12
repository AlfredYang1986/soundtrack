package module.comments

import play.api.libs.json.JsValue
import soundtrackmessages.CommonMessage

abstract class msg_CommentsCommand extends CommonMessage

object CommentsMessages {
	case class msg_pushComment(data : JsValue) extends msg_CommentsCommand
	case class msg_queryTopComments(data : JsValue) extends msg_CommentsCommand
	case class msg_queryComments(data : JsValue) extends msg_CommentsCommand
	case class msg_updateComment(data : JsValue) extends msg_CommentsCommand
	case class msg_popComment(data : JsValue) extends msg_CommentsCommand
	
	case class msg_likeComment(data : JsValue) extends msg_CommentsCommand
}