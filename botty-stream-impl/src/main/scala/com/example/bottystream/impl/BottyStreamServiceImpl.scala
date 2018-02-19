package com.example.bottystream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.example.bottystream.api.BottyStreamService
import com.example.botty.api.BottyService

import scala.concurrent.Future

/**
  * Implementation of the BottyStreamService.
  */
class BottyStreamServiceImpl(bottyService: BottyService) extends BottyStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(bottyService.hello(_).invoke()))
  }
}
