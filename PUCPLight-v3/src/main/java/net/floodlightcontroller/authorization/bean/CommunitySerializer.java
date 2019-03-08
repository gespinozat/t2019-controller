package net.floodlightcontroller.authorization.bean;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.projectfloodlight.openflow.types.MacAddress;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CommunitySerializer extends JsonSerializer<Community> {

    @Override
    public void serialize(Community com, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
        jGen.writeStartObject();
        
        jGen.writeStringField("id", com.id);
        jGen.writeStringField("name", com.name);
        
        jGen.writeEndObject();
    }

}
