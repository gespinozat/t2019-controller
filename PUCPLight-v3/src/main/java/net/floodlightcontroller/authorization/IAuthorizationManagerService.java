package net.floodlightcontroller.authorization;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import net.floodlightcontroller.authorization.bean.Community;

import net.floodlightcontroller.core.module.IFloodlightService;

public interface IAuthorizationManagerService extends IFloodlightService {
	/**
	 * Query controller database to find out user's communities.
	 * 
	 * 
	 */
	public void getPerUserCommunities(String identity);

	public Collection<Community> getAllCommunities();
	

}