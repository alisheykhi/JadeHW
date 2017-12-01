import jade.core.AID;

import java.io.Serializable;

public class RequestAgent implements Serializable{
    private AgentType minMax;
    private AgentType deviation;

    public RequestAgent(AgentType minMax, AgentType deviation) {
        this.minMax = minMax;
        this.deviation = deviation;
    }

    public AgentType getMinMax() {
        return minMax;
    }

    public void setMinMax(AgentType minMax) {
        this.minMax = minMax;
    }

    public AgentType getDeviation() {
        return deviation;
    }

    public void setDeviation(AgentType deviation) {
        this.deviation = deviation;
    }
}
