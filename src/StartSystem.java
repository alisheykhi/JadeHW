
import jade.core.Agent;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.*;
public class StartSystem {
    public static void main(String args[]) throws InterruptedException, StaleProxyException {
        // Get a hold on JADE runtime
        final Runtime runTime = Runtime.instance();

        // Exit the JVM when there are no more containers around
        runTime.setCloseVM(true);

        // Create a profile and the main container and start RMA
        Profile mainProfile = new ProfileImpl(true);
        AgentContainer mainContainer = runTime.createMainContainer(mainProfile);
        AgentController rma = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
        rma.start();
        Thread.sleep(900);

        // Creo uno Sniffer
        AgentController sniffer = mainContainer.createNewAgent(
                "mySniffer", "jade.tools.sniffer.Sniffer",
                new Object[]{"BuyerAgent1;BuyerAgent2;ShipperAgent1;ShipperAgent2"});
        sniffer.start();
        Thread.sleep(900);

        // Creo un Introspector
        AgentController introspector = mainContainer.createNewAgent(
                "myIntrospector", "jade.tools.introspector.Introspector",
                null);
        introspector.start();
        Thread.sleep(900);

        /////////////////////////////////////////
        // Prepare for create and fire new agents:

		/* 	Create a new profile and a new non-main container, connecting to the
			default main container (i.e. on this host, port 1099)
			NB. Two containers CAN'T share the same Profile object: create a new one. */
        Profile anotherProfile;
        AgentContainer anotherContainer;
        AgentController agent;

        anotherProfile = new ProfileImpl(false);
        anotherContainer = runTime.createAgentContainer(anotherProfile);
        System.out.println("Starting up a Manager...");
        agent = anotherContainer.createNewAgent("manager", "Agents.Manager", null);
        agent.start();
        Thread.sleep(900);
        System.out.println("Starting up a MIN_MAX...");
        agent = anotherContainer.createNewAgent("minmax1", "Agents.MIN_MAX", null);
        agent.start();
        Thread.sleep(900);
        System.out.println("Starting up a MIN_MAX...");
        agent = anotherContainer.createNewAgent("minmax2", "Agents.MIN_MAX", null);
        agent.start();
        Thread.sleep(900);
        System.out.println("Starting up a MIN_MAX...");
        agent = anotherContainer.createNewAgent("minmax3", "Agents.MIN_MAX", null);
        agent.start();
        Thread.sleep(900);
        System.out.println("Starting up a MIN_MAX...");
        agent = anotherContainer.createNewAgent("minmax4", "Agents.MIN_MAX", null);
        agent.start();
        Thread.sleep(900);
        System.out.println("Starting up a Deviation...");
        agent = anotherContainer.createNewAgent("Deviation1", "Agents.Deviation", null);
        agent.start();
        Thread.sleep(900);
        agent = anotherContainer.createNewAgent("Deviation2", "Agents.Deviation", null);
        agent.start();
        Thread.sleep(900);
        agent = anotherContainer.createNewAgent("Deviation3", "Agents.Deviation", null);
        agent.start();
        Thread.sleep(900);
        agent = anotherContainer.createNewAgent("Deviation4", "Agents.Deviation", null);
        agent.start();
        Thread.sleep(900);
        System.out.println("Starting up a Deviation...");
        agent = anotherContainer.createNewAgent("client", "Agents.Client", null);
        agent.start();
        Thread.sleep(900);

    }


}
