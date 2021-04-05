package ua.edu.ukma.supermarket.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.supermarket.persistence.model.*;
import ua.edu.ukma.supermarket.persistence.service.*;

import java.util.List;
import java.util.Map;

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

    @Autowired
    private final StoreProductService storeProductService;

    public ApplicationController(CategoryService categoryService, EmployeeService employeeService, CustomerService customerService, ProductService productService, StoreProductService storeProductService) {
        this.categoryService = categoryService;
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.productService = productService;
        this.storeProductService = storeProductService;
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

    // Voldemar starts

    //клієнт, що витратив найбільше грошей в нашому магазині
    @SneakyThrows
    @GetMapping("/customer/most_valuable")
    @ResponseBody
    public Response<List<CustomerCard>> findMostValuableCustomer() {
        return customerService.findMostValuableCustomer();
    }

    //всі продукти які купував ПЕВНИЙ клієнт
    @SneakyThrows
    @GetMapping("/product/of_customer")
    @ResponseBody
    public Response<List<Product>> productsByCustomer(@RequestParam("id") int id) {
        return productService.productsByCustomer(id);
    }

    //клієнти, які купували тільки ті товари, що і ПЕВНИЙ клієнт
    @SneakyThrows
    @GetMapping("/customer/only_products_as")
    @ResponseBody
    public Response<List<CustomerCard>> customersThatByOnlyThooseProducts(@RequestParam("id") int id) {
        return customerService.customersThatByOnlyThooseProductsAs(id);
    }

    //Voldemar: касир, що за весь час роботи оформив товару на найбільшу суму
    @SneakyThrows
    @GetMapping("/employee/cashiers/most_valuable")
    @ResponseBody
    public Response<List<Employee>> findMostValuableCashier() {
        return employeeService.findMostValuableCashier();
    }
    // Voldemar ends

    // Artemis

    //товари, які купляли разом з даним товаром
    @SneakyThrows
    @GetMapping("/product/products_with")
    @ResponseBody
    public Response<List<Product>> productsBoughtWith(@RequestParam("id") int id) {
        return productService.productsBoughtWith(id);
    }

    //популярність категорії. Для кожної категорії вивести кількість проданих товарів
    @SneakyThrows
    @GetMapping("/category/popularity")
    @ResponseBody
    public Response<List<CategoryStatistic>> popularity() {
        return categoryService.popularity();
    }

    // топ-N продукти, на яких магазин заробив найбільше
    @SneakyThrows
    @GetMapping("/product/most_money_earn")
    @ResponseBody
    public Response<List<Product>> mostMoneyEarned(@RequestParam("n") int n) {
        return productService.mostMoneyEarned(n);
    }

    // клієнти, у яких в чеку були всі ті товари, які були у заданого по айді
    @SneakyThrows
    @GetMapping("/product/same_as")
    @ResponseBody
    public Response<List<CustomerCard>> sameProductsAs(@RequestParam("id") int id) {
        return customerService.sameProductsAs(id);
    }

    // Yana

    // Знайти всі товари в магазині, що не належать певній категорії
    @SneakyThrows
    @GetMapping("/store-product/all/except")
    @ResponseBody
    public Response<List<StoreProduct>> findStoreProductsNotInCategory(@RequestParam("category") String category) {
        return storeProductService.findAllStoreProductsNotInCategory(category);
    }

    // Знайти номери працівників, їх прізвища та кількість виданих ними чеків за кожен день,
    // де загальна сума покупки більше заданого параметра
    @SneakyThrows
    @GetMapping("/employee/stats")
    @ResponseBody
    public Response<List<EmployeeStatistic>> getEmployeeReceiptsStats(@RequestParam("sum") double sum) {
        return employeeService.getEmployeeReceiptSumStats(sum);
    }

    // Знайти кількість людей (працівників та клієнтів) у кожному місті
    @SneakyThrows
    @GetMapping("/city/stats")
    @ResponseBody
    public Response<Map<String, Integer>> getCityPeopleCount() {
        return employeeService.getCityPeopleCount();
    }

    // Знайти тих покупців, що приходили в магазин тільки в ті дні, що і певний клієнт
    @SneakyThrows
    @GetMapping("/customer/same-days")
    @ResponseBody
    public Response<List<CustomerCard>> getCustomersWhoShopTheSameDaysAsSomeone(@RequestParam("id") int cardId) {
        return customerService.getTheSameDaysAsCustomer(cardId);
    }
}
