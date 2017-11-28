
import jade.core.Agent;
import jade.core.AID;

public class Ali extends Agent{

    @Override
    protected void setup() {
        String nickname = "Ali";
        AID id = new AID(nickname, AID.ISLOCALNAME);
        System.out.println("Hello everybody");
        System.out.println("I'm "+ getAID().getLocalName()+" and i'm ready!"+
        getAID().getName());
    }
}
