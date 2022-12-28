package org.fileserver;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

class RequestUtils {

    static StringBuilder formatParams(HttpRequest request) {
        StringBuilder responseData = new StringBuilder();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        if (!params.isEmpty()) {
            for (Entry<String, List<String>> p : params.entrySet()) {
                String key = p.getKey();
                List<String> vals = p.getValue();
                for (String val : vals) {
                    responseData.append("Parameter: ")
                            .append(key.toUpperCase())
                            .append(" = ")
                            .append(val.toUpperCase())
                            .append("\r\n");
                }
            }
            responseData.append("\r\n");
        }
        return responseData;
    }

    static ParsedRequest getParsedRequest(HttpRequest request) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setUri(queryStringDecoder.path());
        parsedRequest.setUri(queryStringDecoder.parameters());
        parsedRequest.setHttpMethod(request.method());
        return parsedRequest;
    }

    public static class ParsedRequest {
        private String uri;
        private Map<String, List<String>> params;
        private HttpMethod httpMethod;

        public void setHttpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        public void setParams(Map<String, List<String>> params) {
            this.params = params;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public Map<String, List<String>> getParams() {
            return params;
        }

        public String getUri() {
            return uri;
        }
    }

    static StringBuilder formatBody(HttpContent httpContent) {
        StringBuilder responseData = new StringBuilder();
        ByteBuf content = httpContent.content();
        if (content.isReadable()) {
            responseData.append(content.toString(CharsetUtil.UTF_8)
                    .toUpperCase());
            responseData.append("\r\n");
        }
        return responseData;
    }

    static StringBuilder evaluateDecoderResult(HttpObject o) {
        StringBuilder responseData = new StringBuilder();
        DecoderResult result = o.decoderResult();

        if (!result.isSuccess()) {
            responseData.append("..Decoder Failure: ");
            responseData.append(result.cause());
            responseData.append("\r\n");
        }

        return responseData;
    }

    static StringBuilder prepareLastResponse(HttpRequest request, LastHttpContent trailer) {
        StringBuilder responseData = new StringBuilder();
        responseData.append("Good Bye!\r\n");

        if (!trailer.trailingHeaders()
                .isEmpty()) {
            responseData.append("\r\n");
            for (CharSequence name : trailer.trailingHeaders()
                    .names()) {
                for (CharSequence value : trailer.trailingHeaders()
                        .getAll(name)) {
                    responseData.append("P.S. Trailing Header: ");
                    responseData.append(name)
                            .append(" = ")
                            .append(value)
                            .append("\r\n");
                }
            }
            responseData.append("\r\n");
        }
        return responseData;
    }

}
