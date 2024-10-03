package com.funny
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors

final case class Item(name: String, id: Long)
final case class Order(items: List[Item])

object OrderRegistry {
  sealed trait Command
  final case class GetItems(replyTo: ActorRef[Order]) extends Command
  final case class CreateItem(item: Item, replyTo: ActorRef[ActionPerformed]) extends Command
  final case class GetItem(name: String, replyTo: ActorRef[GetItemResponse]) extends Command
  final case class DeleteItem(name: String, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class GetItemResponse(maybeItem: Option[Item])
  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(items: Set[Item]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetItems(replyTo) =>
        replyTo ! Order(items.toList)
        Behaviors.same
      case CreateItem(item, replyTo) =>
        replyTo ! ActionPerformed(s"Item \n ${item.name} created.")
        registry(items + item)
      case GetItem(name, replyTo) =>
        replyTo ! GetItemResponse(items.find(_.name == name))
        Behaviors.same
      case DeleteItem(name, replyTo) =>
        replyTo ! ActionPerformed(s"Item \n $name deleted.")
        registry(items.filterNot(_.name == name))
    }
}
