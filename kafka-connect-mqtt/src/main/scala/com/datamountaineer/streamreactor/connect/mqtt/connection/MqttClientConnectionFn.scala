/*
 * Copyright 2017 Datamountaineer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datamountaineer.streamreactor.connect.mqtt.connection

import com.datamountaineer.streamreactor.connect.mqtt.config.{ MqttSinkSettings, MqttSourceSettings}
import com.datamountaineer.streamreactor.connect.mqtt.source.MqttSSLSocketFactory
import com.typesafe.scalalogging.slf4j.StrictLogging
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.eclipse.paho.client.mqttv3.{MqttCallback, MqttClient, MqttConnectOptions}

object MqttClientConnectionFn extends StrictLogging {
  def apply(callback: MqttCallback)(implicit settings: MqttSourceSettings): MqttClient = {
    {
      val options = buildBaseClient(settings.connectionTimeout,
                                    settings.keepAliveInterval,
                                    settings.cleanSession,
                                    settings.password,
                                    settings.sslCertFile,
                                    settings.sslCACertFile,
                                    settings.sslCertKeyFile,
                                    settings.user
                                  )

      val c = new MqttClient(settings.connection, settings.clientId, new MemoryPersistence())
      c.setCallback(callback)

      logger.info(s"Connecting to ${settings.connection}")
      c.connect(options)
      logger.info(s"Connected to ${settings.connection} as ${settings.clientId}")
      c
    }
  }

  def apply(settings: MqttSinkSettings) : MqttClient = {
    val options = buildBaseClient(settings.connectionTimeout,
                                  settings.keepAliveInterval,
                                  settings.cleanSession,
                                  settings.password,
                                  settings.sslCertFile,
                                  settings.sslCACertFile,
                                  settings.sslCertKeyFile,
                                  settings.user
                                )

    val c = new MqttClient(settings.connection, settings.clientId, new MemoryPersistence())
    logger.info(s"Connecting to ${settings.connection}")
    c.connect(options)
    logger.info(s"Connected to ${settings.connection} as ${settings.clientId}")
    c
  }

  def buildBaseClient(connectionTimeout: Int,
                      keepAliveInterval: Int,
                      cleanSession: Boolean,
                      password: Option[String],
                      sslCertFile: Option[String],
                      sslCACertFile: Option[String],
                      sslCertKeyFile: Option[String],
                      user: Option[String]
                     ) : MqttConnectOptions = {
    val options = new MqttConnectOptions()
    options.setConnectionTimeout(connectionTimeout)
    options.setKeepAliveInterval(keepAliveInterval)
    options.setCleanSession(cleanSession)
    password.foreach(p => options.setPassword(p.toCharArray))
    options.setAutomaticReconnect(true)
    if(user.isDefined)
    {
        options.setUserName(user.get)
    }

    sslCertFile.foreach { _ =>
      options.setSocketFactory(
        MqttSSLSocketFactory(sslCACertFile.get, sslCertFile.get, sslCertKeyFile.get, "")
      )
    }

    options
  }
}
