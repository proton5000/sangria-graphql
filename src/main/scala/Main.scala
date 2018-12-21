import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import sangria.ast.Document
import sangria.execution._
import sangria.macros.derive._
import sangria.marshalling.sprayJson._
import sangria.parser.QueryParser
import sangria.schema._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Main {

  def main(args: Array[String]): Unit = {

//    val QueryType = ObjectType("Query", fields[Unit, Unit](
//      Field("hello", StringType, resolve = _ => "Hello world!")
//    ))
//
//    val schema = Schema(QueryType)
//    val query = graphql"{ hello }"
//
//    val result = Executor.execute(schema, query)
//
//    result.foreach(res => println(res.spaces2))

    val IdentifiableType = InterfaceType(
      "Identifiable",
      "Entity that can be identified",

      fields[Unit, Identifiable](
        Field("id", StringType, resolve = _.value.id)))

    implicit val PictureType: ObjectType[Unit, Picture] = ObjectType(
      "Picture",
      "The product picture",

      fields[Unit, Picture](
        Field("width", IntType, resolve = _.value.width),
        Field("height", IntType, resolve = _.value.height),
        Field("url", OptionType(StringType),
          description = Some("Picture CDN URL"),
          resolve = _.value.url)))

    val ProductType: ObjectType[Unit, Product] =
      deriveObjectType[Unit, Product](
        Interfaces(IdentifiableType),
        IncludeMethods("picture"))

    val Id = Argument("id", StringType)

    val QueryType = ObjectType("Query", fields[ProductRepo, Unit](
      Field("product", OptionType(ProductType),
        description = Some("Returns a product with specific `id`."),
        arguments = Id :: Nil,
        resolve = c => c.ctx.product(c arg Id)),

      Field("products", ListType(ProductType),
        description = Some("Returns a list of all available products."),
        resolve = _.ctx.products)))

    val schema: Schema[ProductRepo, Unit] = Schema(QueryType)

    def graphQLEndpoint(requestJson: JsValue) = {
      val JsObject(fields) = requestJson

      val JsString(query) = fields("query")

      val operation = fields.get("operationName") collect {
        case JsString(op) => op
      }

      val vars = fields.get("variables") match {
        case Some(obj: JsObject) ⇒ obj
        case _ => JsObject.empty
      }

      QueryParser.parse(query) match {

        // query parsed successfully, time to execute it!
        case Success(queryAst) => complete(ToResponseMarshallable(executeGraphQLQuery(queryAst, operation, vars)))

        // can't parse GraphQL query, return error
        case Failure(error) => complete(ToResponseMarshallable(BadRequest, JsObject("error" -> JsString(error.getMessage))))
      }
    }

    def executeGraphQLQuery(query: Document, op: Option[String], vars: JsObject) =
      Executor.execute(schema, query, new ProductRepo, variables = vars, operationName = op)
        .map(OK -> _)
        .recover {
          case error: QueryAnalysisError => BadRequest -> error.resolveError
          case error: ErrorWithResolver => InternalServerError -> error.resolveError
        }

    implicit val system: ActorSystem = ActorSystem("sangria-server")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    import system.dispatcher

    val route: Route =
      (post & path("graphql")) {
        entity(as[JsValue]) { requestJson =>
          graphQLEndpoint(requestJson)
        }
      } ~
        get {
          getFromResource("graphiql.html")
        }

    Http().bindAndHandle(route, "0.0.0.0", 8080)

  }

}
