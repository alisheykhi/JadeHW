
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;


public class MIN_MAX extends Agent {
    private AgentType agent;
    private AID[] managers;

    protected void setup() {
        agent = new AgentType(this.getAID(),"MinMax", (Math.random() * 1000));
        System.out.println("Hallo! MinMax Agent " + getAID().getName() + " is ready.");
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
    }
    private class SignUp extends OneShotBehaviour{
        public void action() {
            ACLMessage reg = new ACLMessage(ACLMessage.INFORM);
            for (int i = 0; i < managers.length; ++i) {
                reg.addReceiver(managers[i]);
            }
            try {
                reg.setContentObject((Serializable) agent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            reg.setConversationId("FindManager");
            System.out.println(myAgent.getAID()+"send register inform to manager");
            myAgent.send(reg);
        }
    }
}
