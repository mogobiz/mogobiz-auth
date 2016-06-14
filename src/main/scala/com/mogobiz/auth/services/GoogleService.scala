/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.auth.services

import akka.http.scaladsl.model.{ StatusCode, StatusCodes }
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import com.mogobiz.auth.Settings
import com.mogobiz.auth.api.Google2Api
import com.typesafe.scalalogging.StrictLogging
import org.scribe.builder.ServiceBuilder
import org.scribe.exceptions.OAuthException
import org.scribe.model._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class GoogleService(implicit executionContext: ExecutionContext) extends Directives with StrictLogging {
  implicit val timeout = Timeout(10.seconds)

  val route = pathPrefix("oauth") {
    pathPrefix("google") {
      signin ~ callback
    }
  }

  def buildService() = new ServiceBuilder()
    .provider(classOf[Google2Api])
    .apiKey(Settings.Google.ConsumerKey)
    .apiSecret(Settings.Google.ConsumerSecret)
    .callback(Settings.Google.Callback)
    .scope(Settings.Google.Scope)
    .build()

  val api = new Google2Api()

  def getAccessToken(requestToken: Token, verifier: Verifier): Token = {
    import com.mogobiz.json.Implicits._
    val request = new OAuthRequest(Verb.POST, api.getAccessTokenEndpoint())
    request.addBodyParameter(OAuthConstants.CLIENT_ID, Settings.Google.ConsumerKey)
    request.addBodyParameter(OAuthConstants.CLIENT_SECRET, Settings.Google.ConsumerSecret)
    request.addBodyParameter(OAuthConstants.CODE, verifier.getValue())
    request.addBodyParameter(OAuthConstants.REDIRECT_URI, Settings.Google.Callback)
    request.addBodyParameter("grant_type", "authorization_code")
    request.addBodyParameter(OAuthConstants.SCOPE, Settings.Google.Scope)
    logger.debug(request.getBodyContents)
    val response = request.send()
    val accessData = serialization.read[Map[String, String]](response.getBody)
    val accessToken = accessData.get("access_token")
    new Token(accessToken.getOrElse(throw new OAuthException("Cannot extract an access token. Response was: " + response.getBody)), "", response.getBody)
  }

  lazy val signin = path("signin") {
    get {
      val service = buildService()
      val authURL = service.getAuthorizationUrl(null)
      redirect(authURL, StatusCodes.TemporaryRedirect)
    }
  }

  lazy val callback = path("callback") {
    get {
      parameters('code) { code =>
        val service = buildService()
        val verifier = new Verifier(code)
        val accessToken = getAccessToken(null, verifier)
        logger.debug(accessToken.getRawResponse)
        val ResourceUrl = Settings.Google.ResourceUrl
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
