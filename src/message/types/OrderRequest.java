package message.types;


import java.io.Serializable;

public class OrderRequest implements Serializable {
    private String name;
    private String clientPath;

    public OrderRequest(String name, String clientPath){
        this.name=name;
        this.clientPath=clientPath;
    }

    public String getName(){
        return name;
    }

    public String getClientPath() {
        return clientPath;
    }
}
