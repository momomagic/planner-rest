package hacora.planner.receiver.core

import com.datastax.driver.core.{BoundStatement, ResultSet, ResultSetFuture}
import scala.concurrent.{CanAwait, Future, ExecutionContext}
import scala.util.{Success, Try}
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

/**
  * Created by momo on 5/23/16.
  */
private[core] trait CassandraResultSetOperations {

  /**
    * Execuation case class to perform asynchronous functionalities
    * @param executonContext
    */
  private case class ExecutionContextExecutor(executonContext: ExecutionContext) extends java.util.concurrent.Executor {
    def execute(command: Runnable): Unit = { executonContext.execute(command) }
  }


  /**
    * Asnychronous reader , listen to results without interruption
    * @param resultSetFuture
    */
  protected class RichResultSetFuture(resultSetFuture: ResultSetFuture) extends Future[ResultSet] {


    /**
      * return an instance of this class with asynchronous result set
      * @param atMost
      * @param permit
      * @throws java.lang.InterruptedException
      * @throws java.util.concurrent.TimeoutException
      * @return
      */
    @throws(classOf[InterruptedException])
    @throws(classOf[scala.concurrent.TimeoutException])
    def ready(atMost: Duration)(implicit permit: CanAwait): this.type = {
      resultSetFuture.get(atMost.toMillis, TimeUnit.MILLISECONDS)
      this
    }

    /**
      * Get a result set asynchronously
      * @param atMost
      * @param permit
      * @throws java.lang.Exception
      * @return
      */
    @throws(classOf[Exception])
    def result(atMost: Duration)(implicit permit: CanAwait): ResultSet = {
      resultSetFuture.get(atMost.toMillis, TimeUnit.MILLISECONDS)
    }

    /**
      * Are you done and completed start returning results
      * @param func
      * @param executionContext
      * @tparam U
      */
    def onComplete[U](func: (Try[ResultSet]) => U)(implicit executionContext: ExecutionContext): Unit = {
      if (resultSetFuture.isDone) {
        func(Success(resultSetFuture.getUninterruptibly))
      } else {
        resultSetFuture.addListener(new Runnable {
          def run() {
            func(Try(resultSetFuture.get()))
          }
        }, ExecutionContextExecutor(executionContext))
      }
    }

    /**
      * Are you done yey ?
      *
      * @return
      */
    def isCompleted: Boolean = resultSetFuture.isDone

    /**
      * Get value if it is done otherwise get NONE
      * @return
      */
    def value: Option[Try[ResultSet]] = if (resultSetFuture.isDone) Some(Try(resultSetFuture.get())) else None
  }

  /**
    * Impplicit conversion from ResultSetFuture to RichResultSetFuture
    * @param resultSetFuture
    * @return
    */
  implicit def toFuture(resultSetFuture: ResultSetFuture): Future[ResultSet] = new RichResultSetFuture(resultSetFuture)
}

/**
  * Binder trait from value to BoundStatement
  * @tparam A
  */
trait Binder[-A] {
  def bind(value: A, boundStatement: BoundStatement): Unit

}

trait BoundStatementOperations {

  implicit class RichBoundStatement[A : Binder](boundStatement: BoundStatement) {
    val binder = implicitly[Binder[A]]

    def bindFrom(value: A): BoundStatement = {
      binder.bind(value, boundStatement)
      boundStatement
    }
  }

}

object cassandra {

  object resultset extends CassandraResultSetOperations

  object boundstatement extends BoundStatementOperations

}