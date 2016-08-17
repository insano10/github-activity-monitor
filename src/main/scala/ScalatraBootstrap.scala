import javax.servlet.ServletContext

import com.insano10.GithubActivityMonitorServlet
import org.scalatra._

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new GithubActivityMonitorServlet, "/*")
  }
}
