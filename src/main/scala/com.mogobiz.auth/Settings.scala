package com.mogobiz.auth


import com.typesafe.config.ConfigFactory

object Settings {
  private val config = ConfigFactory.load("auth")

  object Twitter {
    val Callback = config.getString("oauth.twitter.callback")
    val ConsumerKey = config.getString("oauth.twitter.consumer.key")
    val ConsumerSecret = config.getString("oauth.twitter.consumer.secret")
    val Scope = config.getString("oauth.twitter.scope")
    val ResourceUrl = config.getString("oauth.twitter.resource.url")
  }

  object LinkedIn {
    val Callback = config.getString("oauth.linkedin.callback")
    val ConsumerKey = config.getString("oauth.linkedin.consumer.key")
    val ConsumerSecret = config.getString("oauth.linkedin.consumer.secret")
    val Scope = config.getString("oauth.linkedin.scope")
    val ResourceUrl = config.getString("oauth.linkedin.resource.url")
  }

  object Google {
    val Callback = config.getString("oauth.google.callback")
    val ConsumerKey = config.getString("oauth.google.consumer.key")
    val ConsumerSecret = config.getString("oauth.google.consumer.secret")
    val Scope = config.getString("oauth.google.scope")
    val ResourceUrl = config.getString("oauth.google.resource.url")
  }

  object Facebook {
    val Callback = config.getString("oauth.facebook.callback")
    val ConsumerKey = config.getString("oauth.facebook.consumer.key")
    val ConsumerSecret = config.getString("oauth.facebook.consumer.secret")
    val Scope = config.getString("oauth.facebook.scope")
    val ResourceUrl = config.getString("oauth.facebook.resource.url")

  }

  object Github {
    val Callback = config.getString("oauth.github.callback")
    val ConsumerKey = config.getString("oauth.github.consumer.key")
    val ConsumerSecret = config.getString("oauth.github.consumer.secret")
    val Scope = config.getString("oauth.github.scope")
    val ResourceUrl = config.getString("oauth.github.resource.url")
  }

  object Yahoo {
    val Callback = config.getString("oauth.yahoo.callback")
    val ConsumerKey = config.getString("oauth.yahoo.consumer.key")
    val ConsumerSecret = config.getString("oauth.yahoo.consumer.secret")
    val Scope = config.getString("oauth.yahoo.scope")
    val ResourceUrl = config.getString("oauth.yahoo.resource.url")
  }
}

