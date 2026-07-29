package com.trading.app.brokerConfig.aliceBlueConfig;

import com.trading.app.brokerConfig.aliceBlueConfig.service.AliceBlueService;
import com.trading.app.brokerConfig.aliceBlueConfig.util.Constant;
import com.trading.app.brokerConfig.aliceBlueConfig.util.TokenStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class AliceBlueController {

    private final AliceBlueService aliceBlueService;

    private final TokenStore tokenStore;

    public AliceBlueController(AliceBlueService aliceBlueService, TokenStore tokenStore) {
        this.aliceBlueService = aliceBlueService;
        this.tokenStore = tokenStore;
    }

    @CrossOrigin
    @GetMapping("/auth/login")
    public String login() {
        return aliceBlueService.getLoginUrl();
    }


    @GetMapping("/callback")
    public RedirectView callback(@RequestParam("authCode") String authCode) {
        System.out.println("AUTH CODE = " + authCode);
        String authToken = aliceBlueService.generateAccessToken(authCode);
        tokenStore.saveToken(authToken);

        return new RedirectView(
                Constant.REDIRECT_FRONTENT);
    }

}
