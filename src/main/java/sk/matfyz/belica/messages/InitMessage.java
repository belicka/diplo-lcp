/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.matfyz.belica.messages;

import java.util.Set;
import sk.matfyz.lcp.AbstractMessage;
import sk.matfyz.lcp.api.AgentId;

/**
 *
 * @author martin
 */
public class InitMessage extends AbstractMessage {

    public InitMessage(Set<AgentId> rcpts) {
        super(AgentId.ROOT, null, rcpts, null);
    }

    @Override
    public String toString() {
        return "InitMessage";
    }
}