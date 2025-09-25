package v1.sulphurapi.core;

import burp.api.montoya.MontoyaApi;
import io.swagger.v3.oas.models.OpenAPI;
import v1.sulphurapi.settings.ControlPanel;

import javax.swing.*;

public final class SAPI {

    /**
     * Singleton instance of the MontoyaApi.
     */
    private static MontoyaApi INSTANCE = null;

    /**
     * Loaded OpenAPI instance. Null if no OpenAPI has been loaded.
     */
    private static OpenAPI LOADEDAPI = null;

    /**
     * UIPanel instance. Null if no UI panel has been loaded.
     */
    private static JPanel UIPanel = null;

    /**ControlPanel instance. Null if no UI Panel has been loaded*/
    private static ControlPanel controlPanel = null;


    private SAPI() {}

    public static MontoyaApi getAPI() {
        return INSTANCE;
    }
    public static OpenAPI getLoadedAPI() {return LOADEDAPI;}
    public static JPanel getUIPanel() {return UIPanel;}

    /**
     * Saves the MontoyaApi instance.
     *
     * @param api The MontoyaApi instance to initialize.
     */
    public static void initAPI(MontoyaApi api) {
        if (INSTANCE == null) {
            INSTANCE = api;
        }
    }

    /**
     * Saves the OpenAPI instance to be used later.
     *
     * @param api The OpenAPI instance to load.
     */
    public static void loadAPI(OpenAPI api) {
        LOADEDAPI = api;
    }

    /**
     * Saves the UIPanel instance to be used later.
     *
     * @param uiPanel The JPanel instance to load.
     */
    public static void loadUIPanel(JPanel uiPanel) {UIPanel = uiPanel;}

    /**
     * Load settings panel
     */
    public static void loadControlPanel(ControlPanel panel) {controlPanel = panel;}

    public static Boolean getDebugMode() {
        if (controlPanel != null) {
            return controlPanel.getDebugMode();
        }
        return false;
    }

    /**  Default logging method to log information messages in burp suite. */
    public static void logInfo(String message) {
        if (SAPI.getDebugMode()) {
            INSTANCE.logging().raiseInfoEvent(message);
        }
    }

    /**  Default logging method to log error messages in burp suite. */
    public static void logError(String message) {
        if (SAPI.getDebugMode()) {
            INSTANCE.logging().raiseErrorEvent(message);
        }
    }
}
