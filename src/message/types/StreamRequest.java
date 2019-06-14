package message.types;

import java.io.Serializable;

public class StreamRequest implements Serializable {
    private String name;
    private String clientPath;

    public StreamRequest(String name, String clientPath){
        this.name=name;
        this.clientPath=clientPath;
    }

    public String getName() {
        return name;
    }

    public String getClientPath() {
        return clientPath;
    }
}
