/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.auth.services

import akka.util.Timeout
import com.mogobiz.auth.Settings
import com.mogobiz.session.SessionESDirectives._
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.LinkedInApi
import org.scribe.model.{OAuthRequest, Token, Verb, Verifier}
import spray.http.StatusCodes
import spray.routing.Directives

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import spray.http.StatusCode._

class LinkedInService(implicit executionContext: ExecutionContext) extends Directives {
  implicit val timeout = Timeout(10.seconds)

  val route = pathPrefix("oauth") {
    pathPrefix("linkedin") {
      signin ~ callback
    }
  }

  def buildService() = new ServiceBuilder()
    .provider(LinkedInApi.withScopes(Settings.LinkedIn.Scope.split(','): _*))
    .apiKey(Settings.LinkedIn.ConsumerKey)
    .apiSecret(Settings.LinkedIn.ConsumerSecret)
    .callback(Settings.LinkedIn.Callback)
    .build()

  lazy val signin = path("signin") {
    get {
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

  lazy val callback = path("callback") {
    get {
      session {
        session =>
          val token = session("oauthToken").toString
          val secret = session("oauthSecret").toString
          parameters('oauth_verifier.?, 'oauth_token.?) { (oauth_verifier, oauth_token) =>
            if (oauth_verifier.isDefined) {
              val service = buildService()
              val verifier = new Verifier(oauth_verifier.get)
              val requestToken = new Token(token, secret)
              val accessToken = service.getAccessToken(requestToken, verifier)
              println(accessToken.getRawResponse)
              val ResourceUrl = Settings.LinkedIn.ResourceUrl
              val request = new OAuthRequest(Verb.GET, ResourceUrl)
              service.signRequest(accessToken, request)
              val response = request.send()
              if (response.getCode == StatusCodes.OK.intValue) {
                complete {
                  response.getBody
                }
              }
              else {
                complete(int2StatusCode(response.getCode))
              }
            }
            else {
              complete(StatusCodes.Unauthorized)
            }
          }
      }
    }
  }
}
