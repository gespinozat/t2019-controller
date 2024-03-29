curl -X PUT -d '{ "user": { "identity": "gespinoza", "mac": "09:99:88:77:01:00" } }' http://localhost:8080/authorization/authorized/receive/json


Floodlight OpenFlow Controller (OSS)
====================================

Build Status:
-------------

[![Build Status](https://travis-ci.org/floodlight/floodlight.svg?branch=master)](https://travis-ci.org/floodlight/floodlight)

Floodlight Wiki
---------------

First, the Floodlight wiki has moved. Please visit us at our new site hosted by Atlassian:

https://floodlight.atlassian.net/wiki/display/floodlightcontroller/Floodlight+Documentation

What is Floodlight?
-------------------

Floodlight is the leading open source SDN controller. It is supported by a community of developers including a number of engineers from Big Switch Networks (http://www.bigswitch.com/).

OpenFlow is a open standard managed by Open Networking Foundation. It specifies a protocol through switch a remote controller can modify the behavior of networking devices through a well-defined “forwarding instruction set”. Floodlight is designed to work with the growing number of switches, routers, virtual switches, and access points that support the OpenFlow standard.

The v1.1 Floodlight release builds upon the improvements made in v1.0, with emphasis on new security features, the inclusion of a new proactive Access Control List module (or ACL) written by Pengfei (Alex) Lu – thanks Alex!, bug fixes to the Static Flow Pusher, REST API improvements, more sophisticated flow checking for the Static Flow Pusher – thanks Sanjivini and Naveen!, a reworked Firewall REST API – thanks electricjay!, support for dynamic switch role changes through the REST API and within modules, a new included DHCP server, and many bug fixes and optimizations.


Floodlight v1.1 has full support for OpenFlow 1.0 and 1.3 along with experimental support for OpenFlow 1.1, 1.2, and 1.4. Here are the highlights of what Floodlight v1.1 has to offer and how you can get your hands on it:

At it's core is the OpenFlowJ-Loxigen (or OpenFlowJ-Loxi for short) generated Java library, which among many powerful things abstracts the OpenFlow version behind a common API. Loxigen works by parsing OpenFlow concepts defined as structures in a set of input files. It then generates a set of Java, Python, and C libraries for use in OpenFlow applications. The Loxigen-generated libraries abstract away low-level details and provide a far more pleasant and high-level programming experience for developers. It is straightforward to define each OpenFlow version in Loxigen's input files, and each OpenFlow version is exposed through a common API, which results in few if not zero application code changes when adding OpenFlow versions. In other words, Loxigen provides a fairly future-proof API for the many OpenFlow versions to come. The Loxigen project is open source and can be found on GitHub here (http://github.com/floodlight/loxigen/wiki/OpenFlowJ-Loxi).

Floodlight of course uses the Java library generated by Loxigen, also known as OpenFlowJ-Loxi. Although OpenFlowJ-Loxi is the new heart of the new Floodlight controller, there have been many higher-level changes necessary to accommodate the new library as well as to fix some known bugs and improve the overall performance and capabilities of the Floodlight controller. Many will go unnoticed; however, some will have immediate impact on how your modules interact with the controller core.

For instance, the Floodlight v0.90 and v0.91 (old master) Controller class was, among many things, responsible for managing switches. This responsibility has been relocated from the Controller class to a new class called the OFSwitchManager. It is exposed to modules as a service, the IOFSwitchService. Instead of accessing switches using the IFloodlightProviderService, developers should instead depend on and obtain a reference to the IOFSwitchService.

Furthermore, the Static Flow Pusher and REST API in general has undergone an extensive renovation to enable full OpenFlow 1.3 support. More information on the Static Flow Pusher and its REST API syntax can be found here (http://www.openflowhub.org/display/floodlightcontroller/Floodlight+REST+API). Please note any syntax changes from prior Floodlight versions, which have been done to be more consistent with ovs-ofctl style keys.

One of the key features of Floodlight v1.1 is its full support for OpenFlow 1.0 and 1.3, complete with an easy-to-use, version-agnostic API. Each OpenFlow version has a factory that can build all types and messages as they are defined for that version of OpenFlow. This allows for a very much improved way to create OpenFlow Messages, Matches, Actions, FlowMods, etc. The creation of many OpenFlow objects has been greatly simplified using builders, all accessible from a common OpenFlow factory interface. All objects produced from builders are immutable, which allows for safer code and makes your applications easier to debug.

To best demonstrate the extent to which constructing and working with OpenFlow concepts such as FlowMods has been improved in Floodlight v1.0, consider the following before and after example.

**Pre-v1.0 -- the old way to compose an OFFlowMod**

    OFFlowMod flow = new OFFlowMod(); // no builder pattern; not immutable
    OFMatch match = new OFMatch();
    ArrayList<OFAction> actions = new ArrayList<OFAction>();
    OFActionOutput outputAction = new OFActionOutput();
    match.setInputPort((short) 1); // not type-safe; many OpenFlow concepts are represented as shorts
    match.setDataLayerType(Ethernet.TYPE_IPv4);
    match.setWildcards(Wildcards.FULL.matchOn(Flag.IN_PORT).matchOn(Flag.DL_TYPE)); // wildcarding necessary
    outputAction.setType(OFActionType.OUTPUT); 
    outputAction.setPort((short) 2); // raw types used; casting required
    outputAction.setLength((short) OFActionOutput.MINIMUM_LENGTH);
    actions(outputAction);
    flow.setBufferId(-1);
    flow.setActions(actions);
    flow.setMatch(match);
    flow.setLengthU(OFFlowMod.MINIMUM_LENGTH + outputAction.getLengthU()); // length must be set correctly
    sw.write(flow);

**Floodlight v1.0, v1.1 -- the new and improved way to compose an OFFlowMod**

    ArrayList<OFAction> actions = new ArrayList<OFAction();
    actions.add(myFactory.actions().buildOutput() // builder pattern used throughout
    .setPort(OFPort.of(1)) // raw types replaced with objects for type-checking and readability
    .build()); // list of immutable OFAction objects
    OFFlowAdd flow = myFactory.buildFlowAdd()
    .setMatch(myfactory.buildMatch()
    .setExact(MatchField.IN_PORT, OFPort.of(1)) // type-checked matching
    .setExact(MatchField.ETH_TYPE, EthType.IPv4))
    .build()) // immutable Match object
    .setActions(actions)
    .setOutPort(OFPort.of(2))
    .setBufferId(OFBufferId.NO_BUFFER)
    .build(); // immutable OFFlowMod; no lengths to set; no wildcards to set
    sw.write(flow);

Some of the concepts above will be discussed further below, but the major items to note are the use of the builder design pattern for ease-of-use and the production of immutable objects, the use of objects instead of raw types to enforce type-safe coding and to produce more readable code, built-in wildcarding, and finally there is no need to deal with message lengths.

All switches that connect to Floodlight contain a factory for the version of OpenFlow the switch speaks. There can be multiple switches, all speaking different versions of OpenFlow, where the controller handles the low-level protocol differences behind the scenes. From the perspective of modules and application developers, the switch is simply exposed as an IOFSwitch, which has the function getOFFactory() to return the OpenFlowJ-Loxi factory appropriate for the OpenFlow version the switch is speaking. Once you have the correct factory, you can create OpenFlow types and concepts through the common API OpenFlowJ-Loxi exposes.

As such, you do not need to switch APIs when composing your FlowMods and other types. Let's say you wish to build a FlowMod and send it to a switch. Each switch known to the OFSwitchManager has a reference to an OpenFlow factory of the same version negotiated in the initial handshake between the switch and the controller. Simply reference the factory from your switch, create the builder, build the FlowMod, and write it to the switch. The same API is exposed for the construction of all OpenFlow objects, regardless of the OpenFlow version. You will however need to know what you are allowed to do for each OpenFlow version; otherwise, if you for example tell an OpenFlow 1.0 switch to perform some action such as add a Group, which is not supported for it's OpenFlow version, the OpenFlowJ-Loxi library will kindly inform you with an UnsupportedOperationException.

There are some other subtle changes introduced, for the better. For example, many common types such as switch datapath IDs, OpenFlow ports, and IP and MAC addresses are defined by the OpenFlowJ-Loxi library through the DatapathId, OFPort, IPv4Address/IPv6Address, and MacAddress, respectively. You are encouraged to explore org.projectfloodlight.openflow.types, where you will find a wide variety of common types that are now conveniently defined in a single location. Like the objects produced from builders above, all types are immutable.

For more information on how to use the new APIs exposed in Floodlight v1.1, please refer to the OpenFlowJ-Loxi documentation and examples here (https://floodlight.atlassian.net/wiki/display/floodlightcontroller/How+to+use+OpenFlowJ-Loxigen).

There are many more minor details, which can be found in the release notes. I have been grateful to have the support of many Floodlight developers, and together we have worked to provide the highest quality release within a reasonable time frame. I would especially like to thank the following individuals and beta testers for their code contributions and debugging efforts:

* Rui Cardoso
* Hung-Wei Chiu
* Rich Lane
* Qingxiang Lin
* Sanjivini Naikar
* Jason Paraga
* Naveen Sampath
* Rob Sherwood
* Sebastian Szwaczyk
* KC Wang
* Andreas Wundsam
* electricjay
* Pengfei (Alex) Lu

Based on further community feedback, there will be minor releases to address any issues found or enhancements anyone would like to contribute. The mailing list has seen quite an uptick in activity over the last few months! 

If at any time you have a question or concern, please reach out to us. We rely on our fellow developers to make the most effective improvements and find any bugs. Thank you all for the support and I hope you find your work with Floodlight v1.1 fun and productive! 

Happy coding!  
Ryan Izard  
ryan.izard@bigswitch.com  
rizard@g.clemson.edu




Floodlight v1.1 can be found on GitHub at:  
http://github.com/floodlight/floodlight/tree/v1.1.

Any updates leading up to a minor release after v1.1 will be placed in master at:  
http://github.com/floodlight/floodlight.

And finally all "bleeding edge" updates will be in my repository's master branch at:  
http://github.com/rizard/floodlight.

If you need an older version of Floodlight for any reason, they can still be found on GitHub:  

Floodlight v1.0 can be found on GitHub at:  
http://github.com/floodlight/floodlight/tree/v1.0  

Floodlight v0.91 (old master) can be found at:  
https://github.com/floodlight/floodlight/tree/v0.91

Floodlight v0.90 can be found at:  
https://github.com/floodlight/floodlight/tree/v0.90

To download a pre-built VM appliance, access documentation, and sign up for the mailing list, go to:  
http://www.projectfloodlight.org/floodlight
