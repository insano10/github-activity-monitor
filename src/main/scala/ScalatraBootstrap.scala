import javax.servlet.ServletContext
import _root_.akka.actor.{Props, ActorSystem}
import com.insano10.gham.GithubActivityMonitorServlet
import org.scalatra._

class ScalatraBootstrap extends LifeCycle {

  val system = ActorSystem()

  override def init(context: ServletContext) {
    context.mount(new GithubActivityMonitorServlet(system), "/*")
  }

  override def destroy(context:ServletContext) {
    system.terminate()
  }
}
