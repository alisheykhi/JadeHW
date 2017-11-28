import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import jade.lang.acl.MessageTemplate;

public class Manager  extends Agent {
    protected void setup() {
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                ACLMessage msg = myAgent.receive(mt);
                if (msg != null) {
                    // CFP Message received. Process it
                    String array = msg.getContent(); // this is one of the goods
                    String maxvalue = Utility.maxValue(array);
                    ACLMessage reply = msg.createReply();
                    if (maxvalue != "0") {
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(maxvalue);
                    }
                    else {
                        // The requested good is NOT available for sale.
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("not-available");
                    }
                    System.out.println(myAgent.getAID()+reply.getContent());
                    myAgent.send(reply);
                }
                else {
                    block();
                }
            }
        } );
    }
}