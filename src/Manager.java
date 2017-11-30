
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
import java.util.*;


public class Manager  extends Agent {
    private List<AgentType> minMax= new ArrayList<>();
    private List<AgentType> deviation = new ArrayList<>();
    private AgentType bestMinMax = new AgentType(this.getAID(),"MinMax", Double.POSITIVE_INFINITY);
    private AgentType bestDeviation = new AgentType(this.getAID(),"Deviation", Double.POSITIVE_INFINITY);
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
        System.out.println("Hallo! Manager " + getAID().getName() + " is ready.");

        addBehaviour(new RegisterAgent());
    }

    private class RegisterAgent extends CyclicBehaviour {
        AgentType sender;
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                try {
                    sender = (AgentType)msg.getContentObject();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                switch (sender.getType()){
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
            else {
                block();
            }
        }
    }

    private void updateMinMax(final AgentType currentAgent) {
        if (bestMinMax.getPrice() > currentAgent.getPrice()){
            addBehaviour(new OneShotBehaviour() {
                public void action() {
                    bestMinMax = currentAgent;
                    System.out.println("Best MinMax Updated by "+currentAgent.getName()+"and Price "+currentAgent.getPrice());
                }
            } );
        }
    }

    private void updateDeviation(final AgentType currentAgent) {
        if (bestDeviation.getPrice() > currentAgent.getPrice()){
            addBehaviour(new OneShotBehaviour() {
                public void action() {
                    bestDeviation = currentAgent;
                    System.out.println("Best Deviation Updated by "+currentAgent.getName()+"and Price "+currentAgent.getPrice());
                }
            } );
        }
    }
}

//    protected void setup() {
//        addBehaviour(new CyclicBehaviour(this) {
//            public void action() {
//                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
//                ACLMessage msg = myAgent.receive(mt);
//                if (msg != null) {
//                    // CFP Message received. Process it
//                    String array = msg.getContent(); // this is one of the goods
//                    String maxvalue = Utility.maxValue(array);
//                    ACLMessage reply = msg.createReply();
//                    if (maxvalue != "0") {
//                        reply.setPerformative(ACLMessage.PROPOSE);
//                        reply.setContent(maxvalue);
//                    }
//                    else {
//                        // The requested good is NOT available for sale.
//                        reply.setPerformative(ACLMessage.REFUSE);
//                        reply.setContent("not-available");
//                    }
//                    System.out.println(myAgent.getAID()+reply.getContent());
//                    myAgent.send(reply);
//                }
//                else {
//                    block();
//                }
//            }
//        } );
//    }