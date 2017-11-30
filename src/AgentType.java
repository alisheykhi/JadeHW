import jade.core.AID;

import java.io.Serializable;

public class AgentType implements Serializable {

    private AID name;
    private String Type;
    private Double price;

    public AgentType(AID name, String type, Double price) {
        this.name = name;
        Type = type;
        this.price = price;
    }

    public AID getName() {
        return name;
    }

    public void setName(AID name) {
        this.name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
