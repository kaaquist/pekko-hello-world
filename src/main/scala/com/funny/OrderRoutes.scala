package com.funny

import com.funny.OrderRegistry.{ActionPerformed, CreateItem, DeleteItem, GetItem, GetItemResponse, GetItems}
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.AskPattern._
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.util.Timeout
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future


object JsonFormats  {
  import DefaultJsonProtocol._
  implicit val itemJsonFormat: RootJsonFormat[Item] = jsonFormat2(Item.apply)
  implicit val orderJsonFormat: RootJsonFormat[Order] = jsonFormat1(Order.apply)

  implicit val actionPerformedJsonFormat: RootJsonFormat[ActionPerformed]  = jsonFormat1(ActionPerformed.apply)
}

class OrderRoutes(orderRegistry: ActorRef[OrderRegistry.Command])(implicit val system: ActorSystem[_]) {

  import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  private implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("order-app.routes.ask-timeout"))

  def getItems(): Future[Order] =
    orderRegistry.ask(GetItems.apply)

  def getItem(name: String): Future[GetItemResponse] =
    orderRegistry.ask(GetItem(name, _))

  def createItem(item: Item): Future[ActionPerformed] =
    orderRegistry.ask(CreateItem(item, _))

  def deleteItem(name: String): Future[ActionPerformed] =
    orderRegistry.ask(DeleteItem(name, _))

  val orderRoutes: Route =
    pathPrefix("items") {
      concat(
        pathEnd {
          concat(
            get {
              complete(getItems())
            },
            post {
              entity(as[Item]) { item =>
                onSuccess(createItem(item)) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        path(Segment) { name =>
          concat(
            get {
              rejectEmptyResponse {
                onSuccess(getItem(name)) { response =>
                  complete(response.maybeItem)
                }
              }
            },
            delete {
              onSuccess(deleteItem(name)) { performed =>
                complete((StatusCodes.OK, performed))
              }
            })
        })
    }
}
