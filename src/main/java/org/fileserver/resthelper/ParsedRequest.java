package org.fileserver.resthelper;

import io.netty.handler.codec.http.HttpMethod;
import org.fileserver.resthelper.annotations.RequestType;

import java.util.List;
import java.util.Map;

public class ParsedRequest {
    private String uri;
    private Map<String, List<String>> params;
    private RequestType.Type httpMethod;

    public void setHttpMethod(HttpMethod httpMethod) {
        if (HttpMethod.GET.equals(httpMethod)) {
            this.httpMethod = RequestType.Type.Get;
        } else if (HttpMethod.POST.equals(httpMethod)) {
            this.httpMethod = RequestType.Type.Post;
        }
    }

    public void setParams(Map<String, List<String>> params) {
        this.params = params;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public RequestType.Type getHttpMethod() {
        return httpMethod;
    }

    public Map<String, List<String>> getParams() {
        return params;
    }

    public String getUri() {
        return uri;
    }
}
