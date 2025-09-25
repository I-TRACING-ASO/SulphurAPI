package v1.sulphurapi.handlers.http;

import burp.api.montoya.http.handler.*;
import v1.sulphurapi.structure.GUIManager;

public class HTTPHandler implements HttpHandler {

    private GUIManager ui;

    public HTTPHandler(GUIManager ui) {
        this.ui = ui;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        return null;
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        return null;
    }
}
