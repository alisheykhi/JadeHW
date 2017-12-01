
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;
public class StartSystem {
    public static void main(String args[]) throws InterruptedException, StaleProxyException {
        final Runtime runTime = Runtime.instance();
        runTime.setCloseVM(true);
        Profile mainProfile = new ProfileImpl(true);
        AgentContainer mainContainer = runTime.createMainContainer(mainProfile);
        AgentController rma = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
        rma.start();
        Thread.sleep(900);

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
