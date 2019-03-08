package net.floodlightcontroller.authorization;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class AuthorizationManagerWebRoutable  implements RestletRoutable {
	@Override
	public Router getRestlet(Context context) {
		Router router = new Router(context);
		//router.attach("/authorized", AuthorizationResource.class); // GET
		router.attach("/authorized", AuthorizationResource.class); // PUT, DELETE
		//router.attach("/authorized", AuthorizationResource.class); // POST
		/* router.attach("/tenants/{tenant}/rules/type/{type}/network/{vnid}", RuleResource1.class);
		router.attach("/tenants/{tenant}/rules/type/{type}/network/{vnid}", RuleResource1.class);
		router.attach("/tenants/{tenant}/networks/filter/{network}", IsolatingResource1.class); */
		//router.attachDefault(NoOp.class);
		return router;
		}
	@Override
	public String basePath() {
		return "/authorization";
	}	
}
