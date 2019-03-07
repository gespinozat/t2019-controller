package net.floodlightcontroller.authorization;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationResource extends ServerResource {
    protected static Logger log = LoggerFactory.getLogger(AuthorizationResource.class);
    
    public class UserDefinition {
        public String identity = null;
        public String mac = null;
    }
    
    protected void jsonToUserDefinition(String json, UserDefinition auth_user) throws IOException {
        MappingJsonFactory f = new MappingJsonFactory();
        JsonParser jp;
        
        try {
            jp = f.createJsonParser(json);
        } catch (JsonParseException e) {
            throw new IOException(e);
        }
        
        jp.nextToken();
        if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected START_OBJECT");
        }
        
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            if (jp.getCurrentToken() != JsonToken.FIELD_NAME) {
                throw new IOException("Expected FIELD_NAME");
            }
            
            String n = jp.getCurrentName();
            jp.nextToken();
            if (jp.getText().equals("")) 
                continue;
            else if (n.equals("auth_user")) {
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String field = jp.getCurrentName();
                    if (field == null) continue;
                    if (field.equals("identity")) {
                    	auth_user.identity = jp.getText();
                    } else if (field.equals("mac")) {
                    	auth_user.mac = jp.getText();
                    } else {
                        log.warn("Unrecognized field {} in " +
                        		"parsing network definition", 
                        		jp.getText());
                    }
                }
            }
        }
        
        jp.close();
    }
    
    /*
    @Get("json")
    public Collection <VirtualNetwork> retrieve() {
        IVirtualNetworkService vns =
                (IVirtualNetworkService)getContext().getAttributes().
                    get(IVirtualNetworkService.class.getCanonicalName());
        
        return vns.listNetworks();               
    }
    */
    
    @Put
    @Post
    public String queryDatabase(String postData) {        
        UserDefinition auth_user = new UserDefinition();
        try {
        	jsonToUserDefinition(postData, auth_user);
        } catch (IOException e) {
            log.error("Could not parse JSON {}", e.getMessage());
        }
        
        // We try to get the ID from the URI only if it's not
        // in the POST data 
        if (auth_user.identity == null) {
	        String identity = (String) getRequestAttributes().get("identity");
	        if ((identity != null) && (!identity.equals("null")))
	        	auth_user.identity = identity;
        }
        
        IAuthorizationManagerService users =
                (IAuthorizationManagerService)getContext().getAttributes().
                    get(IAuthorizationManagerService.class.getCanonicalName());

        users.queryDatabase(auth_user.identity, auth_user.mac);
        setStatus(Status.SUCCESS_OK);
        return "{\"status\":\"ok\"}";
    }
    
    /*
    @Delete
    public String deleteNetwork() {
        IVirtualNetworkService vns =
                (IVirtualNetworkService)getContext().getAttributes().
                    get(IVirtualNetworkService.class.getCanonicalName());
        String guid = (String) getRequestAttributes().get("network");
        vns.deleteNetwork(guid);
        setStatus(Status.SUCCESS_OK);
        return "{\"status\":\"ok\"}";
    }
    */
}

