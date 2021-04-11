package ua.edu.ukma.supermarket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.edu.ukma.supermarket.persistence.model.Employee;
import ua.edu.ukma.supermarket.persistence.service.EmployeeService;
import ua.edu.ukma.supermarket.persistence.service.PasswordService;

@Controller
public class AuthenticationController {

    private final EmployeeService employeeService;
    private final PasswordService passwordService;

    @Autowired
    public AuthenticationController(EmployeeService employeeService, PasswordService passwordService) {
        this.employeeService = employeeService;
        this.passwordService = passwordService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("error", null);
        return "login";
    }

    @PostMapping("/login-processing")
    public String loginUser(@ModelAttribute Employee employee, Model model) {

        Employee foundEmployee = employeeService.findEmployeeByUsername(employee.getUsername()).getObject();

        if (foundEmployee == null) {
            model.addAttribute("error", "Employee doesn't exists");
            return "login";
        }

        if (!passwordService.compareRawAndEncodedPassword(employee.getPassword(), (foundEmployee.getPassword()))) {
            model.addAttribute("error", "Wrong password");
            return "login";
        }

        return "redirect:/";
    }

}
