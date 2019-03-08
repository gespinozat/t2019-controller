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


@JsonSerialize(using=CommunitySerializer.class)
public class Community{
    protected String id; // community id
    protected String name; // community name
	
    /**
     * Constructor requires community id and name
     * @param id: community identity
     * @param name: community name 
     */    
    
    
    public Community(String id, String name) {
        this.id = id;
        this.name = name;		
        return;        
    }

	public Community() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}  
    
    
   
}
