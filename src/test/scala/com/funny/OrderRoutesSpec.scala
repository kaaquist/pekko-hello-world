package com.funny

import org.apache.pekko.actor.testkit.typed.scaladsl.ActorTestKit
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.marshalling.Marshal
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class OrderRoutesSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest {
  lazy val testKit = ActorTestKit()
  implicit def typedSystem: ActorSystem[_] = testKit.system
  override def createActorSystem(): org.apache.pekko.actor.ActorSystem =
    testKit.system.classicSystem

  val orderRegistry = testKit.spawn(com.funny.OrderRegistry())
  lazy val routes = new OrderRoutes(orderRegistry).orderRoutes

  // use the json formats to marshal and unmarshall objects in the test
  import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#set-up

  "OrderRoutes" should {
    "return no items if no present (GET /items)" in {
      val request = HttpRequest(uri = "/items")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"items":[]}""")
      }
    }

    "be able to add items (POST /items)" in {
      val item = Item("Bread", 12)
      val itemEntity = Marshal(item).to[MessageEntity].futureValue // futureValue is from ScalaFutures

      val request = Post("/items").withEntity(itemEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"description":"Item Bread created."}""")
      }
    }

    "be able to remove items (DELETE /items)" in {
      val request = Delete(uri = "/items/Bread")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"description":"Item Bread deleted."}""")
      }
    }
  }
}