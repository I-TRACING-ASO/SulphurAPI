package v1.sulphurapi.handlers.montoya;

import burp.api.montoya.extension.ExtensionUnloadingHandler;
import v1.sulphurapi.handlers.http.HTTPHandler;

public class UnloadingHandler implements ExtensionUnloadingHandler {

    private HTTPHandler handler;

    public UnloadingHandler(HTTPHandler handler){
        this.handler=handler;
    }

    @Override
    public void extensionUnloaded() {
    }
}
