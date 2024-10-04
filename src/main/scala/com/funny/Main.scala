package com.funny

import org.apache.pekko
import pekko.actor.typed.ActorSystem
import pekko.actor.typed.scaladsl.Behaviors
import pekko.http.scaladsl.Http
import pekko.http.scaladsl.server.Route

import scala.util.{Failure, Success}

object Main {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext
    val futureBinding = Http().newServerAt("localhost", 9090).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val orderRegistryActor = context.spawn(OrderRegistry(), "RegistryActor")
      context.watch(orderRegistryActor)

      val routes = new OrderRoutes(orderRegistryActor)(context.system)
      startHttpServer(routes.orderRoutes)(context.system)

      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "HelloPekkoHttpServer")
  }
}
