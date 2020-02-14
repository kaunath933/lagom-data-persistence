package com.knoldus.customer.impl.command

import akka.Done
import com.knoldus.customer.api.CustomerDetails
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import play.api.libs.json.{Format, Json}

trait CustomerCommand[R] extends ReplyType[R]

case class CreateCustomerCommand(customer: CustomerDetails) extends CustomerCommand[Done]

object CreateCustomerCommand {
  implicit val format: Format[CreateCustomerCommand] = Json.format
}

case class GetCustomerCommand(id: String) extends CustomerCommand[CustomerDetails]

object GetCustomerCommand {
  implicit val format: Format[GetCustomerCommand] = Json.format
}

case class DeleteCustomer(id: String) extends CustomerCommand[Done]

object DeleteCustomer {
  implicit val format: Format[DeleteCustomer] = Json.format
}
