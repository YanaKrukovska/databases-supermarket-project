package ua.edu.ukma.supermarket.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    private final ReceiptService receiptService;

    public ApplicationController(CategoryService categoryService, EmployeeService employeeService, CustomerService customerService, ProductService productService, StoreProductService storeProductService, ReceiptService receiptService) {
        this.categoryService = categoryService;
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.productService = productService;
        this.storeProductService = storeProductService;
        this.receiptService = receiptService;
    }

    @GetMapping("/")
    public String basePage(Model model) {
        Category sampleCategory = new Category(23, "Weapons");
        model.addAttribute("category", sampleCategory);
        return "index";
    }

    @GetMapping("/product")
    public String productsPage(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "products";
    }

    @PostMapping("/request-delete-product")
    public String removeProduct(@ModelAttribute("productId") int id, Model model) {
        Response<Product> productResponse = productService.deleteProduct(id);
        if (productResponse.getErrors().size() > 0) {
            model.addAttribute("errors", productResponse.getErrors());
            return "error-page";
        }
        return "redirect:/product";
    }

    @GetMapping("/category")
    public String categoriesPage(Model model) {
        Response<List<Category>> categoryResponse = categoryService.findAll();
        if (categoryResponse.getErrors().size() > 0) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("categories", categoryResponse.getObject());
        return "categories";
    }

    @GetMapping("/edit-category")
    public String editCategory(@ModelAttribute("categoryNumber") int id, Model model) {
        Response<Category> categoryResponse = categoryService.findCategoryById(id);
        if (categoryResponse.getErrors().size() > 0) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("category", categoryResponse.getObject());
        return "category-edit";
    }

    @PostMapping("/request-edit-category")
    public String requestEditCategory(@ModelAttribute Category category, Model model) {
        Response<Category> categoryResponse = categoryService.updateCategory(category.getCategoryNumber(), category.getCategoryName());
        if (categoryResponse.getErrors().size() > 0) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        return "redirect:/category";
    }

    @PostMapping("/request-delete-category")
    public String removeCategory(@ModelAttribute("categoryNumber") int id, Model model) {
        Response<Category> categoryResponse = categoryService.deleteCategory(id);
        if (categoryResponse.getErrors().size() > 0) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        return "redirect:/category";
    }

    @GetMapping("/employee")
    public String employeesPage(Model model) {
        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        return "employees";
    }

    @PostMapping("/request-delete-employee")
    public String removeEmployee(@ModelAttribute("employeeId") String id, Model model) {
        Response<Employee> employeeResponse = employeeService.deleteEmployee(id);
        if (employeeResponse.getErrors().size() > 0) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        return "redirect:/employee";
    }

    @GetMapping("/customer")
    public String customersPage(Model model) {
        List<CustomerCard> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        return "customers";
    }

    @PostMapping("/request-delete-customer")
    public String removeCustomer(@ModelAttribute("cardNumber") int id, Model model) {
        Response<CustomerCard> customerCardResponse = customerService.deleteCustomerCard(id);
        if (customerCardResponse.getErrors().size() > 0) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        return "redirect:/customer";
    }

    @GetMapping("/store-product")
    public String storeProductsPage(Model model) {
        List<StoreProduct> storeProducts = storeProductService.findAll();
        model.addAttribute("storeProducts", storeProducts);
        return "store-products";
    }

    @PostMapping("/request-delete-store-product")
    public String removeStoreProduct(@ModelAttribute("upc") String upc, Model model) {
        Response<StoreProduct> storeProductResponse = storeProductService.deleteStoreProduct(upc);
        if (storeProductResponse.getErrors().size() > 0) {
            model.addAttribute("errors", storeProductResponse.getErrors());
            return "error-page";
        }
        return "redirect:/store-product";
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
        return employeeService.updateEmployee(employee);
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

    @SneakyThrows
    @PostMapping("/product")
    @ResponseBody
    public Response<Product> createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @SneakyThrows
    @PostMapping("/product/update")
    @ResponseBody
    public Response<Product> updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @SneakyThrows
    @DeleteMapping("/product/{id}")
    @ResponseBody
    public Response<Product> deleteProduct(@PathVariable("id") int productId) {
        return productService.deleteProduct(productId);
    }

    @SneakyThrows
    @GetMapping("/product/all")
    @ResponseBody
    public Response<List<Product>> getAllProductsSorted() {
        return productService.getAllProductsSortedByName();
    }

    @SneakyThrows
    @GetMapping("/product/all/{categoryId}")
    @ResponseBody
    public Response<List<Product>> getAllProductsInCategorySorted(@PathVariable("categoryId") int categoryId) {
        return productService.getAllProductsFromCategorySortedByName(categoryId);
    }

    @SneakyThrows
    @PostMapping("/customer")
    @ResponseBody
    public Response<CustomerCard> createCustomerCard(@RequestBody CustomerCard customerCard) {
        return customerService.createCustomerCard(customerCard);
    }

    @SneakyThrows
    @PostMapping("/customer/update")
    @ResponseBody
    public Response<CustomerCard> updateCustomerCard(@RequestBody CustomerCard customerCard) {
        return customerService.updateCustomerCard(customerCard);
    }

    @SneakyThrows
    @DeleteMapping("/customer/{id}")
    @ResponseBody
    public Response<CustomerCard> deleteCustomerCard(@PathVariable("id") int customerCardId) {
        return customerService.deleteCustomerCard(customerCardId);
    }

    @SneakyThrows
    @PostMapping("/store-product")
    @ResponseBody
    public Response<StoreProduct> createStoreProduct(@RequestBody StoreProduct storeProduct) {
        return storeProductService.createStoreProduct(storeProduct);
    }

    @SneakyThrows
    @PostMapping("/store-product/update")
    @ResponseBody
    public Response<StoreProduct> updateStoreProduct(@RequestBody StoreProduct storeProduct) {
        return storeProductService.updateStoreProduct(storeProduct);
    }

    @SneakyThrows
    @DeleteMapping("/store-product/{upc}")
    @ResponseBody
    public Response<StoreProduct> deleteStoreProduct(@PathVariable("upc") String productUpc) {
        return storeProductService.deleteStoreProduct(productUpc);
    }

    @SneakyThrows
    @GetMapping("/store-product/all/{productId}")
    @ResponseBody
    public Response<List<StoreProduct>> getAllStoreProductsFromProduct(@PathVariable("productId") int productId) {
        return storeProductService.getAllStoreProductsFromProduct(productId);
    }

    @SneakyThrows
    @PostMapping("/receipt")
    @ResponseBody
    public Response<Receipt> createReceipt(@RequestBody Receipt receipt) {
        return receiptService.createReceipt(receipt);
    }

    @SneakyThrows
    @PostMapping("/receipt/update")
    @ResponseBody
    public Response<Receipt> updateReceipt(@RequestBody Receipt receipt) {
        return receiptService.updateReceipt(receipt);
    }

    @SneakyThrows
    @DeleteMapping("/receipt/{id}")
    @ResponseBody
    public Response<Receipt> deleteStoreProduct(@PathVariable("id") Integer id) {
        return receiptService.deleteReceipt(id);
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
