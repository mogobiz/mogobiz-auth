/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.auth.api

import org.scribe.builder.api.DefaultApi20
import org.scribe.model.OAuthConfig
import org.scribe.utils.{OAuthEncoder, Preconditions}

class Github2Api extends DefaultApi20 {
  val AUTHORIZE_URL: String        = "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s"
  val SCOPED_AUTHORIZE_URL: String = AUTHORIZE_URL + "&scope=%s"

  override def getAccessTokenEndpoint(): String = "https://github.com/login/oauth/access_token"

  override def getAuthorizationUrl(config: OAuthConfig): String = {
    Preconditions
      .checkValidUrl(config.getCallback(), "Must provide a valid url as callback. Github does not support OOB");
    // Append scope if present
    if (config.hasScope()) {
      String.format(SCOPED_AUTHORIZE_URL,
                    config.getApiKey(),
                    OAuthEncoder.encode(config.getCallback()),
                    OAuthEncoder.encode(config.getScope()));
    } else {
      String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }
  }
}
