package Agents;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import RequiredClass.AgentType;
import RequiredClass.RandomArray;

import java.io.IOException;
import java.util.Arrays;
import java.util.IntSummaryStatistics;


public class Deviation extends Agent {
    private AgentType agent;
    private AID[] managers;

    protected void setup() {
        agent = new AgentType(this.getAID(),"Deviation", (Math.random() * 1000));
        System.out.println(getAID().getLocalName()+":\tHello! Deviation Agent " + getAID().getLocalName() + "by price "+agent.getPrice()+" is ready.");
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Manager");
        sd.setName("Calculate-MinMax-Deviation");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            managers = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                managers[i] = result[i].getName();
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new SignUp());
        addBehaviour(new CalculateDev());
    }
    private class SignUp extends Behaviour {
        int done = 0;
        public void action() {
            ACLMessage reg = new ACLMessage(ACLMessage.INFORM);
            reg.setConversationId("register");
            for (AID manager : managers) {
                reg.addReceiver(manager);
            }
            try {
                reg.setContentObject(agent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                reg.setContentObject(agent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(myAgent.getAID().getLocalName()+ ":\tsend register inform (Deviation) to manager "+managers[0].getLocalName());
            myAgent.send(reg);
            done++;
        }
        public boolean done() {
            return done == 1;
        }
    }
    private class CalculateDev extends CyclicBehaviour {
        public void action() {
            RandomArray array = new RandomArray();
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchConversationId("Deviation"));
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                ACLMessage reply = msg.createReply();
                try {
                    array = (RandomArray) msg.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                IntSummaryStatistics stat = Arrays.stream(array.getRandomArray()).summaryStatistics();
                double mean = stat.getAverage();
                double variance =0;
                for (int i = 0; i < stat.getCount(); i++) {
                    variance += Math.pow(array.getRandomArray()[i]-mean, 2);
                }
                variance = variance/stat.getCount();
                double dev = Math.sqrt(variance);
                String ans = "Deviation is "+dev;
                System.out.println(getLocalName()+":\tReceived Min_MAX Request from "+msg.getSender().getLocalName());
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(ans);
                System.out.println(getLocalName()+":\tsend Deviation inform to "+msg.getSender().getLocalName());
                myAgent.send(reply);
            }
            else {
                block();
            }

        }
    }
}
