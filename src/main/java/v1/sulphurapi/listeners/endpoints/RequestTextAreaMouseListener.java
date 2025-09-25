package v1.sulphurapi.listeners.endpoints;

import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;
import v1.sulphurapi.core.SAPI;
import v1.sulphurapi.handlers.endpoints.EndpointsHandler;

import java.net.URI;
import java.net.URL;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class RequestTextAreaMouseListener implements MouseListener {

    private EndpointsHandler ep;

    public RequestTextAreaMouseListener(EndpointsHandler ep) {
        this.ep = ep;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.isPopupTrigger()) {event(e);}
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {event(e);}
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {event(e);}
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * Adds a right-click context menu to the request text area in the grid.
     * The menu allows sending the request to Intruder or Repeater.
     */
    private void event(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu("Actions");
        JMenuItem sendToIntruder = new JMenuItem("Send to Intruder");
        sendToIntruder.addActionListener((a) -> {
            sendToIntruderAction();
        });
        menu.add(sendToIntruder);
        JMenuItem sendToRepeater = new JMenuItem("Send to Repeater");
        sendToRepeater.addActionListener((a) -> {
            sendToRepeaterAction();
        });
        menu.add(sendToRepeater);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    /**
     * Sends the request from the text area to Intruder.
     * If the request is empty or the grid is not initialized, it logs an error message.
     * It constructs an HttpRequest object from the text area content
     * and uses the first server from the loaded API to create an HttpService.
     */
    private void sendToIntruderAction() {
        if (ep.getGrid() == null ||
            ep.getGrid().getEndpointsRequestTextArea() == null ||
            ep.getGrid().getEndpointsRequestTextArea().getText().isEmpty()) {
            SAPI.getAPI().logging().logToOutput("No request to send to Intruder");
            return;
        }
        try {
            URL server = new URI(SAPI.getLoadedAPI().getServers().getFirst().getUrl()).toURL();
            HttpService service = HttpService.httpService(
                    server.getHost(),
                    server.getPort(),
                    server.getProtocol().equalsIgnoreCase("https")
            );
            HttpRequest request = HttpRequest.httpRequest(service,ep.getGrid().getEndpointsRequestTextArea().getText());
            SAPI.getAPI().intruder().sendToIntruder(request);
            SAPI.getAPI().logging().raiseInfoEvent("Finished request: " + request.toString());
        } catch (Exception ex) {
            SAPI.getAPI().logging().raiseErrorEvent("Error sending request to Intruder: " + ex.getMessage());
        }
    }

    /**
     * Sends the request from the text area to Repeater.
     * If the request is empty, it logs an error message.
     * It constructs an HttpRequest object from the text area content
     * and sends it to the Repeater using the SAPI API.
     */
    private void sendToRepeaterAction() {
        if (ep.getGrid().getEndpointsRequestTextArea().getText().isEmpty()) {
            SAPI.getAPI().logging().logToOutput("No request to send to Repeater");
            return;
        }
        HttpRequest request = HttpRequest.httpRequest(ep.getGrid().getEndpointsRequestTextArea().getText());
        SAPI.getAPI().repeater().sendToRepeater(request);
    }
}
