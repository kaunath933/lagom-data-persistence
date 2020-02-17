package com.knoldus.customer.impl.event

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.knoldus.customer.api.CustomerDetails
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}

import scala.concurrent.{ExecutionContext, Future}

class CustomerEventReadSideProcessor(db: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext) extends ReadSideProcessor[CustomerEvent] {

  var addCustomer: PreparedStatement = _

  var deleteCustomer: PreparedStatement = _

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[CustomerEvent] = readSide.builder[CustomerEvent]("CustomerEventReadSidePreocessor")
    .setGlobalPrepare(()=>createTable)
    .setPrepare(_ => prepareStatements())
    .setEventHandler[CustomerAdded](ese => addCustomer(ese.event.customer))
    .setEventHandler[CustomerDeleted](ese => deleteCustomer(ese.event.id))
    .build()

  override def aggregateTags: Set[AggregateEventTag[CustomerEvent]] = CustomerEvent.Tag.allTags

  def createTable(): Future[Done] = {
    db.executeCreateTable(
      """CREATE TABLE IF NOT EXISTS customerdatabase.customer (
        |id text PRIMARY KEY, name text, email text)""".stripMargin)
  }

  def prepareStatements(): Future[Done] =
    db.prepare("INSERT INTO customerdatabase.customer (id, name, email) VALUES (?, ?, ?)")
      .map { ps =>
        addCustomer = ps
        Done
      }.map(_ => db.prepare("DELETE FROM customer where id = ?").map(ps => {
      deleteCustomer = ps
      Done
    })).flatten

  def addCustomer(customer: CustomerDetails): Future[List[BoundStatement]] = {
    val bindInsertCustomer: BoundStatement = addCustomer.bind()
    bindInsertCustomer.setString("id", customer.id)
    bindInsertCustomer.setString("name", customer.name)
    bindInsertCustomer.setString("email", customer.email)
    Future.successful(List(bindInsertCustomer))
  }

  def deleteCustomer(id: String): Future[List[BoundStatement]] = {
    val bindDeleteCustomer: BoundStatement = deleteCustomer.bind()
    bindDeleteCustomer.setString("id", id)
    Future.successful(List(bindDeleteCustomer))
  }

}