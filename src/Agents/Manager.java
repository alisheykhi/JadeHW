package Agents;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import RequiredClass.AgentType;
import RequiredClass.RequestAgent;

import java.io.IOException;
import java.util.*;


public class Manager  extends Agent {
    private List<AgentType> minMax = new ArrayList<>();
    private List<AgentType> deviation = new ArrayList<>();
    private AgentType bestMinMax = new AgentType(this.getAID(), "MinMax", Double.POSITIVE_INFINITY);
    private AgentType bestDeviation = new AgentType(this.getAID(), "Deviation", Double.POSITIVE_INFINITY);

    protected void setup() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Manager");
        sd.setName("Calculate-MinMax-Deviation");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println(getAID().getLocalName()+":\tHello! Manager " + getAID().getLocalName()+ " is ready.");

        addBehaviour(new RegisterAgent());
        addBehaviour(new WaitForClientRequest());
    }

    private class RegisterAgent extends CyclicBehaviour {
        AgentType sender;

        public void action() {
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.MatchConversationId("register"));
            ACLMessage msg = this.myAgent.receive(mt);
            if (msg != null) {
                try {
                    sender =  (AgentType) msg.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                System.out.println(getAID().getLocalName()+":\treceived register inform ("+sender.getType()+") from " + sender.getName().getLocalName());
                switch (sender.getType()) {
                    case "MinMax":
                        minMax.add(sender);
                        updateMinMax(sender);
                        break;
                    case "Deviation":
                        deviation.add(sender);
                        updateDeviation(sender);
                        break;
                }
            }
        }
    }

    private class WaitForClientRequest extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchConversationId("findAgent"));
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                String content = msg.getContent();
                if ((content != null) && (content.indexOf("Please find MinMax and Deviation Agent") != -1)) {
                    System.out.println(getLocalName()+":\tReceived find request from " + msg.getSender().getLocalName());
                    reply.setPerformative(ACLMessage.INFORM);
                    RequestAgent ra = new RequestAgent(bestMinMax, bestDeviation);
                    try {
                        reply.setContentObject(ra);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(getLocalName()+":\tsend find inform to " + msg.getSender().getLocalName());
                    myAgent.send(reply);
                }
                else {
                    System.out.println("Receive find request from " + msg.getSender().getName()+ " with wrong content");
                }
            }

        }
    }

        private void updateMinMax(final AgentType currentAgent) {
            if (bestMinMax.getPrice() > currentAgent.getPrice()) {
                addBehaviour(new OneShotBehaviour() {
                    public void action() {
                        bestMinMax = currentAgent;
                        System.out.println(myAgent.getLocalName()+":\tBest MinMax Updated by " + currentAgent.getName().getLocalName() + " by Price " + currentAgent.getPrice());
                    }
                });
            }
        }

        private void updateDeviation(final AgentType currentAgent) {
            if (bestDeviation.getPrice() > currentAgent.getPrice()) {
                addBehaviour(new OneShotBehaviour() {
                    public void action() {
                        bestDeviation = currentAgent;
                        System.out.println(myAgent.getLocalName()+":\tBest Deviation Updated by " + currentAgent.getName().getLocalName() + " by Price " + currentAgent.getPrice());
                    }
                });
            }
        }
    }
