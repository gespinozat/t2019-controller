package net.floodlightcontroller.authorization;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFFlowDelete;
import org.projectfloodlight.openflow.protocol.OFFlowStatsEntry;
import org.projectfloodlight.openflow.protocol.OFFlowStatsReply;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFStatsReply;
import org.projectfloodlight.openflow.protocol.OFStatsRequest;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActions;
import org.projectfloodlight.openflow.protocol.instruction.OFInstruction;
import org.projectfloodlight.openflow.protocol.instruction.OFInstructionApplyActions;
import org.projectfloodlight.openflow.protocol.instruction.OFInstructions;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv6Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TableId;
import org.projectfloodlight.openflow.types.U64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;

import net.floodlightcontroller.authorization.dao.DCommunity;
import net.floodlightcontroller.authorization.bean.Community;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IListener.Command;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceListener;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingDecision;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.RoutingDecision;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.virtualnetwork.IVirtualNetworkService;
import net.floodlightcontroller.virtualnetwork.VirtualNetwork;
import net.floodlightcontroller.virtualnetwork.VirtualNetworkManager;
import net.floodlightcontroller.virtualnetwork.VirtualNetworkWebRoutable;
import net.floodlightcontroller.virtualnetwork.VlanRule;


public class AuthorizationManager implements IFloodlightModule, IAuthorizationManagerService {
	
	// Register APP ID
	private static final short APP_ID = 9;

	static {
		AppCookie.registerApp(APP_ID, "AuthorizationManager");

	}
	
	// Logger for module
	protected static Logger log = LoggerFactory.getLogger(AuthorizationManager.class);

	// Module dependencies
	protected IFloodlightProviderService floodlightProviderService;
	protected IRestApiService restApiService;
	protected IDeviceService deviceService;
	protected IOFSwitchService switchService;
	protected IStaticFlowEntryPusherService sfp;
	protected IRoutingService routingService;
	protected ITopologyService topologyService;
	
	private OFFactory factory = OFFactories.getFactory(OFVersion.OF_13);
	
	protected DCommunity dcom = new DCommunity();
	
	List<Community> comunidades = dcom.german();
	Iterator<Community> iterar = comunidades.iterator();
	
	public void ver() {
		
		for (int i = 0; i < comunidades.size(); i++) {
			log.info("El nombre de la comunidad {} es {} ", new Object[] {comunidades.get(i).getId(),comunidades.get(i).getName() });
		}
		
	}
	

	// IAuthorizationManagerService
	@Override
	public void queryDatabase(String identity, String mac) {
		if (log.isInfoEnabled()) {
			log.info("Authorization running : User {} ", new Object[] { mac, identity });
			log.info(identity);
			log.info(mac);
		}
		
		//Logica de query a la base de datos. Pendiente definir como se manejara la base de datos.
	}
	
	@Override
	public List<String> getAllCommunities() {
		// TODO Auto-generated method stub
		return null;
	}
			

	// IFloodlightModule
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IAuthorizationManagerService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IAuthorizationManagerService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRestApiService.class);
		l.add(IDeviceService.class);
		l.add(IOFSwitchService.class);
		l.add(IStaticFlowEntryPusherService.class);
		l.add(IRoutingService.class);
		l.add(ITopologyService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProviderService = context.getServiceImpl(IFloodlightProviderService.class);
		restApiService = context.getServiceImpl(IRestApiService.class);
		deviceService = context.getServiceImpl(IDeviceService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);
		sfp = context.getServiceImpl(IStaticFlowEntryPusherService.class);
		routingService = context.getServiceImpl(IRoutingService.class);
		topologyService = context.getServiceImpl(ITopologyService.class);
		
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		restApiService.addRestletRoutable(new AuthorizationManagerWebRoutable());
		ver();
		
	}	
		
}