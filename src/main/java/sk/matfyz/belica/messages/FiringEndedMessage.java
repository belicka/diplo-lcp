/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.matfyz.belica.messages;

import java.util.Set;
import sk.matfyz.belica.ContextId;
import sk.matfyz.lcp.api.AgentId;
import sk.matfyz.lcp.api.MessageId;

/**
 *
 * @author martin
 */
public class FiringEndedMessage extends MessageWithContext {

    public FiringEndedMessage(AgentId senderLabel, MessageId messageId, ContextId context, Set<AgentId> rcpts) {
        super(senderLabel, messageId, context, rcpts, null);
    }
        
    @Override
    public String toString() {
        return "FiringEndedMessage: " + getSender() + " has ended firing.";
    }
}
