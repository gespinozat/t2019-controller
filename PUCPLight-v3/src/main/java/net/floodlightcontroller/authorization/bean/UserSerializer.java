package net.floodlightcontroller.authorization.bean;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.projectfloodlight.openflow.types.MacAddress;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


public class UserSerializer extends JsonSerializer<User> {

    @Override
    public void serialize(User auth_user, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
        jGen.writeStartObject();
        
        jGen.writeStringField("identity", auth_user.identity);
        jGen.writeStringField("mac", auth_user.mac);     
        
        jGen.writeEndObject();
    }

}
