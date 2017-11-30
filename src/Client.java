import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;


public class Client extends Agent{
    private AID manager = new AID();
    protected void setup() {
        addBehaviour(new TickerBehaviour(this,10000) {
            public void onTick() {
                String array = Utility.randomArray(10);
                manager = new AID("manager@192.168.21.100:1099/JADE");
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                cfp.addReceiver(manager);
                cfp.setContent(array);
                cfp.setConversationId("FindAgent");
                cfp.setReplyWith("cfp"+System.currentTimeMillis());
                System.out.println(myAgent.getAID()+cfp.getContent());
                myAgent.send(cfp);
            }
        } );
    }
}


