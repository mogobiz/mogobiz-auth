/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.auth

import com.typesafe.config.ConfigFactory

object Settings {
  private val config = ConfigFactory.load("auth").withFallback(ConfigFactory.load("default-auth"))

  object Twitter {
    val Callback: String       = config.getString("oauth.twitter.callback")
    val ConsumerKey: String    = config.getString("oauth.twitter.consumer.key")
    val ConsumerSecret: String = config.getString("oauth.twitter.consumer.secret")
    val Scope: String          = config.getString("oauth.twitter.scope")
    val ResourceUrl: String    = config.getString("oauth.twitter.resource.url")
  }

  object LinkedIn {
    val Callback: String       = config.getString("oauth.linkedin.callback")
    val ConsumerKey: String    = config.getString("oauth.linkedin.consumer.key")
    val ConsumerSecret: String = config.getString("oauth.linkedin.consumer.secret")
    val Scope: String          = config.getString("oauth.linkedin.scope")
    val ResourceUrl: String    = config.getString("oauth.linkedin.resource.url")
  }

  object Google {
    val Callback: String       = config.getString("oauth.google.callback")
    val ConsumerKey: String    = config.getString("oauth.google.consumer.key")
    val ConsumerSecret: String = config.getString("oauth.google.consumer.secret")
    val Scope: String          = config.getString("oauth.google.scope")
    val ResourceUrl: String    = config.getString("oauth.google.resource.url")
  }

  object Facebook {
    val Callback: String       = config.getString("oauth.facebook.callback")
    val ConsumerKey: String    = config.getString("oauth.facebook.consumer.key")
    val ConsumerSecret: String = config.getString("oauth.facebook.consumer.secret")
    val Scope: String          = config.getString("oauth.facebook.scope")
    val ResourceUrl: String    = config.getString("oauth.facebook.resource.url")

  }

  object Github {
    val Callback: String       = config.getString("oauth.github.callback")
    val ConsumerKey: String    = config.getString("oauth.github.consumer.key")
    val ConsumerSecret: String = config.getString("oauth.github.consumer.secret")
    val Scope: String          = config.getString("oauth.github.scope")
    val ResourceUrl: String    = config.getString("oauth.github.resource.url")
  }

  object Yahoo {
    val Callback: String       = config.getString("oauth.yahoo.callback")
    val ConsumerKey: String    = config.getString("oauth.yahoo.consumer.key")
    val ConsumerSecret: String = config.getString("oauth.yahoo.consumer.secret")
    val Scope: String          = config.getString("oauth.yahoo.scope")
    val ResourceUrl: String    = config.getString("oauth.yahoo.resource.url")
  }

}
