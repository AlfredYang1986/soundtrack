package pattern

import scala.concurrent.duration._

import akka.actor.Actor
import akka.actor.ActorContext
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import soundtrackmessages._
import play.api.Application
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson

import module.auth.{ msg_AuthCommand, AuthModule }
import module.payload.{ msg_PayloadCommand, PayloadModule }
import module.comments.{ msg_CommentsCommand, CommentsModule }
import module.systemnotify.{ msg_SystemNotifyCommand, SystemNotifyModule }
import module.admin.{ msg_AdminCommand, AdminModule }

object PipeFilterActor {
	def prop(originSender : ActorRef, msr : MessageRoutes) : Props = {
		Props(new PipeFilterActor(originSender, msr))
	}
}

class PipeFilterActor(originSender : ActorRef, msr : MessageRoutes) extends Actor with ActorLogging {
	
	def dispatchImpl(cmd : CommonMessage, module : ModuleTrait) = {
		tmp = Some(true)
		module.dispatchMsg(cmd)(rst) match {
			case (_, Some(err)) => {
				originSender ! error(err)
				cancelActor					
			}
			case (Some(r), _) => {
				rst = Some(r) 
			}
			case _ => println("never go here")
		}
		rstReturn
	}
	
	var tmp : Option[Boolean] = None
	var rst : Option[Map[String, JsValue]] = msr.rst
	var next : ActorRef = null
	def receive = {
		case cmd : msg_AuthCommand => dispatchImpl(cmd, AuthModule)
		case cmd : msg_PayloadCommand => dispatchImpl(cmd, PayloadModule)
		case cmd : msg_CommentsCommand => dispatchImpl(cmd, CommentsModule)
		case cmd : msg_SystemNotifyCommand => dispatchImpl(cmd, SystemNotifyModule)
		case cmd : msg_ResultCommand => dispatchImpl(cmd, ResultModule)
		case cmd : msg_AdminCommand => dispatchImpl(cmd, AdminModule)
		case cmd : ParallelMessage => {
		    cancelActor
			next = context.actorOf(ScatterGatherActor.prop(originSender, msr), "scat")
			next ! cmd
		}
		case timeout() => {
			originSender ! new timeout
			cancelActor
		}
	 	case x : AnyRef => println(x); ???
	}
	
	val timeOutSchdule = context.system.scheduler.scheduleOnce(2 second, self, new timeout)

	def rstReturn = tmp match {
		case Some(_) => { rst match {
			case Some(r) => 
				msr.lst match {
					case Nil => {
						originSender ! result(toJson(r))
					}
					case head :: tail => {
						head match {
							case p : ParallelMessage => {
								next = context.actorOf(ScatterGatherActor.prop(originSender, MessageRoutes(tail, rst)), "scat")
								next ! p
							}
							case c : CommonMessage => {
								next = context.actorOf(PipeFilterActor.prop(originSender, MessageRoutes(tail, rst)), "pipe")
								next ! c
							}
						}
					}
					case _ => println("msr error")
				}
				cancelActor
			case _ => Unit
		}}
		case _ => println("never go here"); Unit
	}
	
	def cancelActor = {
		timeOutSchdule.cancel
//		context.stop(self)
	}
}