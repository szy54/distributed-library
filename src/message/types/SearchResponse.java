package message.types;

import java.io.Serializable;

public class SearchResponse implements Serializable {
    private String name;
    private Boolean inDatabase;
    private String clientPath;
    private Double price;

    public SearchResponse(String name, Boolean inDatabase, String clientPath, Double price){
        this.name=name;
        this.inDatabase=inDatabase;
        this.clientPath=clientPath;
        this.price=price;
    }

    public Boolean getInDatabase() {
        return inDatabase;
    }

    public String getClientPath() {
        return clientPath;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }
}
