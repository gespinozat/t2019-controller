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


@JsonSerialize(using=DeviceSerializer.class)
public class Device{
    protected String mac; // device mac
	protected String name; // device name
    /**
     * Constructor requires mac
     * @param mac: mac address 
     */
    public Device(String mac,String name) {
        this.mac = mac;
        this.name = name;
        return;        
    }
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}  
    
    
   
}
