package v1.sulphurapi.structure;

import v1.sulphurapi.core.SAPI;
import v1.sulphurapi.handlers.endpoints.EndpointsHandler;
import v1.sulphurapi.handlers.auth.SecurityHandler;
import v1.sulphurapi.handlers.openapi.SchemaHandler;
import v1.sulphurapi.interfaces.GUIActionInterface;
import v1.sulphurapi.utils.SwaggerReader;

import javax.swing.*;
import java.util.concurrent.locks.ReentrantLock;


public class APILoader implements GUIActionInterface {
    private static final ReentrantLock lock = new ReentrantLock();
    private String fileLoadedPath;
    private SwaggerReader loadedAPI;
    private GUIManager guiManager;
    private EndpointsHandler endpointsHandler;
    private SchemaHandler schemaHandler;
    private SecurityHandler securityHandler;


    /**
     * This class is responsible for loading the API file and initializing the UI components.
     * It uses a background thread to load the API file and updates the UI once the loading is complete.
     * It Holds the references to handlers needed for updating the grid
     *
     * @param manager The manager hosting the main UI components
     * @param pathField The JTextField to display the path of the loaded API file.
     */
   public APILoader(GUIManager manager, JTextField pathField) {
       runAsyncAction(() -> {
           lock.lock();
           try {
               /// TODO: Remove the if block when the Extension is fully implemented

               JFileChooser file = new JFileChooser();
               file.setFileSelectionMode(JFileChooser.FILES_ONLY);
               int returnValue = file.showOpenDialog(SAPI.getUIPanel());
               if (returnValue == JFileChooser.APPROVE_OPTION) {
                   this.fileLoadedPath = file.getSelectedFile().getAbsolutePath();
                   this.loadedAPI = new SwaggerReader(this.fileLoadedPath);
               }

               this.guiManager = manager;

               pathField.setText(fileLoadedPath);
               SAPI.loadAPI(this.loadedAPI.getParsedAPI());
               if (SAPI.getLoadedAPI() != null) {
                   SAPI.getAPI().logging().logToOutput("Loading new API file: " + this.loadedAPI.toString());
                   this.endpointsHandler = new EndpointsHandler(this);
                   this.schemaHandler = new SchemaHandler(this);
                   this.securityHandler = new SecurityHandler(this);
               } else {
                   SAPI.getAPI().logging().logToOutput("No paths found in the loaded API");
               }
           } finally {
                lock.unlock();
           }
       },"API File Loading Done");
    }

    public EndpointsHandler getEndpointsHandler() {
        return endpointsHandler;
    }

    public SchemaHandler getSchemaHandler() {
        return schemaHandler;
    }

    public SecurityHandler getSecurityHandler() {
        return securityHandler;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }
}
