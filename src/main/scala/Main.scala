import sangria.schema._
import sangria.execution._
import sangria.macros._
import sangria.marshalling.circe._
import scala.concurrent.ExecutionContext.Implicits.global

object Main {

  def main(args: Array[String]): Unit = {

    val QueryType = ObjectType("Query", fields[Unit, Unit](
      Field("hello", StringType, resolve = _ => "Hello world!")
    ))

    val schema = Schema(QueryType)
    val query = graphql"{ hello }"

    val result = Executor.execute(schema, query)

    result.foreach(res => println(res.spaces2))

  }

}
