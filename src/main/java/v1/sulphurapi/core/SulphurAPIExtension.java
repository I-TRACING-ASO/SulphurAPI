package v1.sulphurapi.core;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import v1.sulphurapi.handlers.http.HTTPHandler;
import v1.sulphurapi.settings.ControlPanel;
import v1.sulphurapi.structure.GUIManager;
import v1.sulphurapi.handlers.montoya.UnloadingHandler;


public class SulphurAPIExtension implements BurpExtension {

    /**
     * Initializes the SulphurAPI extension.
     *
     * @param api The MontoyaApi instance provided by Burp Suite.
     */
    @Override
    public void initialize(MontoyaApi api) {
        SAPI.initAPI(api);

        ControlPanel settings = new ControlPanel();

        api.userInterface().registerSettingsPanel(settings.getSettingsPanel());

        SAPI.loadControlPanel(settings);

        api.extension().setName("SulphurAPI");

        GUIManager ui = new GUIManager();

        api.userInterface().registerSuiteTab("SulphurAPI", ui.getUi());

        api.logging().logToOutput("SulphurAPI initialized");
        HTTPHandler handler = new HTTPHandler(ui);

        api.extension().registerUnloadingHandler(new UnloadingHandler(handler));
        api.http().registerHttpHandler(handler);
    }
}
