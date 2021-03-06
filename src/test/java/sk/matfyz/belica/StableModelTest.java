package sk.matfyz.belica;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import sk.matfyz.belica.messages.ContextEndedMessage;
import sk.matfyz.belica.messages.MessageWithContext;
import sk.matfyz.lcp.DefaultPlatform;
import sk.matfyz.lcp.api.AgentId;
import sk.matfyz.lcp.api.Message;
import sk.matfyz.lcp.api.Platform;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author martin
 */
public class StableModelTest {

    public class TestLogicProgrammingAgent extends LogicProgrammingAgent {

        public TestLogicProgrammingAgent(Platform platform, AgentId id) {
            super(platform, id);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Message msg = getMessages().take();
                    ContextId msgCtxId = ((MessageWithContext) msg).getContextId();
                    Context found = getContexts().get(msgCtxId);

                    if (found == null) {
                        Context newCtx = new Context(msgCtxId, this);
                        getContexts().put(msgCtxId, newCtx);
                    }

                    // do not remove context on context end
                    if (!(msg instanceof ContextEndedMessage)) {
                        getContexts().get(msgCtxId).processMessage(msg);
                    }
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(LogicProgrammingAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Test
    public void testModel1() {
        Platform platform = new DefaultPlatform();

        LogicProgrammingAgent p1 = new TestLogicProgrammingAgent(platform, new AgentId("agent1"));
        LogicProgrammingAgent p2 = new TestLogicProgrammingAgent(platform, new AgentId("agent2"));
        LogicProgrammingAgent p3 = new TestLogicProgrammingAgent(platform, new AgentId("agent3"));

        p1.getRules().add(Rule.createRuleHead(new Constant("agent1:a")).addToBody(new Constant("agent2:b")));
        p2.getRules().add(Rule.createRuleHead(new Constant("agent2:b")).addToBody(new Constant("agent3:c")));
        p3.getRules().add(Rule.createRuleHead(new Constant("agent3:c")));

        Set<Literal> p1model = new HashSet<>();
        p1model.add(new Constant("agent1:a"));
        p1model.add(new Constant("agent2:b"));

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p3);
        executor.shutdown();

        ContextId ctxId = p1.start();

        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        Assert.assertEquals(p1.getContexts().get(ctxId).getSmallestModel(), p1model);
    }

    @Test
    public void testModel2() {
        Platform platform = new DefaultPlatform();

        LogicProgrammingAgent p1 = new TestLogicProgrammingAgent(platform, new AgentId("agent1"));
        LogicProgrammingAgent p2 = new TestLogicProgrammingAgent(platform, new AgentId("agent2"));
        LogicProgrammingAgent p3 = new TestLogicProgrammingAgent(platform, new AgentId("agent3"));

        p1.getRules().add(Rule.createRuleHead(new Constant("agent1:a")).addToBody(new Constant("agent2:b")));
        p2.getRules().add(Rule.createRuleHead(new Constant("agent2:b")).addToBody(new Constant("agent3:c")));
        p3.getRules().add(Rule.createRuleHead(new Constant("agent3:c")).addToBody(new Constant("agent1:a")));

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p3);
        executor.shutdown();

        ContextId ctxId = p1.start();

        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        Assert.assertEquals(p1.getContexts().get(ctxId).getSmallestModel().isEmpty(), true);
    }

    @Test
    public void testModel3() {
        Platform platform = new DefaultPlatform();

        LogicProgrammingAgent p1 = new TestLogicProgrammingAgent(platform, new AgentId("agent1"));
        LogicProgrammingAgent p2 = new TestLogicProgrammingAgent(platform, new AgentId("agent2"));
        LogicProgrammingAgent p3 = new TestLogicProgrammingAgent(platform, new AgentId("agent3"));
        LogicProgrammingAgent p4 = new TestLogicProgrammingAgent(platform, new AgentId("agent4"));

        p1.getRules().add(Rule.createRuleHead(new Constant("agent1:a")).addToBody(new Constant("agent2:b")));
        p1.getRules().add(Rule.createRuleHead(new Constant("agent1:c")).addToBody(new Constant("agent3:d")));
        p2.getRules().add(Rule.createRuleHead(new Constant("agent2:b")).addToBody(new Constant("agent3:d")));
        p3.getRules().add(Rule.createRuleHead(new Constant("agent3:d")).addToBody(new Constant("agent4:e")));
        p4.getRules().add(Rule.createRuleHead(new Constant("agent4:e")));

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p3);
        executor.execute(p4);
        executor.shutdown();

        ContextId ctxId = p1.start();

        Set<Literal> p1model = new HashSet<>();
        p1model.add(new Constant("agent1:a"));
        p1model.add(new Constant("agent1:c"));
        p1model.add(new Constant("agent2:b"));
        p1model.add(new Constant("agent3:d"));

        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        Assert.assertEquals(p1.getContexts().get(ctxId).getSmallestModel(), p1model);
    }

    @Test
    public void testModel4() {
        Platform platform = new DefaultPlatform();

        LogicProgrammingAgent p1 = new TestLogicProgrammingAgent(platform, new AgentId("agent1"));
        LogicProgrammingAgent p2 = new TestLogicProgrammingAgent(platform, new AgentId("agent2"));
        LogicProgrammingAgent p3 = new TestLogicProgrammingAgent(platform, new AgentId("agent3"));

        p1.getRules().add(Rule.createRuleHead(new Constant("agent1:a")).addToBody(new Constant("agent2:b")));
        p2.getRules().add(Rule.createRuleHead(new Constant("agent2:b")).addToBody(new Constant("agent3:c"), new Constant("agent1:a")));
        p2.getRules().add(Rule.createRuleHead(new Constant("agent3:c")).addToBody(new Constant("agent2:b")));

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p3);
        executor.shutdown();

        ContextId ctxId = p1.start();

        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        Assert.assertEquals(p1.getContexts().get(ctxId).getSmallestModel().isEmpty(), true);
    }

    @Test
    public void testModel5() {
        Platform platform = new DefaultPlatform();

        LogicProgrammingAgent p1 = new TestLogicProgrammingAgent(platform, new AgentId("agent1"));
        LogicProgrammingAgent p2 = new TestLogicProgrammingAgent(platform, new AgentId("agent2"));
        LogicProgrammingAgent p3 = new TestLogicProgrammingAgent(platform, new AgentId("agent3"));

        p1.getRules().add(Rule.createRuleHead(new Constant("agent1:a")).addToBody(new Constant("agent2:b"), new Constant("agent3:c")));
        p2.getRules().add(Rule.createRuleHead(new Constant("agent2:b")).addToBody(new Constant("agent1:a"), new Constant("agent3:c")));
        p2.getRules().add(Rule.createRuleHead(new Constant("agent3:c")).addToBody(new Constant("agent1:a"), new Constant("agent2:b")));

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p3);
        executor.shutdown();

        ContextId ctxId = p1.start();

        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        Assert.assertEquals(p1.getContexts().get(ctxId).getSmallestModel().isEmpty(), true);
    }

    @Test
    public void testModel6() {
        Platform platform = new DefaultPlatform();

        LogicProgrammingAgent p1 = new TestLogicProgrammingAgent(platform, new AgentId("agent1"));
        LogicProgrammingAgent p2 = new TestLogicProgrammingAgent(platform, new AgentId("agent2"));
        LogicProgrammingAgent p3 = new TestLogicProgrammingAgent(platform, new AgentId("agent3"));

        p1.getRules().add(Rule.createRuleHead(new Constant("agent1:a")).addToBody(new Constant("agent1:b")));
        p1.getRules().add(Rule.createRuleHead(new Constant("agent1:b")));
        p2.getRules().add(Rule.createRuleHead(new Constant("agent2:b")).addToBody(new Constant("agent3:c")));
        p3.getRules().add(Rule.createRuleHead(new Constant("agent3:c")));

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p3);
        executor.shutdown();

        Set<Literal> p1model = new HashSet<>();
        p1model.add(new Constant("agent1:a"));
        p1model.add(new Constant("agent1:b"));

        ContextId ctxId = p1.start();

        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        Assert.assertEquals(p1.getContexts().get(ctxId).getSmallestModel(), p1model);
    }

    @Test
    public void testModel7() {
        Platform platform = new DefaultPlatform();

        LogicProgrammingAgent p1 = new TestLogicProgrammingAgent(platform, new AgentId("agent1"));
        LogicProgrammingAgent p2 = new TestLogicProgrammingAgent(platform, new AgentId("agent2"));
        LogicProgrammingAgent p3 = new TestLogicProgrammingAgent(platform, new AgentId("agent3"));
        LogicProgrammingAgent p4 = new TestLogicProgrammingAgent(platform, new AgentId("agent4"));

        p1.getRules().add(Rule.createRuleHead(new Constant("agent1:a")).addToBody(new Constant("agent1:b")));
        p1.getRules().add(Rule.createRuleHead(new Constant("agent1:b")).addToBody(new Constant("agent2:a")));
        p2.getRules().add(Rule.createRuleHead(new Constant("agent2:a")));
        p3.getRules().add(Rule.createRuleHead(new Constant("agent3:c")).addToBody(new Constant("agent4:d")));
        p4.getRules().add(Rule.createRuleHead(new Constant("agent4:d")));

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(p1);
        executor.execute(p2);
        executor.execute(p3);
        executor.execute(p4);
        executor.shutdown();

        Set<Literal> p1model = new HashSet<>();
        p1model.add(new Constant("agent1:a"));
        p1model.add(new Constant("agent1:b"));
        p1model.add(new Constant("agent2:a"));

        ContextId ctxId = p1.start();

        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }

        Assert.assertEquals(p1.getContexts().get(ctxId).getSmallestModel(), p1model);
    }
}
