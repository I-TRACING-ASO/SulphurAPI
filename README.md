# SulphurAPI  - API Testing Burp Suite extension

Burp Suite extension for automating OWASP API Top 10 detection. Will include dedicated checks (mass assignment, authentication, authorization), OpenID Connect/OAuth2 management, and advanced OpenAPI parsing. Designed to make API security testing more reliable and compliant with BApp Store criteria.
Supports Swagger/OpenAPI version 2.0 up to 3.1.1

### Install Release

Check out the latest [Release](https://github.com/I-TRACING-ASO/SulphurAPI/releases/latest)

### Build & Run
```zsh
# Clone repo
git clone https://github.com/I-TRACING-ASO/SulphurAPI.git
cd your-repo

# Build
mvn package

# Jar is located in target folder - SulphurAPI-X.X.jar
```

### Usage
1. Load the extension in Burpsuite with the .jar plugin
2. Use the button in the extension tab top-left corner to open a swagger/openapi file (.json or .yaml) (make sure it follows the specification and has no errors)
3. Right-click on endpoints in endpoints list and load a specific target endpoint
4. Edit parameters if it has any in the value cells
5. Send the request (move them to repeater/intruder if you want by right-clicking on the request area field)
