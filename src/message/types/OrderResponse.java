package message.types;

import java.io.Serializable;

public class OrderResponse implements Serializable {
    private Boolean isSuccesful;
    private String name;
    private Double price;

    public OrderResponse(Boolean isSuccesful, String name, Double price){
        this.isSuccesful=isSuccesful;
        this.name=name;
        this.price=price;
    }

    public Boolean getSuccesful() {
        return isSuccesful;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }
}
