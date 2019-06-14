package message.types;

import java.io.Serializable;

public class SearchOutsource implements Serializable {
    private SearchRequest searchRequest;
    private String dataBasePath;

    public SearchOutsource(SearchRequest searchRequest, String dataBasePath){
        this.searchRequest=searchRequest;
        this.dataBasePath=dataBasePath;
    }

    public SearchRequest getSearchRequest() {
        return searchRequest;
    }

    public String getDataBasePath() {
        return dataBasePath;
    }
}
