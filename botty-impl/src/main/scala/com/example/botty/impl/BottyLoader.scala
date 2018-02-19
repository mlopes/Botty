package com.example.botty.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.example.botty.api.BottyService
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.softwaremill.macwire._

class BottyLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new BottyApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new BottyApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[BottyService])
}

abstract class BottyApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[BottyService](wire[BottyServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = BottySerializerRegistry

  // Register the Botty persistent entity
  persistentEntityRegistry.register(wire[BottyEntity])
}
