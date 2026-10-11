# Alice Blue Vendor API — Complete Authentication Flow in Java

## 1. Objective

Implement Alice Blue authentication in a Java Spring Boot application so that multiple users can connect their individual Alice Blue trading accounts to our application.

**Official documentation**
- Main API documentation: https://v2api.aliceblueonline.com/
- Authentication: https://v2api.aliceblueonline.com/Authentication/
- Vendor API: https://v2api.aliceblueonline.com/Vendors/
- Developer Portal: https://a3.aliceblueonline.com/

## 2. Authentication Flow Overview

1. Register our application as a Vendor with Alice Blue.
2. Obtain `appCode` and `apiSecret`.
3. Configure the application's Redirect URL in the Developer Portal.
4. User clicks **Connect to Alice Blue** in our frontend.
5. Redirect the user to the official Alice Blue login page.
6. User logs in using their Alice Blue credentials.
7. Alice Blue redirects the user to our configured Redirect URL with `authCode` and `userId`.
8. Our Spring Boot backend receives these parameters.
9. Backend calculates a SHA-256 checksum.
10. Backend calls the Alice Blue Get User Details API.
11. Alice Blue returns the session information.
12. Backend securely stores the session and marks the broker account as connected.

## 3. Step 1 — Register the Vendor Application

Open the Alice Blue Developer Portal:

https://a3.aliceblueonline.com/

- Log in using the appropriate Alice Blue credentials.
- Navigate to **My Apps** or the Apps section.
- Select **Create New App**.
- Enter the mandatory application details.
- Configure the application URL and Redirect URL as required.
- Save the application.
- Obtain the generated `appCode` and `apiSecret`.
- Submit the application for Alice Blue Admin review and Vendor API approval.

**Important:** Creating an app does not necessarily mean Vendor API access is already approved.

Store the credentials in backend environment variables or a secrets manager. Never hardcode them in Angular HTML or TypeScript.

Example environment variables:

```text
ALICEBLUE_APP_CODE=your_app_code
ALICEBLUE_API_SECRET=your_api_secret
ALICEBLUE_REDIRECT_URL=http://localhost:8080/api/brokers/aliceblue/callback
```

The Redirect URL above is an example for local development. Configure the same appropriate URL in the Developer Portal.

## 4. Step 2 — Frontend Connect Button

When the user clicks **Connect to Alice Blue**, initiate the authorization flow.

Example Angular method:

```typescript
connectToAliceBlue(): void {
  window.location.href =
    'http://localhost:8080/api/brokers/aliceblue/connect';
}
```

The frontend calls the backend route. The backend redirects the browser to Alice Blue's login page.

This keeps the authorization flow controlled by the backend.

## 5. Step 3 — Redirect the User to Alice Blue

The official authentication documentation specifies the login URL format:

```text
https://ant.aliceblueonline.com/?appcode=YOUR_APP_CODE
```

Example Spring Boot controller method:

```java
@GetMapping("/connect")
public void connectToAliceBlue(
        HttpServletResponse response
) throws IOException {

    String appCode =
            System.getenv("ALICEBLUE_APP_CODE");

    String loginUrl =
            "https://ant.aliceblueonline.com/?appcode="
            + URLEncoder.encode(
                    appCode,
                    StandardCharsets.UTF_8
            );

    response.sendRedirect(loginUrl);
}
```

Required imports:

```java
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
```

The user now logs in directly on the Alice Blue website.

Our application should not collect or store the user's Alice Blue password.

## 6. Step 4 — Receive the Callback

After successful authorization, Alice Blue redirects the user to the configured Redirect URL with these parameters:

- `authCode` — User authorization token.
- `userId` — Alice Blue user ID.

Example callback URL:

```text
http://localhost:8080/api/brokers/aliceblue/callback?authCode=AUTH_CODE&userId=USER_ID
```

The values shown above are placeholders.

Example controller:

```java
@GetMapping("/callback")
public ResponseEntity<String> aliceBlueCallback(
        @RequestParam String authCode,
        @RequestParam String userId
) {

    // Continue authentication in the backend.
    // Do not log authCode or other credentials.

    return ResponseEntity.ok(
            "Alice Blue authorization received"
    );
}
```

Required imports:

```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
```

This is only the callback skeleton. The next step must obtain the actual session before the account can be considered connected.

## 7. Step 5 — Generate the SHA-256 Checksum

According to the official Alice Blue authentication documentation, calculate the SHA-256 hash of:

```text
userId + authCode + apiSecret
```

The values must be concatenated in exactly this order, without additional separators.

Java implementation:

```java
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class AliceBlueChecksum {

    private AliceBlueChecksum() {
    }

    public static String generate(
            String userId,
            String authCode,
            String apiSecret
    ) throws NoSuchAlgorithmException {

        String input = userId + authCode + apiSecret;

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        byte[] hash = digest.digest(
                input.getBytes(StandardCharsets.UTF_8)
        );

        return HexFormat.of().formatHex(hash);
    }
}
```

Usage:

```java
String checksum = AliceBlueChecksum.generate(
        userId,
        authCode,
        apiSecret
);
```

Do not use a normal password encoder or add a delimiter between the values. Follow the documented checksum format.

## 8. Step 6 — Call the Get User Details API

The official endpoint is:

```text
POST https://a3.aliceblueonline.com/open-api/od/v1/vendor/getUserDetails
```

The request JSON uses the case-sensitive key `checkSum`.

Example request:

```json
{
  "checkSum": "GENERATED_SHA256_CHECKSUM"
}
```

Java implementation using the standard Java HTTP client and Jackson:

```java
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class AliceBlueSessionClient {

    private static final String SESSION_URL =
        "https://a3.aliceblueonline.com/open-api/od/v1/vendor/getUserDetails";

    private final HttpClient httpClient =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper =
        new ObjectMapper();

    public JsonNode createSession(
            String checksum
    ) throws Exception {

        String requestBody = objectMapper.writeValueAsString(
            Map.of("checkSum", checksum)
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(SESSION_URL))
            .timeout(Duration.ofSeconds(20))
            .header("Content-Type", "application/json")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    requestBody
                )
            )
            .build();

        HttpResponse<String> response =
            httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {
            throw new IllegalStateException(
                "Alice Blue session API returned HTTP "
                + response.statusCode()
            );
        }

        return objectMapper.readTree(response.body());
    }
}
```

Use a compatible Jackson dependency if it is not already included in the Spring Boot project.

## 9. Step 7 — Validate the Response

A successful response documented by Alice Blue contains fields such as:

```json
{
  "stat": "Ok",
  "clientId": "EXAMPLE_CLIENT_ID",
  "userSession": "EXAMPLE_SESSION_TOKEN"
}
```

The values are illustrative placeholders, not usable credentials.

Backend validation:

```java
JsonNode result =
        aliceBlueSessionClient.createSession(checksum);

String status = result.path("stat").asText();

if (!"Ok".equalsIgnoreCase(status)) {
    String errorMessage =
            result.path("emsg")
                  .asText("Session creation failed");

    throw new IllegalStateException(errorMessage);
}

String clientId =
        result.path("clientId").asText();

String userSession =
        result.path("userSession").asText();

if (clientId.isBlank() || userSession.isBlank()) {
    throw new IllegalStateException(
        "Required session information is missing"
    );
}
```

Only after validating this response should the application mark the account as connected.

## 10. Step 8 — Securely Store the Session

For each connected user, store the necessary information in the backend:

- Internal application user ID.
- Alice Blue `userId` or `clientId`.
- Encrypted `userSession`.
- Connection status.
- Session creation and expiry information, where available.

Security requirements:

- Never return `userSession` or `apiSecret` to Angular.
- Never print authorization codes, checksums, secrets or session tokens in application logs.
- Encrypt sensitive session data at rest.
- Associate each broker session with the correct application user.
- Use HTTPS in production.
- Provide a way to disconnect and invalidate or discard the session as supported by Alice Blue.

## 11. Step 9 — Return Connection Status to Angular

After session creation succeeds, return a simple response to the frontend:

```json
{
  "success": true,
  "broker": "ALICE_BLUE",
  "connected": true,
  "message": "Alice Blue account connected successfully"
}
```

This is our application's response format, not Alice Blue's official API response.

Angular should display **Connected** based on the backend's verified session status—not simply because the login button was clicked.

## 12. Step 10 — Use the Session for Further APIs

Once the session is created:

- Use the returned session according to the authentication requirements of each subsequent Alice Blue API.
- Implement market data, account details, positions, orders and other required features.
- Keep all broker API calls in Spring Boot.
- Return only the information required by Angular.
- Add error handling for expired sessions, revoked access and failed API calls.

Check each individual endpoint's official documentation for its required headers and request format.

## 13. Error Handling

| Error | What to check |
|---|---|
| Invalid authorization code | Ensure the callback contains the correct `authCode`. |
| Invalid checksum | Verify the concatenation order and the correct API Secret. |
| API key expired or unavailable | Check the Developer Portal and app status. |
| HTTP 401 or 403 | Verify authentication, permissions and security configuration. |
| Missing session token | Check the response body and session creation status. |
| Redirect failure | Verify the configured Redirect URL and app configuration. |

Do not automatically mark a broker as connected when an API request fails.

## 14. Complete Flow Summary

```text
Angular Connect Button
        |
        v
Spring Boot /connect
        |
        v
Alice Blue Login Page
        |
        v
User Authorization
        |
        v
Spring Boot /callback
        |
        v
Receive authCode + userId
        |
        v
Generate SHA-256 Checksum
        |
        v
POST /open-api/od/v1/vendor/getUserDetails
        |
        v
Validate Session Response
        |
        v
Securely Store userSession
        |
        v
Return Connected Status to Angular
```

## Implementation Order

1. Register and obtain approval for the Alice Blue Vendor application.
2. Configure the Redirect URL.
3. Implement the `/connect` endpoint.
4. Implement the `/callback` endpoint.
5. Implement SHA-256 checksum generation.
6. Implement the Get User Details API client.
7. Validate and securely store the session.
8. Connect the Angular UI to the backend.
9. Test success, failure and session-expiry scenarios.
10. Implement market data and trading operations only after authentication is verified.

**Reference:** Alice Blue official documentation — Authentication and Vendors API. The endpoint paths, checksum input order and response fields above are based on the documented flow. Confirm current Vendor approval requirements and production redirect settings with Alice Blue before deployment.
