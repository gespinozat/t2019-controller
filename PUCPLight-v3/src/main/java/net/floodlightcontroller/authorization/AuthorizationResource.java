package net.floodlightcontroller.authorization;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import net.floodlightcontroller.authorization.bean.Community;

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
    
    public class CommunityDefinition {
        public String id = null;
        public String name = null;
    }
    
    protected void jsonToUserDefinition(String json, CommunityDefinition com) throws IOException {
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
            else if (n.equals("community")) {
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    String field = jp.getCurrentName();
                    if (field == null) continue;
                    if (field.equals("id")) {
                    	com.id = jp.getText();
                    } else if (field.equals("name")) {
                    	com.name = jp.getText();
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
    
    
    @Get("json")
    public Collection <Community> retrieve() {
        IAuthorizationManagerService coms =
                (IAuthorizationManagerService)getContext().getAttributes().
                    get(IAuthorizationManagerService.class.getCanonicalName());
        
        return coms.getAllCommunities();               
    }
    
    
    @Put
    @Post
    public String getCommunitiesPerUser(String postData) {        
    	CommunityDefinition com = new CommunityDefinition();
        try {
        	jsonToUserDefinition(postData, com);
        } catch (IOException e) {
            log.error("Could not parse JSON {}", e.getMessage());
        }
        
        // We try to get the ID from the URI only if it's not
        // in the POST data 
        if (com.id == null) {
	        String id = (String) getRequestAttributes().get("community");
	        if ((id != null) && (!id.equals("null")))
	        	com.id = id;
        }
        
        IAuthorizationManagerService coms =
                (IAuthorizationManagerService)getContext().getAttributes().
                    get(IAuthorizationManagerService.class.getCanonicalName());

        coms.getPerUserCommunities(com.id, com.name);
        setStatus(Status.SUCCESS_OK);
        return "{\"status\":\"ok\"}";
    }
    
    
    @Delete
    public String deleteNetwork() {
        IAuthorizationManagerService coms =
                (IAuthorizationManagerService)getContext().getAttributes().
                    get(IAuthorizationManagerService.class.getCanonicalName());
        String id = (String) getRequestAttributes().get("community");
        //coms.deleteCommunity(id); PENDING creat method and declare in the interface IAuthorizationManagerService
        setStatus(Status.SUCCESS_OK);
        return "{\"status\":\"ok\"}";
    }
    
}

