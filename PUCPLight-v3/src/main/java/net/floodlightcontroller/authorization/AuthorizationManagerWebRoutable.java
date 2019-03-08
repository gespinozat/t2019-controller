package net.floodlightcontroller.authorization;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class AuthorizationManagerWebRoutable  implements RestletRoutable {
	@Override
	public Router getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/authorized/json", AuthorizationResource.class); // GET
		router.attach("/authorized/receive/json", AuthorizationResource.class); // PUT, DELETE
		router.attach("/authorized/update/json", AuthorizationResource.class); // POST
		//router.attachDefault(NoOp.class);
		return router;
		}
	@Override
	public String basePath() {
		return "/authorization";
	}	
}
