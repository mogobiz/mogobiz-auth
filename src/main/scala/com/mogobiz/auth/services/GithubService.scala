/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.auth.services

import akka.util.Timeout
import com.mogobiz.auth.api.Github2Api
import com.mogobiz.auth.Settings
import com.mogobiz.session.SessionESDirectives._
import org.scribe.builder.ServiceBuilder
import org.scribe.model.{OAuthRequest, Verb, Verifier}
import spray.http.StatusCode._
import spray.http.StatusCodes
import spray.routing.Directives

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class GithubService(implicit executionContext: ExecutionContext) extends Directives {
  implicit val timeout = Timeout(10.seconds)

  val route = pathPrefix("oauth") {
    pathPrefix("github") {
      signin ~ callback
    }
  }

  def buildService() =
    new ServiceBuilder()
      .provider(classOf[Github2Api])
      .apiKey(Settings.Github.ConsumerKey)
      .apiSecret(Settings.Github.ConsumerSecret)
      .callback(Settings.Github.Callback)
      .scope(Settings.Github.Scope)
      .build()

  lazy val signin = path("signin") {
    get {
      val service = buildService()
      session { session =>
        val authURL = service.getAuthorizationUrl(null)
        redirect(authURL, StatusCodes.TemporaryRedirect)
      }
    }
  }

  lazy val callback = path("callback") {
    get {
      session { session =>
        parameters('code) { code =>
          val service     = buildService()
          val verifier    = new Verifier(code)
          val accessToken = service.getAccessToken(null, verifier)
          val ResourceUrl = Settings.Github.ResourceUrl
          val request     = new OAuthRequest(Verb.GET, ResourceUrl)
          service.signRequest(accessToken, request)
          val response = request.send()
          if (response.getCode == StatusCodes.OK.intValue) {
            complete {
              response.getBody
            }
          } else {
            complete(int2StatusCode(response.getCode))
          }
        }
      }
    }
  }
}
