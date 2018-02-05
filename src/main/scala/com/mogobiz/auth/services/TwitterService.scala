/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.auth.services

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import com.mogobiz.auth.Settings
import com.mogobiz.session.SessionESDirectives._
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.TwitterApi
import org.scribe.model.{OAuthRequest, Token, Verb, Verifier}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class TwitterService(implicit executionContext: ExecutionContext)
    extends Directives {
  implicit val timeout = Timeout(10.seconds)

  val route = pathPrefix("oauth") {
    pathPrefix("twitter") {
      signin ~ callback
    }
  }

  def buildService() =
    new ServiceBuilder()
      .provider(classOf[TwitterApi.Authenticate])
      .apiKey(Settings.Twitter.ConsumerKey)
      .apiSecret(Settings.Twitter.ConsumerSecret)
      .callback(Settings.Twitter.Callback)
      .build()

  lazy val signin = get {
    path("signin") {
      session { session =>
        val service = buildService()
        val requestToken = service.getRequestToken()
        val authURL = service.getAuthorizationUrl(requestToken)
        setSession(
          session += "oauthToken" -> requestToken.getToken += "oauthSecret" -> requestToken.getSecret) {
          redirect(authURL, StatusCodes.TemporaryRedirect)
        }
      }
    }
  }

  lazy val callback = get {
    path("callback") {
      session { session =>
        val token = session("oauthToken").toString
        val secret = session("oauthSecret").toString
        parameters('oauth_verifier, 'oauth_token.?) {
          (oauth_verifier, oauth_token) =>
            val service = buildService()
            val verifier = new Verifier(oauth_verifier)
            val requestToken = new Token(token, secret)
            val accessToken = service.getAccessToken(requestToken, verifier)
            val params = accessToken.getRawResponse
              .split('&')
              .map { kv =>
                val kvArray = kv.split('=')
              (kvArray(0), kvArray(1))
            } toMap
            val ResourceUrl = Settings.Twitter.ResourceUrl + s"screen_name=${params("screen_name")}"
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
