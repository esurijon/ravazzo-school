package com.aulatec.push

import play.api.libs.json.JsObject
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.Writes

case class GcmResult(
  message_id: Option[String],
  registration_id: Option[String],
  error: String)

object GcmResult {
  implicit val gcmResponseReads = Json.reads[GcmResult]
}

case class GcmResponse(
  multicast_id: Long,
  success: Int,
  failure: Int,
  canonical_ids: Int,
  results: Option[List[GcmResult]])

object GcmResponse {
  implicit val gcmResponseReads = Json.reads[GcmResponse]
}

