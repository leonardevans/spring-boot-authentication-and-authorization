package com.lemutugi.controller;

import com.lemutugi.controller.util.HttpUtil;
import com.lemutugi.model.User;
import com.lemutugi.payload.request.ForgotPasswordRequest;
import com.lemutugi.payload.request.ResetPasswordRequest;
import com.lemutugi.payload.request.SignUpRequest;
import com.lemutugi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth/*")
public class AuthController extends HttpUtil {
    private UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("login")
    public String showLoginPage(){
        return "/auth/login";
    }

    @GetMapping("signup")
    public String showSignupPage(Model model){
        model.addAttribute("signUpRequest", new SignUpRequest());
        return "/auth/signup";
    }

    @PostMapping("signup")
    public String signup(@Valid @ModelAttribute("signUpRequest") SignUpRequest signUpRequest, BindingResult bindingResult){
        bindingResult = this.validateSignUpData(bindingResult, userService, signUpRequest);

        if (bindingResult.hasErrors()) return "/auth/signup";

        User user = userService.registerUser(signUpRequest, "");
        if (user != null){

            return "redirect:/auth/signup?success";
        }

        return "redirect:/auth/signup?error";
    }

    @GetMapping("forgot-password")
    public String showForgotPasswordPage(Model model){
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "/auth/forgot-password";
    }

    @PostMapping("forgot-password")
    public String forgotPassword(@Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest forgotPasswordRequest, BindingResult bindingResult){
        bindingResult = this.validateForgotPasswordData(bindingResult, userService, forgotPasswordRequest);

        if (bindingResult.hasErrors()) return "/auth/forgot-password";

        if (userService.forgotPassword(forgotPasswordRequest, "")){

            return "redirect:/auth/forgot-password?success";
        }

        return "redirect:/auth/forgot-password?error";
    }

    @GetMapping("password-reset-token")
    public ModelAndView validateResetToken(@RequestParam("token") String token, ModelAndView modelAndView){
        User user = userService.validatePasswordResetToken(token);

        if (user == null) {
            modelAndView.setViewName("/auth/verify-token-message");
            modelAndView.addObject("errorMessage", "Failed to verify your password reset token. Please request another password reset token in the forgot password page");
        }else{
            modelAndView.setViewName("/auth/reset-password");
            modelAndView.addObject("resetPasswordRequest", new ResetPasswordRequest(user.getEmail()));
        }

        return modelAndView;
    }

    @PostMapping("reset-password")
    public String resetPassword(@Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest resetPasswordRequest, BindingResult bindingResult){
        bindingResult = this.validateResetPasswordData(bindingResult, userService, resetPasswordRequest);

        if (bindingResult.hasErrors()) return "/auth/reset-password";

        if (userService.resetPassword(resetPasswordRequest)){
            return "redirect:/auth/reset-password?success";
        }

        return "redirect:/auth/reset-password?error";
    }

    @GetMapping("reset-password")
    public String showResetPassword(Model model){
        model.addAttribute("resetPasswordRequest", new ResetPasswordRequest());
        return "/auth/reset-password";
    }

    @GetMapping("verify-email")
    public String validateEmailToken(@RequestParam("token") String token, Model model){
        if (userService.validateEmailToken(token)) {
            model.addAttribute("successMessage", "You have successfully verified your email address.");
        }else{
            model.addAttribute("errorMessage", "Failed to verify your email address. Please contact us if this problem persists.");
        }

        return "/auth/verify-token-message";
    }
}
