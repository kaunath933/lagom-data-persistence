package com.knoldus.customer.impl.event

import com.knoldus.customer.api.CustomerDetails
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag, AggregateEventTagger}
import play.api.libs.json.{Format, Json}

sealed trait CustomerEvent extends AggregateEvent[CustomerEvent] {
  override def aggregateTag: AggregateEventTagger[CustomerEvent] = CustomerEvent.Tag
}

object CustomerEvent {
  val NumShards = 3
  val Tag: AggregateEventShards[CustomerEvent] = AggregateEventTag.sharded[CustomerEvent](NumShards)
}

case class CustomerAdded(customer: CustomerDetails) extends CustomerEvent

object CustomerAdded {
  implicit val format: Format[CustomerAdded] = Json.format
}

case class CustomerDeleted(id: String) extends CustomerEvent

object CustomerDeleted {
  implicit val format: Format[CustomerDeleted] = Json.format
}
