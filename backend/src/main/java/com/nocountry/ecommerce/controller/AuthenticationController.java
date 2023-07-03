package com.nocountry.ecommerce.controller;

import com.nocountry.ecommerce.dto.ChangePassword;
import com.nocountry.ecommerce.dto.CustomerLoginResponse;
import com.nocountry.ecommerce.model.Account;
import com.nocountry.ecommerce.model.Customers;
import com.nocountry.ecommerce.repository.AccountRepository;
import com.nocountry.ecommerce.service.AccountService;
import com.nocountry.ecommerce.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@RestController
@RequestMapping("api/authentication")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("sign-up")
    public ResponseEntity<?> signUp(@RequestBody Customers customer){
            if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
                return new ResponseEntity<>("Email can't be empty", HttpStatus.BAD_REQUEST);
            }
            if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
                return new ResponseEntity<>("Password can't be empty", HttpStatus.BAD_REQUEST);
            }
            if (customer.getName() == null || customer.getName().isEmpty()) {
                return new ResponseEntity<>("Name can't be empty", HttpStatus.BAD_REQUEST);
            }
            if (customer.getLastName() == null || customer.getLastName().isEmpty()) {
                return new ResponseEntity<>("Lastname can't be empty", HttpStatus.BAD_REQUEST);
            }
            if (accountService.findByEmail(customer.getEmail()).isPresent()) {
                return new ResponseEntity<>("This account already exists", HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(accountService.createCustomer(customer), HttpStatus.CREATED);
            }
    }

    @PostMapping("sign-in")
    public ResponseEntity<?> signIn(@RequestBody Account account){
        CustomerLoginResponse signInAccount = authenticationService.signInAndReturnJWT(account);
        if(!signInAccount.isActive()){
            return new ResponseEntity<>("Account is not active", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(signInAccount, HttpStatus.OK);
    }

    @GetMapping("verify/{verificationCode}")
    public ResponseEntity<?> verifyAccount(@PathVariable String verificationCode) {
        try {
            if (verificationCode == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Verification Failed");
            } else {
                boolean verified = accountService.verifyAccount(verificationCode);
                if (verified) {
                    return ResponseEntity.ok("Verification Succeeded");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Verification Failed");
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An error occurred while verify the account" + e.getMessage());
        }
    }
    @PostMapping("forgot-password")
    public ResponseEntity<?> sendEmailForgotPassword(@RequestBody Customers customers) throws MessagingException, UnsupportedEncodingException {
        return new ResponseEntity<>(accountService.sendPasswordRecoveryToEmail(customers), HttpStatus.OK);
    }
    
    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePassword changePassword, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Misplaced fields");
        }
        if (!changePassword.getPassword().equals(changePassword.getConfirmPassword())){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Passwords do not match");
        }
        String token = changePassword.getTokenPassword();
        Account account = accountService.findByTokenPassword(token)
                .orElseThrow(() -> new UsernameNotFoundException("The account does not exist." + token));

        String newPassword = passwordEncoder.encode(token);
        account.setPassword(newPassword);
        account.setToken(null);
        accountRepository.save(account);
        return ResponseEntity.ok("Updated password");
    }
}
