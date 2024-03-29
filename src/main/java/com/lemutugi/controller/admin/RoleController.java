package com.lemutugi.controller.admin;

import com.lemutugi.controller.util.HttpUtil;
import com.lemutugi.model.Role;
import com.lemutugi.payload.request.RoleRequest;
import com.lemutugi.repository.PrivilegeRepository;
import com.lemutugi.service.RoleService;
import com.lemutugi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequestMapping("/admin/roles/")
@RolesAllowed({"ROLE_ADMIN", "ROLE_SUPERADMIN"})
@Controller
public class RoleController extends HttpUtil {
    private RoleService roleService;
    private PrivilegeRepository privilegeRepository;

    @Autowired
    public RoleController(RoleService roleService, PrivilegeRepository privilegeRepository) {
        this.roleService = roleService;
        this.privilegeRepository = privilegeRepository;
    }

    @GetMapping
    public String showRoles(
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = Constants.SMALL_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = "name", required = false) String sortField,
            @RequestParam(defaultValue = "asc", required = false) String sortDir ,
            @RequestParam(required = false) String search,
            Model model){

        Page<Role> rolePage = Page.empty();

        if (search != null){
            rolePage = roleService.search(pageNo, pageSize, sortField, sortDir, search);
            model.addAttribute("search", "&search=" + search);
        }else{
            rolePage = roleService.getAllRoles(pageNo, pageSize, sortField, sortDir);
            model.addAttribute("search", "");
        }

        List<Role> roles = rolePage.getContent();

        model = this.addPagingAttributes(model, pageSize, pageNo, rolePage.getTotalPages(), rolePage.getTotalElements(), sortField, sortDir);

        model.addAttribute("roles", roles);
        return "/admin/roles";
    }

    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    @GetMapping("add")
    public String showAddRole(Model model){
        model.addAttribute("roleRequest", new RoleRequest());
        model.addAttribute("allPrivileges", privilegeRepository.findAll());
        return "/admin/add-edit-role";
    }

    @PreAuthorize("hasAuthority('EDIT_ROLE')")
    @GetMapping("edit/{id}")
    public String showEditRole(@PathVariable("id") Long id, Model model){
        Role role = roleService.getRoleById(id);
        RoleRequest roleRequest = new RoleRequest(role);
        model.addAttribute("roleRequest", roleRequest);
        model.addAttribute("allPrivileges", privilegeRepository.findAll());
        return "/admin/add-edit-role";
    }

    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    @GetMapping("delete/{id}")
    public String deleteRole(@PathVariable("id") Long id){
        if (roleService.deleteRoleById(id)) return "redirect:/admin/roles/?delete_success";
        return "redirect:/admin/roles/?delete_error";
    }

    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    @PostMapping("add")
    public String createRole(@Valid @ModelAttribute("roleRequest") RoleRequest roleRequest, BindingResult bindingResult, Model model){
        bindingResult = this.validateCreateRoleData(bindingResult, roleService, roleRequest);

        if (bindingResult.hasErrors()) {
            model.addAttribute("allPrivileges", privilegeRepository.findAll());
            return "/admin/add-edit-role";
        }

        roleService.createRole(roleRequest);

        return "redirect:/admin/roles/?add_success";
    }

    @PreAuthorize("hasAuthority('EDIT_ROLE')")
    @PostMapping("update")
    public String updateRole(@Valid @ModelAttribute("roleRequest") RoleRequest roleRequest, BindingResult bindingResult, Model model){
        bindingResult = this.validateUpdateRoleData(bindingResult, roleService, roleRequest);

        if (bindingResult.hasErrors()) {
            model.addAttribute("allPrivileges", privilegeRepository.findAll());
            return "/admin/add-edit-role";
        }

        roleService.updateRole(roleRequest);

        return "redirect:/admin/roles/?update_success";
    }
}
