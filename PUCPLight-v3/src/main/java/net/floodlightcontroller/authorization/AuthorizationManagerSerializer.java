package net.floodlightcontroller.authorization;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.projectfloodlight.openflow.types.MacAddress;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serialize a VirtualNetwork object
 * @author KC Wang
 */
public class AuthorizationManagerSerializer extends JsonSerializer<AuthenticatedUser> {

    @Override
    public void serialize(AuthenticatedUser auth_user, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
        jGen.writeStartObject();
        
        jGen.writeStringField("identity", auth_user.identity);
        jGen.writeStringField("mac", auth_user.mac);     
        
        jGen.writeEndObject();
    }

}
