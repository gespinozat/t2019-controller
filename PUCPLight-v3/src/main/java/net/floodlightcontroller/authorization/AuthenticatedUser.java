package net.floodlightcontroller.authorization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.floodlightcontroller.devicemanager.SwitchPort;


@JsonSerialize(using=AuthorizationManagerSerializer.class)
public class AuthenticatedUser{
    protected String identity; // network name
    protected String mac; // network id
	
    /**
     * Constructor requires network name and id
     * @param identity: user identity
     * @param mac: mac address 
     */
    public AuthenticatedUser(String name, String guid) {
        this.identity = name;
        this.mac = guid;		
        return;        
    }  
    
    
   
}
