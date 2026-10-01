package com.trading.app.brokerConfig.aliceBlueConfig;

import com.trading.app.brokerConfig.aliceBlueConfig.service.AliceBlueService;
import com.trading.app.brokerConfig.aliceBlueConfig.util.Constant;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller responsible for handling AliceBlue broker authentication flow.
 *
 * <p>
 * Authentication flow:
 *
 * <pre>
 * 1. Frontend calls /auth/login
 * 2. Backend generates the AliceBlue login URL
 * 3. User logs in through AliceBlue
 * 4. AliceBlue redirects the user to /callback with authCode
 * 5. Backend generates the session/auth token
 * 6. User is redirected back to the frontend
 * </pre>
 *
 * <p>
 * This controller only handles HTTP requests.
 * The actual AliceBlue authentication logic is handled by
 * {@link AliceBlueService}.
 */
@RestController
public class AliceBlueController {

    /*
     * Service responsible for handling AliceBlue authentication
     * and broker-related business logic.
     *
     * Constructor injection is used so that Spring can automatically
     * create and inject the AliceBlueService bean.
     */
    private final AliceBlueService aliceBlueService;


    /**
     * Creates the AliceBlueController.
     *
     * @param aliceBlueService service used for AliceBlue authentication
     */
    public AliceBlueController(AliceBlueService aliceBlueService) {
        this.aliceBlueService = aliceBlueService;
    }


    /**
     * Starts the AliceBlue login process.
     *
     * <p>
     * The frontend calls this endpoint when the user wants to
     * authenticate with AliceBlue.
     *
     * <p>
     * The controller delegates the actual URL generation to
     * AliceBlueService and returns the generated login URL.
     *
     * <p>
     * Example:
     *
     * <pre>
     * GET http://localhost:8080/auth/login
     * </pre>
     *
     * @return AliceBlue login URL
     */
    @CrossOrigin
    @GetMapping("/auth/login")
    public String login() {

        // Generate and return the AliceBlue authentication URL.
        return aliceBlueService.getLoginUrl();
    }


    /**
     * Handles the callback request sent by AliceBlue after
     * successful user authentication.
     *
     * <p>
     * AliceBlue redirects the browser to this endpoint with an
     * authentication code.
     *
     * <p>
     * Example:
     *
     * <pre>
     * http://localhost:8080/callback?authCode=XXXXXX
     * </pre>
     *
     * <p>
     * The received authCode is passed to AliceBlueService.
     * The service generates the required authentication/session
     * token and stores it for further broker API usage.
     *
     * @param authCode authentication code returned by AliceBlue
     * @return redirect response to the frontend after authentication
     */
    @GetMapping("/callback")
    public RedirectView callback(@RequestParam("authCode") String authCode) {

        // Print the received authentication code for debugging.
        // TODO: Remove this log before production because authCode
        // is a sensitive authentication value.
        System.out.println("AUTH CODE = " + authCode);


        /*
         * Generate the AliceBlue authentication/session token
         * using the authCode received from the broker.
         */
        String checkSumValue = aliceBlueService.generateAuthToken(authCode);


        /*
         * If token generation is successful,
         * redirect the user back to the frontend application.
         */
        if (checkSumValue != null) {

            return new RedirectView(
                    Constant.REDIRECT_FRONTENT
            );
        }


        /*
         * If token generation fails, return an error message.
         */
        return new RedirectView(
                "Failed Checksum Generation"
        );
    }
}