package net.floodlightcontroller.authorization;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IAuthorizationManagerService extends IFloodlightService {
	/**
	 * Query controller database to find out user's communities.
	 * 
	 * 
	 */
	public void queryDatabase(String identity, String mac);

	public List<String> getCommunities(String indentity);
	

}