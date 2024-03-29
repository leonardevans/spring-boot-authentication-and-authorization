package com.lemutugi.controller.rest;

import com.lemutugi.controller.util.HttpUtil;
import com.lemutugi.model.Location;
import com.lemutugi.model.User;
import com.lemutugi.payload.dto.MyAccountDto;
import com.lemutugi.payload.request.LocationRequest;
import com.lemutugi.payload.request.account.PasswordRequest;
import com.lemutugi.payload.request.account.ProfilePictureRequest;
import com.lemutugi.payload.response.ApiResponse;
import com.lemutugi.service.MyAccountService;
import com.lemutugi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/my-account/")
public class MyAccountApi extends HttpUtil {
    private MyAccountService myAccountService;
    private UserService userService;

    @Autowired
    public MyAccountApi(MyAccountService myAccountService, UserService userService) {
        this.myAccountService = myAccountService;
        this.userService = userService;
    }

    @PutMapping("change-password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody PasswordRequest passwordRequest, BindingResult bindingResult){
        bindingResult = this.validateChangePasswordData(bindingResult, myAccountService, passwordRequest);

        ApiResponse apiResponse = null;

        if (bindingResult.hasErrors()) {
            apiResponse = new ApiResponse(false, "Failed to change your password. Please provide correct information.");
            apiResponse.setErrors(this.getErrors(bindingResult));
            return ResponseEntity.badRequest().body(apiResponse);
        }

        Map<String, Object> data = new HashMap<>();
        User user = myAccountService.updatePassword(passwordRequest.getNewPassword());
        data.put("user", user);

        apiResponse = new ApiResponse(true, "Password updated successfully.");
        apiResponse.setData(data);
        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("update-location")
    public ResponseEntity<ApiResponse> updateLocation(@Valid @RequestBody LocationRequest locationRequest, BindingResult bindingResult){
        ApiResponse apiResponse = null;

        if (bindingResult.hasErrors()) {
            apiResponse = new ApiResponse(false, "Failed to update your location. Please provide correct information.");
            apiResponse.setErrors(this.getErrors(bindingResult));
            return ResponseEntity.badRequest().body(apiResponse);
        }

        Map<String, Object> data = new HashMap<>();
        Location location = myAccountService.updateLocation(locationRequest);
        data.put("location", location);

        apiResponse = new ApiResponse(true, "Location updated successfully.");
        apiResponse.setData(data);
        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping("update-account")
    public ResponseEntity<ApiResponse> updateMyAccount(@Valid @RequestBody MyAccountDto myAccountDto, BindingResult bindingResult){
        bindingResult = this.validateUpdateMyData(bindingResult, userService, myAccountDto);
        ApiResponse apiResponse = null;

        if (bindingResult.hasErrors()) {
            apiResponse = new ApiResponse(false, "Failed to update your account. Please provide correct information.");
            apiResponse.setErrors(this.getErrors(bindingResult));
            return ResponseEntity.badRequest().body(apiResponse);
        }

        Map<String, Object> data = new HashMap<>();
        User user = myAccountService.updateMyDetails(myAccountDto);
        MyAccountDto myAccountDtoUpdated = new MyAccountDto(user);
        data.put("user", myAccountDtoUpdated);

        apiResponse = new ApiResponse(true, "Account updated successfully.");
        apiResponse.setData(data);
        return ResponseEntity.ok().body(apiResponse);
    }

    @PutMapping(value = "update-profile-pic" )
    public ResponseEntity<ApiResponse> updateProfilePic(@Valid @ModelAttribute ProfilePictureRequest profilePictureRequest, BindingResult bindingResult) throws IOException {
        ApiResponse apiResponse = null;

        if (bindingResult.hasErrors()) {
            apiResponse = new ApiResponse(false, "Failed to update your profile picture. Please provide correct information.");
            apiResponse.setErrors(this.getErrors(bindingResult));
            return ResponseEntity.badRequest().body(apiResponse);
        }

        Map<String, Object> data = new HashMap<>();
        String profilePic = myAccountService.updateProfilePic(profilePictureRequest);

        if (profilePic == null){
            apiResponse = new ApiResponse(false, "An error occurred while updating your profile picture.");
        }else{
            data.put("profilePic", profilePic);
            apiResponse = new ApiResponse(true, "Profile picture updated successfully.");
        }

        apiResponse.setData(data);
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("location")
    public ResponseEntity<ApiResponse> getLocation(){
        Map<String, Object> data = new HashMap<>();
        Location location = myAccountService.getMyLocation();
        data.put("location", location);

        ApiResponse apiResponse = new ApiResponse(true, "Location fetched successfully");
        apiResponse.setData(data);
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getDetails(){
        Map<String, Object> data = new HashMap<>();
        User user = myAccountService.getMyDetails();
        MyAccountDto myAccountDto = new MyAccountDto(user);
        data.put("user", myAccountDto);

        ApiResponse apiResponse = new ApiResponse(true, "User details successfully");
        apiResponse.setData(data);
        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("location")
    public ResponseEntity<ApiResponse> deleteLocation(){
        ApiResponse apiResponse = null;

        if (myAccountService.deleteMyLocation()){
            apiResponse = new ApiResponse(true, "Location deleted successfully.");
        }else{
            apiResponse = new ApiResponse(false, "Failed to delete location.");
        }

        return ResponseEntity.ok().body(apiResponse);
    }

    @DeleteMapping("delete-profile-pic")
    public ResponseEntity<ApiResponse> deleteProfilePic(){
        ApiResponse apiResponse = null;

        if (myAccountService.deleteProfilePic()){
            apiResponse = new ApiResponse(true, "Profile picture deleted successfully.");
        }else{
            apiResponse = new ApiResponse(false, "Failed to delete profile picture.");
        }

        return ResponseEntity.ok().body(apiResponse);
    }
}
