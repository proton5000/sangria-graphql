package controllers

import akka.http.scaladsl.server.Directives._

object Controller {

  val routes = get {
    pathEndOrSingleSlash {
      getFromResource("web/index.html")
    }
  }

}
