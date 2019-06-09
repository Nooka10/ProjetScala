package controllers

import javax.inject._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action {
    // redirige sur la page de documentation de l'API
    Redirect("https://documenter.getpostman.com/view/964181/S1TX2dFn?version=latest")
  }

}
