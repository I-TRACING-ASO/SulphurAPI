package v1.sulphurapi.handlers.http;

import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import v1.sulphurapi.core.SAPI;
import v1.sulphurapi.handlers.auth.SecurityHandler;


import java.util.List;

public class RequestHandler implements Runnable {
    private final String url;
    private final String method;
    private final String body;
    private final List<String> headers;
    private HttpRequestResponse response;

    public RequestHandler(String url, String method, String body, List<String> headers) {
        this.url = url;
        this.method = method;
        this.body = body;
        this.headers = headers;
    }

    /**
     * This method is used to send an HTTP request to the specified URL with the given method, body, and headers.
     * It locks the operation to ensure thread safety.
     */
    @Override
    public void run() throws IllegalArgumentException {
        if (url != null && !url.isEmpty()) {
            HttpRequest request = HttpRequest.httpRequestFromUrl(url);
            request = request.withMethod(method);
            if (body != null && !body.isEmpty()) {
                request = request.withBody(body);
            }
            if (SecurityHandler.APIKeyData != null && SecurityHandler.APIKeyData.getLeft() != null) {
                switch (SecurityHandler.APIKeyData.getLeft()) {
                    case QUERY:
                        request = request.withAddedParameters(HttpParameter.urlParameter(SecurityHandler.APIKeyData.getMiddle(), SecurityHandler.APIKeyData.getRight()));
                        break;
                    case HEADER:
                        request = request.withAddedHeader(HttpHeader.httpHeader(SecurityHandler.APIKeyData.getMiddle(), SecurityHandler.APIKeyData.getRight()));
                        break;
                    case COOKIE:
                        request = request.withAddedParameters(HttpParameter.cookieParameter(SecurityHandler.APIKeyData.getMiddle(), SecurityHandler.APIKeyData.getRight()));
                        break;
                }
            }
            if (headers != null && !headers.isEmpty()) {
                for (String header : headers) {
                    request = request.withHeader(HttpHeader.httpHeader(header));
                }
            }
            this.response = SAPI.getAPI().http().sendRequest(request);
        } else {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
    }

    public HttpRequestResponse getRequestResponse() {
        return response;
    }
}
