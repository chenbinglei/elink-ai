package com.sunmax.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xiuho
 * @CreateDate: 2019/9/7
 * @Description:
 */
@CrossOrigin
@RestController
public class MainController {
    @RequestMapping("/current-info")
    public Object getUser(Authentication authentication) {
        return authentication;
    }
}
