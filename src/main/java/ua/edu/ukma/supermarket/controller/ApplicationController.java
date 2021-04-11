package ua.edu.ukma.supermarket.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.supermarket.persistence.model.*;
import ua.edu.ukma.supermarket.persistence.service.*;

import java.util.Date;
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

    @GetMapping("/category")
    public String categoriesPage(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "categories";
    }

    @GetMapping("/employee")
    public String employeesPage(Model model) {
        List<Employee> employees = employeeService.findAll();
        model.addAttribute("employees", employees);
        return "employees";
    }

    @GetMapping("/customer")
    public String customersPage(Model model) {
        List<CustomerCard> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        return "customers";
    }

    @GetMapping("/store-product")
    public String storeProductsPage(Model model) {
        List<StoreProduct> storeProducts = storeProductService.findAll();
        model.addAttribute("storeProducts", storeProducts);
        return "store-products";
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
    @GetMapping("/customer/{surname}")
    @ResponseBody
    public Response<List<CustomerCard>> findCustomersBySurname(@PathVariable("surname") String surname) {
        return customerService.findCustomersCardBySurname(surname);
    }

    @SneakyThrows
    @GetMapping("/customer/discount/{percent}")
    @ResponseBody
    public Response<List<CustomerCard>> findCustomersByPercent(@PathVariable("percent") int percent) {
        return customerService.findCustomersWithCertainPercent(percent);
    }

    // Скласти список усіх постійних клієнтів, що мають карту клієнта, по полях  ПІБ, телефон, адреса (якщо вказана)
    @SneakyThrows
    @GetMapping("/customer/all/basic")
    @ResponseBody
    public Response<List<BasicCustomerCard>> getBasicCustomerInfo() {
        return customerService.getBasicCustomerInfo();
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

    // За UPC-товару знайти ціну продажу товару, кількість наявних одиниць товару.
    @SneakyThrows
    @GetMapping("/store-product/{upc}")
    @ResponseBody
    public Response<List<BasicStoredProduct>> getBasicStoredProductInfo(@PathVariable("upc") String upc) {
        return storeProductService.getBasicStoredProductInfo(upc);
    }

    // За UPC-товару знайти ціну продажу товару, кількість наявних одиниць товару, назву та характеристики товару.
    @SneakyThrows
    @GetMapping("/store-product-advanced/{upc}")
    @ResponseBody
    public Response<List<AdvancedStoreProduct>> getAdvancedStoredProductInfo(@PathVariable("upc") String upc) {
        return storeProductService.getAdvancedStoredProductInfo(upc);
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

    // вилучення відомостей про товари та їх к-сть, куплені у певному чеку.
    @SneakyThrows
    @DeleteMapping("/sale/of_check")
    @ResponseBody
    public Response<Receipt> deleteSaleInfoInReceipt(@RequestParam("id") Integer id) {
        return receiptService.deleteSaleInfoInReceipt(id);
    }

    @SneakyThrows
    @GetMapping("/receipt/{id}")
    @ResponseBody
    public Response<List<Receipt>> findEmployeeReceiptsPeriod(@PathVariable("id") String id,
                                                              @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                              @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return receiptService.findReceiptsOfEmployeeFromPeriod(id, startDate, endDate);
    }

    @SneakyThrows
    @GetMapping("/receipt/sum/{id}")
    @ResponseBody
    public Response<Double> findSumEmployeeFromPeriod(@PathVariable("id") String id,
                                                      @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                      @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return receiptService.sumAllReceiptsByEmployeeFromPeriod(id, startDate, endDate);
    }

    @SneakyThrows
    @GetMapping("/receipt/detailed/{id}")
    @ResponseBody
    public Response<List<ReceiptDetailed>> findDetailedReceiptsOfEmployeeFromPeriod(@PathVariable("id") String id,
                                                                                    @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                                                    @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return receiptService.detailedReceiptsFromEmployeeFromPeriod(id, startDate, endDate);
    }

    @SneakyThrows
    @GetMapping("/receipt/detailed")
    @ResponseBody
    public Response<List<ReceiptDetailed>> findAllDetailedReceiptsFromPeriod(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return receiptService.findAllDetailedReceiptsFromPeriod(startDate, endDate);
    }

    //Визначити загальну кількість одиниць певного товару, проданого за певний період часу NOT WORKING

    @SneakyThrows
    @GetMapping("/amount_of_sales_by_period")
    @ResponseBody
    public Response<Integer> getAmountOfSalesForPeriodByProductId(@RequestParam("id") int id,
                                                                  @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                                  @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return productService.getAmountOfSalesForPeriodByProductId(id, startDate, endDate);
    }

    @SneakyThrows
    @GetMapping("/receipt/sum")
    @ResponseBody
    public Response<Double> findSumFromPeriod(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                              @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return receiptService.sumAllReceiptsFromPeriod(startDate, endDate);
    }

    @SneakyThrows
    @GetMapping("/store-product/promo/by-amount")
    @ResponseBody
    public Response<List<StoreProduct>> getAllPromoStoreProductsSortedByAmount() {
        return storeProductService.findAllPromotionalSortedByAmount(true);
    }

    // За номером чека скласти список усіх товарів, інформація про продаж яких є у цьому чеку;

    @SneakyThrows
    @GetMapping("/product/from_check")
    @ResponseBody
    public Response<List<Product>> allProductsFromCheck(@RequestParam("id") int id) {
        return productService.allProductsFromCheck(id);
    }

    @SneakyThrows
    @GetMapping("/store-product/regular/by-amount")
    @ResponseBody
    public Response<List<StoreProduct>> getAllRegularStoreProductsSortedByAmount() {
        return storeProductService.findAllPromotionalSortedByAmount(false);
    }


    @SneakyThrows
    @GetMapping("/store-product/regular/by-name")
    @ResponseBody
    public Response<List<StoreProductWithName>> getAllRegularStoreProductsSortedByName() {
        return storeProductService.findAllSortedByName(false);
    }

    @SneakyThrows
    @GetMapping("/store-product/promo/by-name")
    @ResponseBody
    public Response<List<StoreProductWithName>> getAllPromoStoreProductsSortedByName() {
        return storeProductService.findAllSortedByName(true);
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
