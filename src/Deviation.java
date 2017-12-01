
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.Arrays;
import java.util.IntSummaryStatistics;


public class Deviation extends Agent {
    private AgentType agent;
    private AID[] managers;

    protected void setup() {
        agent = new AgentType(this.getAID(),"Deviation", (Math.random() * 1000));
        System.out.println("Hallo! Deviation Agent " + getAID().getName() + " is ready.");
        System.out.println("Trying to find Manager ");
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Manager");
        sd.setName("Calculate-MinMax-Deviation");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Found the following " + result.length + " managers:");
            managers = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                managers[i] = result[i].getName();
                System.out.println(managers[i].getName());
            }
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviour(new SignUp());
        addBehaviour(new CalculateDev());
    }
    private class SignUp extends OneShotBehaviour{
        public void action() {
            ACLMessage reg = new ACLMessage(ACLMessage.INFORM);
            for (int i = 0; i < managers.length; ++i) {
                reg.addReceiver(managers[i]);
            }
            try {
                reg.setContentObject(agent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            reg.setConversationId("FindManager");
            reg.setContent("add me to your list!");
            System.out.println(myAgent.getAID()+"send register inform to manager");
            myAgent.send(reg);
        }
    }
    private class CalculateDev extends CyclicBehaviour {
        public void action() {
            RandomArray array = new RandomArray();
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
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
                System.out.println("Agent "+getLocalName()+" - Received Min_MAX Request from "+msg.getSender().getLocalName());
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(ans);
                myAgent.send(reply);
            }
            else {
                block();
            }

        }
    }
}














//import jade.core.Agent;
//import jade.core.behaviours.*;
//import jade.lang.acl.ACLMessage;
//import jade.lang.acl.MessageTemplate;
//import jade.domain.DFService;
//import jade.domain.FIPAException;
//import jade.domain.FIPAAgentManagement.DFAgentDescription;
//import jade.domain.FIPAAgentManagement.ServiceDescription;
//
//import java.util.*;
//
//
//public class Deviation extends Agent {
//    private Hashtable catalogue;
//
//
//    // Put agent initializations here
//    protected void setup() {
//
//        catalogue = new Hashtable();
//        DFAgentDescription dfd = new DFAgentDescription();
//        dfd.setName(getAID());
//        ServiceDescription sd = new ServiceDescription();
//        sd.setType("Calculate-Deviation");
//        sd.setName("Calculate-MinMax-Deviation");
//        dfd.addServices(sd);
//        try {
//            DFService.register(this, dfd);
//        }
//        catch (FIPAException fe) {
//            fe.printStackTrace();
//        }
//
//        // Add the behaviour serving queries from buyer agents
//        addBehaviour(new OfferRequestsServer());
//
//        // Add the behaviour serving purchase orders from buyer agents
//        addBehaviour(new PurchaseOrdersServer());
//
//        // Printout a welcome message (for symmetry PCW)
//        System.out.println("Deviation-agent" + getAID().getName() + " is ready.");
//    }
//
//    protected void takeDown() {
//        try {
//            DFService.deregister(this);
//        }
//        catch (FIPAException fe) {
//            fe.printStackTrace();
//        }
//
//        System.out.println("Deviation-agent "+getAID().getName()+" terminating.");
//    }
//
//    /**
//     This is invoked by the GUI when the user adds a new book for sale
//     */
//    public void updateCatalogue(final String title, final int price) {
//        addBehaviour(new OneShotBehaviour() {
//            public void action() {
//                catalogue.put(title, new Integer(price));
//                System.out.println(title+" inserted into catalogue. Price = "+price);
//            }
//        } );
//    }
//
//    /**
//     Inner class OfferRequestsServer.
//     This is the behaviour used by Book-seller agents to serve incoming requests
//     for offer from buyer agents.
//     If the requested book is in the local catalogue the seller agent replies
//     with a PROPOSE message specifying the price. Otherwise a REFUSE message is
//     sent back.
//     */
//    private class OfferRequestsServer extends CyclicBehaviour {
//        public void action() {
//            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
//            ACLMessage msg = myAgent.receive(mt);
//            if (msg != null) {
//                // CFP Message received. Process it
//                String title = msg.getContent();
//                ACLMessage reply = msg.createReply();
//                System.out.println(title+" get CFP "+msg.getSender().getName());
//                reply.setPerformative(ACLMessage.PROPOSE);
//                reply.setContent("20");
//                myAgent.send(reply);
//            }
//            else {
//                block();
//            }
//        }
//    }  // End of inner class OfferRequestsServer
//
//    /**
//     Inner class PurchaseOrdersServer.
//     This is the behaviour used by Book-seller agents to serve incoming
//     offer acceptances (i.e. purchase orders) from buyer agents.
//     The seller agent removes the purchased book from its catalogue
//     and replies with an INFORM message to notify the buyer that the
//     purchase has been sucesfully completed.
//     */
//    private class PurchaseOrdersServer extends CyclicBehaviour {
//        public void action() {
//            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
//            ACLMessage msg = myAgent.receive(mt);
//            if (msg != null) {
//                String title = msg.getContent();
//                ACLMessage reply = msg.createReply();
//
//                reply.setPerformative(ACLMessage.INFORM);
//                reply.setContent("ok");
//                System.out.println(title+" sold to agent "+msg.getSender().getName());
//                myAgent.send(reply);
//            }
//            else {
//                block();
//            }
//        }
//    }  // End of inner class OfferRequestsServer
//}


//ali:Client;manager:Manager;dev:Deviation