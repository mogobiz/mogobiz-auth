/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.auth.services

import akka.http.scaladsl.model.{ StatusCode, StatusCodes }
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import com.mogobiz.auth.Settings
import com.mogobiz.session.SessionESDirectives._
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.YahooApi
import org.scribe.model.{ OAuthRequest, Token, Verb, Verifier }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

// TODO Not ready yet
class YahooService(implicit executionContext: ExecutionContext) extends Directives {
  implicit val timeout = Timeout(10.seconds)

  val route = pathPrefix("oauth") {
    pathPrefix("yahoo") {
      signin ~ callback
    }
  }

  def buildService() = new ServiceBuilder()
    .provider(classOf[YahooApi])
    .apiKey(Settings.Yahoo.ConsumerKey)
    .apiSecret(Settings.Yahoo.ConsumerSecret)
    .callback(Settings.Yahoo.Callback)
    .debug()
    .build()

  lazy val signin = get {
    path("signin") {
      session { session =>
        val service = buildService()
        val requestToken = service.getRequestToken()
        val authURL = service.getAuthorizationUrl(requestToken)
        setSession(session += "oauthToken" -> requestToken.getToken += "oauthSecret" -> requestToken.getSecret) {
          redirect(authURL, StatusCodes.TemporaryRedirect)
        }
      }
    }
  }

  lazy val callback = get {
    path("callback") {
      session {
        session =>
          val token = session("oauthToken").toString
          val secret = session("oauthSecret").toString
          parameters('oauth_verifier, 'oauth_token.?) { (oauth_verifier, oauth_token) =>
            val service = buildService()
            val verifier = new Verifier(oauth_verifier)
            val requestToken = new Token(token, secret)
            val accessToken = service.getAccessToken(requestToken, verifier)
            val ResourceUrl = Settings.Yahoo.ResourceUrl
            val request = new OAuthRequest(Verb.GET, ResourceUrl)
            service.signRequest(accessToken, request)
            val response = request.send()
            if (response.getCode == StatusCodes.OK.intValue) {
              complete {
                response.getBody
              }
            } else {
              complete(StatusCode.int2StatusCode(response.getCode))
            }
          }
      }
    }
  }
}
