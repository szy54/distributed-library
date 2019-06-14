package message.types;

import java.io.Serializable;

public class DBSearchRequest implements Serializable {
    private SearchRequest searchRequest;
    private String dbPath;

    public DBSearchRequest(SearchRequest searchRequest, String dbPath){
        this.searchRequest=searchRequest;
        this.dbPath=dbPath;
    }

    public SearchRequest getSearchRequest() {
        return searchRequest;
    }

    public String getDbPath() {
        return dbPath;
    }
}
