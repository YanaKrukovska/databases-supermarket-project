package ua.edu.ukma.supermarket.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.supermarket.persistence.model.*;
import ua.edu.ukma.supermarket.persistence.service.CategoryService;
import ua.edu.ukma.supermarket.persistence.service.CustomerService;
import ua.edu.ukma.supermarket.persistence.service.EmployeeService;
import ua.edu.ukma.supermarket.persistence.service.ProductService;

import java.util.List;

@Controller
public class ApplicationController {

    @Autowired
    private final CategoryService categoryService;

    @Autowired
    private final EmployeeService employeeService;

    @Autowired
    private final CustomerService customerService;


    @Autowired
    private final ProductService productService;

    public ApplicationController(CategoryService categoryService, EmployeeService employeeService, CustomerService customerService, ProductService productService) {
        this.categoryService = categoryService;
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.productService = productService;
    }

    @SneakyThrows
    @GetMapping("/category/{id}")
    @ResponseBody
    public Response<Category> getCategoryById(@PathVariable("id") int id) {
        return categoryService.findCategoryById(id);
    }

    @SneakyThrows
    @PostMapping("/category/{name}")
    @ResponseBody
    public Response<Category> createCategory(@PathVariable("name") String categoryName) {
        return categoryService.createCategory(categoryName);
    }

    @SneakyThrows
    @PostMapping("/category/{id}/{name}")
    @ResponseBody
    public Response<Category> updateCategory(@PathVariable("id") int categoryNumber,
                                             @PathVariable("name") String categoryName) {
        return categoryService.updateCategory(categoryNumber, categoryName);
    }

    @SneakyThrows
    @DeleteMapping("/category/{id}")
    @ResponseBody
    public Response<Category> deleteCategory(@PathVariable("id") int categoryNumber) {
        return categoryService.deleteCategory(categoryNumber);
    }

    @SneakyThrows
    @GetMapping("/category/all")
    @ResponseBody
    public Response<List<Category>> getAllCategoriesSorted() {
        return categoryService.getCategoriesSortedByName();
    }

    // Employee methods

    @SneakyThrows
    @GetMapping("/employee/{id}")
    @ResponseBody
    public Response<Employee> findEmployeeById(@PathVariable("id") String id) {
        return employeeService.findEmployeeById(id);
    }

    @SneakyThrows
    @PostMapping("/employee")
    @ResponseBody
    public Response<Employee> createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @SneakyThrows
    @PostMapping("/employee/update")
    @ResponseBody
    public Response<Employee> updateEmployee(@RequestBody Employee employee) {
        return employeeService.updateCategory(employee);
    }

    @SneakyThrows
    @DeleteMapping("/employee/{id}")
    @ResponseBody
    public Response<Employee> deleteEmployee(@PathVariable("id") String employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

    @SneakyThrows
    @GetMapping("/employee/cashiers")
    @ResponseBody
    public Response<List<Employee>> getAllCashiersSortedBySurname() {
        return employeeService.getCashiersSortedBySurname();
    }

    @SneakyThrows
    @GetMapping("/employee/contacts/{surname}")
    @ResponseBody
    public Response<List<Employee>> findEmployeeNumberAddressBySurname(@PathVariable("surname") String surname) {
        return employeeService.findPhoneNumberAndAddressBySurname(surname);
    }

    // artem

    //клієнт, що витратив найбільше грошей в нашому магазині
    @SneakyThrows
    @GetMapping("/customer/most_valuable")
    @ResponseBody
    public Response<List<CustomerCard>> findMostValuableCustomer() {
        return customerService.findMostValuableCustomer();
    }

    //всі продукти які купував ПЕВНИЙ клієнт
    @SneakyThrows
    @GetMapping("/product/of_customer/{id}")
    @ResponseBody
    public Response<List<Product>> productsByCustomer(@PathVariable("id") int cardId) {
        return productService.productsByCustomer(cardId);
    }

}
