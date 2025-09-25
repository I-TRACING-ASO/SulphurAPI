package v1.sulphurapi.utils;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import v1.sulphurapi.core.SAPI;

public class SwaggerReader {

    private String path;
    private final OpenAPI parsedAPI;

    /**
     * Constructor for SwaggerReader that initializes the reader with a given path.
     * @param path The path to the Swagger/OpenAPI file to be read.
     */
    public SwaggerReader(String path) {
        this.path = path;
        SAPI.logInfo("Swagger Reader loaded: " + this.path);
        ParseOptions options = new ParseOptions();
        options.setResolve(true);
        options.setResolveFully(true);
        options.setResolveCombinators(true);
        options.setExplicitObjectSchema(true);
        SwaggerParseResult result = new OpenAPIParser().readLocation(path, null, options);

        SAPI.logInfo("Swagger Reader End");
        this.parsedAPI = result.getOpenAPI();

        if (result.getMessages() != null) {
            result.getMessages().forEach(SAPI::logError);
        }
    }

    public OpenAPI getParsedAPI() {
        return this.parsedAPI;
    }

}
