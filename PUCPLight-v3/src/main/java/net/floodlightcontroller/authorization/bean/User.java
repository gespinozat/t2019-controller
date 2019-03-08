package net.floodlightcontroller.authorization.bean;

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


@JsonSerialize(using=UserSerializer.class)
public class User{
    protected String identity; // user name
    protected String mac; // device mac
	
    /**
     * Constructor requires user identity and mac
     * @param identity: user identity
     * @param mac: mac address 
     */
    public User(String identity, String mac) {
        this.identity = identity;
        this.mac = mac;		
        return;        
    }

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}  
    
    
   
}
