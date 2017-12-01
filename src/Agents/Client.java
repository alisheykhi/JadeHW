package Agents;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import RequiredClass.RandomArray;
import RequiredClass.RequestAgent;
import java.io.IOException;


public class Client extends Agent{
    private AID[] managers;
    private RandomArray randomArray;

    protected void setup() {
        System.out.println(getAID().getLocalName()+":\tHello! Client " + getAID().getLocalName()+ " is ready.");

        addBehaviour(new TickerBehaviour(this,10000) {
            public void onTick() {
                int step = 0;
                randomArray = new RandomArray((int)(Math.random() * 1000));
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("Manager");
                sd.setName("Calculate-MinMax-Deviation");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    managers = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        managers[i] = result[i].getName();
                    }
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
                addBehaviour(new RequestCalculation());
            }
        } );
    }
    private class RequestCalculation extends Behaviour {
        private RequestAgent bestPrice;
        private MessageTemplate mt;
        private MessageTemplate mtMinMax;
        private MessageTemplate mtDeviation;
        private String deviation;
        private String minMax;
        boolean minmaxflag = true;
        boolean deviationflag = true;
        private int step = 0;

        public void action() {
            switch (step) {
                case 0:
                    //send request to manager
                    ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                    for (int i = 0; i < managers.length; ++i) {
                        req.addReceiver(managers[i]);
                    }
                    try {
                        req.setContentObject(randomArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    req.setReplyWith("REQUEST"+System.currentTimeMillis());
                    req.setConversationId("findAgent");
                    req.setContent("Please find MinMax and Deviation Agent");
                    myAgent.send(req);
                    System.out.println(myAgent.getLocalName()+":\tSend request (find Agent) to manager"+managers[0].getLocalName());
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("findAgent"),
                            MessageTemplate.MatchInReplyTo(req.getReplyWith()));
                    step = 1;
                    break;
                case 1:
                    // receive from manager
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Reply received
                        if (reply.getPerformative() == ACLMessage.INFORM) {
                            try {
                                bestPrice = (RequestAgent) reply.getContentObject();
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }
                            if (bestPrice.getDeviation().getName() == managers[0] ||
                                    bestPrice.getMinMax().getName() == managers[0]){
                                System.out.println("there is no Agent for calculate deviation or minMax");
                            }
                        }
                        step = 2;
                    }
                    else {
                        block();
                    }
                    break;
                case 2:
                    // Send the calculation order to the best price agent
                    ACLMessage minmax = new ACLMessage(ACLMessage.REQUEST);
                    minmax.addReceiver(bestPrice.getMinMax().getName());
                    try {
                        minmax.setContentObject(randomArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    minmax.setConversationId("MinMax");
                    minmax.setReplyWith("order"+System.currentTimeMillis());
                    myAgent.send(minmax);
                    mtMinMax = MessageTemplate.and(MessageTemplate.MatchConversationId("MinMax"),
                            MessageTemplate.MatchInReplyTo(minmax.getReplyWith()));

                    ACLMessage dev = new ACLMessage(ACLMessage.REQUEST);
                    dev.addReceiver(bestPrice.getDeviation().getName());
                    try {
                        dev.setContentObject(randomArray);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dev.setConversationId("Deviation");
                    dev.setReplyWith("order"+System.currentTimeMillis());
                    myAgent.send(dev);
                    mtDeviation = MessageTemplate.and(MessageTemplate.MatchConversationId("Deviation"),
                            MessageTemplate.MatchInReplyTo(dev.getReplyWith()));
                    step = 3;
                    break;
                case 3:
                    // Receive the answers
                    ACLMessage replyMinMax = myAgent.receive(mtMinMax);
                    ACLMessage replyDeviation = myAgent.receive(mtDeviation);
                    if (replyMinMax != null && minmaxflag) {
                        if (replyMinMax.getPerformative() == ACLMessage.INFORM) {
                            minmaxflag =false;
                            System.out.println(myAgent.getLocalName()+":\tsuccessfully received answer from agent "+replyMinMax.getSender().getLocalName());
                            System.out.println(myAgent.getLocalName()+":\tMinMax = "+replyMinMax.getContent());
                            System.out.println(myAgent.getLocalName()+":\tPrice = "+bestPrice.getMinMax().getPrice());

                        }
                        else {
                            System.out.println("calculation refused in minmax");
                        }
                        if (!minmaxflag && !deviationflag) {
                            //myAgent.doDelete();
                            step = 4;
                        }
                    }
                    else if(replyDeviation != null && deviationflag) {
                        if (replyDeviation.getPerformative() == ACLMessage.INFORM) {
                            deviationflag = false;
                            System.out.println(myAgent.getLocalName()+":\tsuccessfully received answer from agent "+replyDeviation.getSender().getLocalName());
                            System.out.println(myAgent.getLocalName()+":\tdeviation = "+replyDeviation.getContent());
                            System.out.println(myAgent.getLocalName()+":\tPrice = "+bestPrice.getDeviation().getPrice());

                        }
                        else {
                            System.out.println("calculation refused in dev");
                        }
                        if (!minmaxflag && !deviationflag) {
                            //myAgent.doDelete();
                            step = 4;
                        }
                    }
                    break;
            }
        }
        public boolean done() {
            return (step == 4);
        }
    }
}


