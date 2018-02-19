package com.example.bottystream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.example.bottystream.api.BottyStreamService
import com.example.botty.api.BottyService
import com.softwaremill.macwire._

class BottyStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new BottyStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new BottyStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[BottyStreamService])
}

abstract class BottyStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[BottyStreamService](wire[BottyStreamServiceImpl])

  // Bind the BottyService client
  lazy val bottyService = serviceClient.implement[BottyService]
}
