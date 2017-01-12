package module.wechatpay

import pattern.ModuleTrait
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import soundtrackmessages.MessageDefines
import util.dao.from
import util.dao._data_connection
import util.errorcode.ErrorCode
import com.mongodb.casbah.Imports._
import java.util.Date

import module.common.http._
import module.sercurity.Sercurity
import module.wechatpay.WechatPayMessages._

object PayResultDefine {
	object unpaid extends pay_result(0, "unpaid")
	object paid extends pay_result(1, "paid")
}

sealed case class pay_result(t : Int, d : String)

object WechatPayModule extends ModuleTrait {
	/**
	  * wechat app id
	  */
	val app_id = "wx86135fc9a5e67ff3"
	val app_secret = "74f13dbccc1b8323aadebda15d35144d"

	/**
	  * wechat business id
	  */
	val mch_id = "1364881402"
	val mch_key = "whq628318whq628318whq628318whq62"
	val pay_noncestr = "b927722419c52622651a871d1d9ed8b2"
	val pay_body = "会员年费"
	val pay_notify = "http://wxpay.weixin.qq.com/pub_v2/pay/notify.php"

	val weixin_http = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + app_id + "&secret=" + app_secret
	val weixin_jsapi = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token="
	val weixin_userinfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN"

	def dispatchMsg(msg: MessageDefines)(pr: Option[Map[String, JsValue]]): (Option[Map[String, JsValue]], Option[JsValue]) = msg match {
		case msg_requestPrepayId(data) => requestPrepayId(data)
		case msg_queryAllOrders(data) => queryAllOrders(data)

		case _ => ???
	}

	def queryAllOrders(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val take = (data \ "take").asOpt[Int].map (x => x).getOrElse(10)
			val skip = (data \ "skip").asOpt[Int].map (x => x).getOrElse(0)

			val result = (from db() in "orders").selectSkipTop(skip)(take)("date")(DB2JsValue(_)).toList
			(Some(Map("result" -> toJson(result))), None)
		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def requestPrepayId(data : JsValue) : (Option[Map[String, JsValue]], Option[JsValue]) = {
		try {
			val openid = (data \ "openid").asOpt[String].get
			val fee = (data \ "fee").asOpt[Int].map (x => x).getOrElse(1)
			val timespan = java.lang.Long.toString(System.currentTimeMillis() / 1000)// (new Date().getTime / 1000).toString
			/**
			  * get uni order, prepay_id
			  */
			val trade_no = module.sercurity.Sercurity.md5Hash(openid + timespan)

			val str_pay = "appid=" + app_id + "&body=" + pay_body + "&mch_id=" + mch_id + "&nonce_str=" + pay_noncestr + "&notify_url="+ pay_notify + "&openid=" + openid + "&out_trade_no=" + trade_no + "&spbill_create_ip=127.0.0.1&total_fee=" + fee + "&trade_type=JSAPI&key=" + mch_key
			val str_md5 = module.sercurity.Sercurity.md5Hash(str_pay).toUpperCase
			val valxml = """<xml><appid>%s</appid><body><![CDATA[%s]]></body><mch_id>%s</mch_id><nonce_str>%s</nonce_str><notify_url>%s</notify_url><openid>%s</openid><out_trade_no>%s</out_trade_no><spbill_create_ip>127.0.0.1</spbill_create_ip><total_fee>%d</total_fee><trade_type>JSAPI</trade_type><sign><![CDATA[%s]]></sign></xml>"""
					.format(app_id, pay_body, mch_id, pay_noncestr, pay_notify, openid, trade_no, fee, str_md5)

			val order_url = "https://api.mch.weixin.qq.com/pay/unifiedorder"
			val result = ((HTTP(order_url)).post(valxml.toString))

			val tag = "prepay_id"
			var prepay_id = result.substring(result.indexOf(tag) + tag.length + 1, result.indexOf("</" + tag))
			if (prepay_id.startsWith("<![CDATA[") && prepay_id.endsWith("]]>"))
				prepay_id = prepay_id.substring(9, prepay_id.length - 3)

			val pay_str = "appId=" + app_id + "&nonceStr=" + pay_noncestr + "&package=prepay_id=" + prepay_id + "&signType=MD5&timeStamp=" + timespan + "&key=" + mch_key
			val pay_str_md5 = module.sercurity.Sercurity.md5Hash(pay_str).toUpperCase

			Js2DBObject(openid, pay_body, fee, prepay_id, trade_no, timespan.toLong)

			(Some(Map("prepay_id" -> toJson(prepay_id),
				"out_trade_no" -> toJson(trade_no), "pay_sign" -> toJson(pay_str_md5),
				"timespan" -> toJson(timespan))), None)

		} catch {
			case ex : Exception => (None, Some(ErrorCode.errorToJson(ex.getMessage)))
		}
	}

	def Js2DBObject(openid : String, des : String, fee : Int, prepay_id : String, trade_no : String, date : Long) = {
		val builder = MongoDBObject.newBuilder

		builder += "openid" -> openid
		builder += "description" -> des
		builder += "fee" -> fee
		builder += "date" -> date
		builder += "pay_result" -> PayResultDefine.unpaid.t
		builder += "pay_date" -> 0.toLong

		_data_connection.getCollection("orders") += builder.result
	}

	def DB2JsValue(x : MongoDBObject) : Map[String, JsValue] = {
		Map("openid" -> toJson(x.getAs[String]("openid").get),
			"description" -> toJson(x.getAs[String]("description").get),
			"fee" -> toJson(x.getAs[Number]("fee").get.intValue),
			"date" -> toJson(x.getAs[Number]("date").get.longValue),
			"pay_result" -> toJson(x.getAs[Number]("pay_result").get.intValue),
			"pay_date" -> toJson(x.getAs[Number]("pay_date").get.longValue))
	}
}
