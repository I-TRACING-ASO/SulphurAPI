package v1.sulphurapi.handlers.http;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import v1.sulphurapi.core.SAPI;

import java.util.HashMap;
import java.util.Map;

public class ContentTypeHandler {

    private final Map<String, Runnable> actions  = new HashMap<>();
    private JSONObject content;
    private String body;

    /**
     * Constructor for ContentTypeHandler.
     * Initializes the content type actions for handling different content types.
     */
    public ContentTypeHandler() {
        content = null;
        actions.put("application/json", this::actionJSON);
        actions.put("application/xml", this::actionXML);
        actions.put("application/x-www-form-urlencoded", this::actionUrlEncoded);
    }

    /**
     * Prepare to calculate the body based on provided contentType.
     * This method will run the appropriate method via <code>Runnable</code> interface
     * @param content the map containing pairs of data to build the body of the caller
     * @param contentType the type to select the appropriate action to run
     * @return the calculated body as a String
     */
    public String calculateBody(JSONObject content, String contentType) {
        this.content = content;
        Runnable action = actions.get(contentType);
        if (action != null) {
            if (content == null || contentType == null || contentType.isEmpty() || content.isEmpty()){
                return "";
            }
            action.run();
        } else {
            SAPI.getAPI().logging().raiseErrorEvent("Unsupported content type: " + contentType);
        }
        return this.body;
    }

    /**
     * This method handles the JSON content Type using the <code>JSONObject</code> class.
     * It iterates through the content map and constructs a JSON object,
     * then converts it to a string representation for the body.
     */
    private void actionJSON() {
        if (this.content == null || this.content.isEmpty()) {
            body = "";
            return;
        }
        body = this.content.toString();
    }

    /**
     * This method handles the XML content Type using the <code>XML</code> class from org.json package.
     * It converts the content map into a JSON object and then to an XML string representation.
     */
    private void actionXML() {
        if (this.content == null || this.content.isEmpty()) {
            body = "";
            return;
        }
        body = XML.toString(this.content);
    }

    /**
     * This method handles the x-www-form-urlencoded content type.
     * It constructs a URL-encoded string from the content map.
     * Each key-value pair is formatted as "key=value" and joined with "&".
     */
    private void actionUrlEncoded() {
        StringBuilder body_build = new StringBuilder();
        JSONObject item = this.content;
        for (String key : item.keySet()) {
             if (item.get(key) instanceof JSONArray) {
                 body = "x-www-form-url does not support nested objects";
                 return ;
             }
             body_build.append(key).append("=").append(item.get(key)).append("&");
        }
        if (!body_build.isEmpty()) {
            body_build.setLength(body_build.length() - 1);
            body = body_build.toString();
        } else {
            body = "";
        }
    }
}
