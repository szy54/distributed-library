package message.types;

import java.io.Serializable;

public class SearchRequest implements Serializable {
    private String name;
    private String clientPath;

    public SearchRequest(String name, String clientPath){
        this.name = name;
        this.clientPath=clientPath;
    }

    public String getName() {
        return name;
    }

    public String getClientPath() {
        return clientPath;
    }
}
