package v1.sulphurapi.handlers.endpoints;
import v1.sulphurapi.handlers.http.RequestHandler;
import javax.swing.*;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EndpointsActionsHandler {
    ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Handles the action of sending an API request and updating the request and response areas.
     * @param endpointURL The URL of the API endpoint to send the request to.
     * @param method The HTTP method to use for the request (e.g., GET, POST).
     * @param requestArea The JTextArea where the request details will be displayed.
     * @param responseArea The JTextArea where the response details will be displayed.
     * @param body The body of the request, if applicable (e.g., for POST requests).
     * @param headers A list of headers to include in the request.
     */
    public void getActionHandler(String endpointURL, String method, JTextArea requestArea, JTextArea responseArea, String body, List<String> headers) {
        RequestHandler result = new RequestHandler(endpointURL, method, body, headers);
        result.run();
        lock.writeLock().lock();
        try {
            if (result.getRequestResponse() != null) {
                if (result.getRequestResponse().request() != null) {
                    requestArea.setText(result.getRequestResponse().request().toString());
                } else {
                    requestArea.setText("Request could not be calculated");
                }
                if (result.getRequestResponse().response() != null) {
                    responseArea.setText(result.getRequestResponse().response().toString());
                } else {
                    responseArea.setText("No response received, make sure service is accessible");
                }
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }

}
