package net.floodlightcontroller.authorization.bean;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.projectfloodlight.openflow.types.MacAddress;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DeviceSerializer extends JsonSerializer<Device> {

    @Override
    public void serialize(Device dev, JsonGenerator jGen,
            SerializerProvider serializer) throws IOException,
            JsonProcessingException {
        jGen.writeStartObject();
        
        jGen.writeStringField("mac", dev.mac);
        jGen.writeStringField("name", dev.name);
        
        jGen.writeEndObject();
    }

}
