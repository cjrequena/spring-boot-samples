package com.cjrequena.sample.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FooController {

  @GetMapping("/home")
  public String handleWelcome() {
    return "home";
  }

  @GetMapping("/admin/home")
  public String handleAdminHome() {
    return "admin_home";
  }

  @GetMapping("/user/home")
  public String handleUserHome() {
    return "user_home";
  }

  @GetMapping("/login")
  public String handleLogin() {
    return "login";
  }
}
