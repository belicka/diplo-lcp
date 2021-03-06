/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.matfyz.lcp;

import java.util.ArrayList;
import java.util.Collection;
import sk.matfyz.lcp.api.Agent;
import sk.matfyz.lcp.api.AgentInfo;
import sk.matfyz.lcp.api.DirectoryService;
import sk.matfyz.lcp.api.Discovery;
import sk.matfyz.lcp.api.DiscoveryService;

public class DiscoveryServiceImpl implements DiscoveryService {

    Collection<Discovery> channels = new ArrayList<>();
    DirectoryService directoryService;

    @Override
    public void register(DirectoryService ds) {
        if (ds == null) {
            throw new NullPointerException("ds cannot be null");
        }

        directoryService = ds;
    }

    @Override
    public void deregister(DirectoryService ds) {
        if (ds == null) {
            throw new NullPointerException("ds cannot be null");
        }

        directoryService = null;
    }
    
    @Override
    public void registerLocalAgent(Agent agent) {
        AgentInfo ainfo = new AgentInfo(agent.getName());

//        nastavit adresy pre kazdy kanal
        for (Discovery channel : channels) {
            ainfo.getTransportAddresses().add(channel.getTransportAddress());
        }

//        pridat agentInfo do jednotlivych discovery 
        for (Discovery channel : channels) {
            channel.registerLocalAgent(ainfo);
        }
    }

    @Override
    public void deregisterLocalAgent(Agent agent) {
        for (Discovery channel : channels) {
            channel.deregisterLocalAgent(agent.getName());
        }
    }
    
    @Override
    public void registerDiscovery(Discovery d) {
        channels.add(d);
    }

    @Override
    public void registerExternalAgent(AgentInfo agent) {
        directoryService.updateAgent(agent);
    }
}
