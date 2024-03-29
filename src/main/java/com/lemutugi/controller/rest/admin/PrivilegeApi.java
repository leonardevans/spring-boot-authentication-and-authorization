package com.lemutugi.controller.rest.admin;

import com.lemutugi.controller.util.HttpUtil;
import com.lemutugi.model.Privilege;
import com.lemutugi.payload.request.PrivilegeRequest;
import com.lemutugi.payload.response.ApiResponse;
import com.lemutugi.service.PrivilegeService;
import com.lemutugi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/privileges/")
@RolesAllowed({"ROLE_ADMIN", "ROLE_SUPERADMIN"})
public class PrivilegeApi extends HttpUtil {
    private PrivilegeService privilegeService;

    @Autowired
    public PrivilegeApi(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getPrivileges(
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = Constants.SMALL_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = "name", required = false) String sortField,
            @RequestParam(defaultValue = "asc", required = false) String sortDir ,
            @RequestParam(required = false) String search
            ){
        Page<Privilege> privilegePage = Page.empty();
        Map<String, Object> data = new HashMap<>();

        if (search != null){
            privilegePage = privilegeService.search(pageNo, pageSize, sortField, sortDir, search);
            data.put("search", search);
        }else{
            privilegePage = privilegeService.getAllPrivileges(pageNo, pageSize, sortField, sortDir);
        }
        List<Privilege> privileges = privilegePage.getContent();
        data.put("privileges", privileges);

        data.putAll(this.addPagingAttributes(data, pageSize, pageNo, privilegePage.getTotalPages(), privilegePage.getTotalElements(), sortField, sortDir));

        ApiResponse apiResponse = new ApiResponse(true, "Privileges fetched successfully");
        apiResponse.setData(data);
        return ResponseEntity.ok().body(apiResponse);
    }

    @GetMapping("{id}")
    ResponseEntity<ApiResponse> getPrivilege(@PathVariable("id") Long id){
        Privilege privilege = privilegeService.getPrivilegeById(id);
        Map<String, Object> data = new HashMap<>();
        data.put("privilege", privilege);
        ApiResponse apiResponse = new ApiResponse(true, "Privilege fetched successfully.");
        apiResponse.setData(data);
        return ResponseEntity.ok().body(apiResponse);
    }

    @PreAuthorize("hasAuthority('DELETE_PRIVILEGE')")
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> deletePrivilege(@PathVariable("id") Long id){
        ApiResponse apiResponse = null;
        if (privilegeService.deletePrivilegeById(id)){
            apiResponse = new ApiResponse(true, "Privilege deleted successfully.");
            return ResponseEntity.ok().body(apiResponse);
        }
        apiResponse = new ApiResponse(false, "Failed to delete privilege!");
        return ResponseEntity.internalServerError().body(apiResponse);
    }

    @PreAuthorize("hasAuthority('CREATE_PRIVILEGE')")
    @PostMapping
    public ResponseEntity<ApiResponse> createPrivilege(@Valid @RequestBody PrivilegeRequest privilegeRequest, BindingResult bindingResult){
        bindingResult = this.validateCreatePrivilegeData(bindingResult, privilegeService, privilegeRequest);

        ApiResponse apiResponse = null;

        if (bindingResult.hasErrors()){
            apiResponse = new ApiResponse(false, "Failed to create privilege. Please provide correct information.");
            apiResponse.setErrors(this.getErrors(bindingResult));
            return ResponseEntity.badRequest().body(apiResponse);
        }

        Map<String, Object> data = new HashMap<>();
        Privilege privilege = privilegeService.createPrivilege(privilegeRequest);
        data.put("privilege", privilege);

        apiResponse = new ApiResponse(true, "Privilege created successfully.");
        apiResponse.setData(data);
        return ResponseEntity.ok().body(apiResponse);
    }

    @PreAuthorize("hasAuthority('EDIT_PRIVILEGE')")
    @PutMapping
    public ResponseEntity<ApiResponse> updatePrivilege(@Valid @RequestBody PrivilegeRequest privilegeRequest, BindingResult bindingResult){
        bindingResult = this.validateUpdatePrivilegeData(bindingResult, privilegeService, privilegeRequest);

        ApiResponse apiResponse = null;

        if (bindingResult.hasErrors()){
            apiResponse = new ApiResponse(false, "Failed to update privilege. Please provide correct information.");
            apiResponse.setErrors(this.getErrors(bindingResult));
            return ResponseEntity.badRequest().body(apiResponse);
        }

        Map<String, Object> data = new HashMap<>();
        Privilege privilege = privilegeService.updatePrivilege(privilegeRequest);
        data.put("privilege", privilege);

        apiResponse = new ApiResponse(true, "Privilege updated successfully.");
        apiResponse.setData(data);
        return ResponseEntity.ok().body(apiResponse);
    }
}
