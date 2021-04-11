package ua.edu.ukma.supermarket.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.supermarket.persistence.model.*;
import ua.edu.ukma.supermarket.persistence.service.*;
import ua.edu.ukma.supermarket.persistence.model.AdvancedStoreProduct;
import ua.edu.ukma.supermarket.persistence.model.BasicCustomerCard;
import ua.edu.ukma.supermarket.persistence.model.BasicStoredProduct;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        Response<List<Product>> productsResponse = productService.findAll();
        if (productsResponse.getErrors().size() > 0) {
            model.addAttribute("errors", productsResponse.getErrors());
            return "error-page";
        }
        List<Product> products = productsResponse.getObject();
        LinkedHashMap<Product, String> productsWithCategories = new LinkedHashMap<>();
        for (int i = 0; i < products.size(); i++) {
            Product currentProduct = products.get(i);
            Response<Category> categoryResponse = categoryService.findCategoryById(currentProduct.getCategoryNumber());
            if (categoryResponse.getErrors().size() > 0) {
                model.addAttribute("errors", categoryResponse.getErrors());
                return "error-page";
            }
            Category currentCategory = categoryResponse.getObject();
            productsWithCategories.put(currentProduct, currentCategory.getCategoryName());
        }
        model.addAttribute("products", productsWithCategories);
        return "products";
    }

    @GetMapping("/edit-product")
    public String editProduct(@ModelAttribute("productId") int id, Model model) {
        Response<Product> productResponse = productService.findProductById(id);
        if (productResponse.getErrors().size() > 0) {
            model.addAttribute("errors", productResponse.getErrors());
            return "error-page";
        }
        Product product = productResponse.getObject();
        Response<List<Category>> categoryResponse = categoryService.findAll();
        if (categoryResponse.getErrors().size() > 0) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        List<Category> categories = categoryResponse.getObject();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "product-edit";
    }

    @GetMapping("/add-product")
    public String addProduct(Model model) {

        Product product = new Product(-1,null,null,0);
        Response<List<Category>> categoryResponse = categoryService.findAll();
        if (categoryResponse.getErrors().size() > 0) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        List<Category> categories = categoryResponse.getObject();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "product-add";
    }

    @PostMapping("/request-add-product")
    public String requestAddProduct(@ModelAttribute Product product, Model model) {
        Response<Product> productResponse = productService.createProduct(product);
        if (productResponse.getErrors().size() > 0) {
            model.addAttribute("errors", productResponse.getErrors());
            return "error-page";
        }
        return "redirect:/product";
    }

    @PostMapping("/request-edit-product")
    public String requestEditProduct(@ModelAttribute Product product, Model model) {
        Response<Product> productResponse = productService.updateProduct(product);
        if (productResponse.getErrors().size() > 0) {
            model.addAttribute("errors", productResponse.getErrors());
            return "error-page";
        }
        return "redirect:/product";
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

    @GetMapping("/add-category")
    public String addCategory(Model model) {
        Category category = new Category(-1, null);
        model.addAttribute("category", category);
        return "category-add";
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


    @PostMapping("/request-add-category")
    public String requestAddCategory(@ModelAttribute Category category, Model model) {
        Response<Category> categoryResponse = categoryService.createCategory(category.getCategoryName());
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
        Response<List<Employee>> employeeResponse = employeeService.findAll();
        if (employeeResponse.getErrors().size() > 0) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("employees", employeeResponse.getObject());
        return "employees";
    }

    @GetMapping("/edit-employee")
    public String editEmployee(@ModelAttribute("employeeId") String id, Model model) {
        Response<Employee> employeeResponse = employeeService.findEmployeeById(id);
        if (employeeResponse.getErrors().size() > 0) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        String[] roles = {"Manager", "Cashier"};
        model.addAttribute("employee", employeeResponse.getObject());
        model.addAttribute("roles", roles);
        return "employee-edit";
    }

    @GetMapping("/add-employee")
    public String addEmployee(Model model) {
        Employee employee = new Employee(null, null, null, null, null,
                null, null, null, null, null, null, null);
        String[] roles = {"Manager", "Cashier"};
        model.addAttribute("employee", employee);
        model.addAttribute("roles", roles);
        return "employee-add";
    }

    @PostMapping("/request-add-employee")
    public String requestAddEmployee(@ModelAttribute("employeeId") String employeeId,
                                      @ModelAttribute("surname") String surname,
                                      @ModelAttribute("name") String name,
                                      @ModelAttribute("patronymic") String patronymic,
                                      @ModelAttribute("role") String role,
                                      @ModelAttribute("salary") Double salary,
                                      @ModelAttribute("birthDate") String birthDate,
                                      @ModelAttribute("startDate") String startDate,
                                      @ModelAttribute("phoneNumber") String phoneNumber,
                                      @ModelAttribute("city") String city,
                                      @ModelAttribute("street") String street,
                                      @ModelAttribute("zipCode") String zipCode, Model model) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDateProper = formatter.parse(birthDate);
        Date startDateProper = formatter.parse(startDate);
        Employee employee = new Employee(employeeId, surname, name, patronymic, role, salary, birthDateProper, startDateProper, phoneNumber, city, street, zipCode);
        Response<Employee> employeeResponse = employeeService.createEmployee(employee);
        if (employeeResponse.getErrors().size() > 0) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        return "redirect:/employee";
    }

    @PostMapping("/request-edit-employee")
    public String requestEditEmployee(@ModelAttribute("employeeId") String employeeId,
                                      @ModelAttribute("surname") String surname,
                                      @ModelAttribute("name") String name,
                                      @ModelAttribute("patronymic") String patronymic,
                                      @ModelAttribute("role") String role,
                                      @ModelAttribute("salary") Double salary,
                                      @ModelAttribute("birthDate") String birthDate,
                                      @ModelAttribute("startDate") String startDate,
                                      @ModelAttribute("phoneNumber") String phoneNumber,
                                      @ModelAttribute("city") String city,
                                      @ModelAttribute("street") String street,
                                      @ModelAttribute("zipCode") String zipCode, Model model) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDateProper = formatter.parse(birthDate);
        Date startDateProper = formatter.parse(startDate);
        Employee employee = new Employee(employeeId, surname, name, patronymic, role, salary, birthDateProper, startDateProper, phoneNumber, city, street, zipCode);
        Response<Employee> employeeResponse = employeeService.updateEmployee(employee);
        if (employeeResponse.getErrors().size() > 0) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        return "redirect:/employee";
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
        Response<List<CustomerCard>> customerCardResponse = customerService.findAll();
        if (customerCardResponse.getErrors().size() > 0) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("customers", customerCardResponse.getObject());
        return "customers";
    }

    @GetMapping("/edit-customer")
    public String editCustomer(@ModelAttribute("cardNumber") int id, Model model) {
        Response<CustomerCard> customerCardResponse = customerService.findCustomerCardById(id);
        if (customerCardResponse.getErrors().size() > 0) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("customer", customerCardResponse.getObject());
        return "customer-edit";
    }

    @GetMapping("/add-customer")
    public String addCustomer( Model model) {

        CustomerCard customerCard = new CustomerCard(0,null,null,null,null,null,null,null,1);
        model.addAttribute("customer", customerCard);
        return "customer-add";
    }

    @PostMapping("/request-add-customer")
    public String requestAddCustomer(@ModelAttribute CustomerCard customerCard, Model model) {

        Response<CustomerCard> customerCardResponse = customerService.createCustomerCard(customerCard);
        if (customerCardResponse.getErrors().size() > 0) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        return "redirect:/customer";
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

    @PostMapping("/request-edit-customer")
    public String requestEditCustomer(@ModelAttribute CustomerCard customerCard, Model model) {
        Response<CustomerCard> customerCardResponse = customerService.updateCustomerCard(customerCard);
        if (customerCardResponse.getErrors().size() > 0) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        return "redirect:/customer";
    }

    @GetMapping("/store-product")
    public String storeProductsPage(Model model) {
        Response<List<StoreProduct>> storeProductsResponse = storeProductService.findAll();


        if (storeProductsResponse.getErrors().size() > 0) {
            model.addAttribute("errors", storeProductsResponse.getErrors());
            return "error-page";
        }
        List<StoreProduct> storeProducts = storeProductsResponse.getObject();
        LinkedHashMap<StoreProduct, String> storeProductsWithNames = new LinkedHashMap<>();
        for (int i = 0; i < storeProducts.size(); i++) {
            StoreProduct currentStoreProduct = storeProducts.get(i);
            Response<Product> productResponse = productService.findProductById(currentStoreProduct.getProductId());
            if (productResponse.getErrors().size() > 0) {
                model.addAttribute("errors", productResponse.getErrors());
                return "error-page";
            }
            Product currentProduct = productResponse.getObject();
            storeProductsWithNames.put(currentStoreProduct, currentProduct.getProductName());
        }
        model.addAttribute("products", storeProductsWithNames);
        return "store-products";
    }

    @GetMapping("/edit-store-product")
    public String editStoreProduct(@ModelAttribute("upc") String upc, Model model) {
        Response<StoreProduct> storeProductResponse = storeProductService.findStoreProductByUpc(upc);
        if (storeProductResponse.getErrors().size() > 0) {
            model.addAttribute("errors", storeProductResponse.getErrors());
            return "error-page";
        }
        StoreProduct storeProduct = storeProductResponse.getObject();
        Response<List<StoreProduct>> storeProductListResponse = storeProductService.findAll();
        if (storeProductListResponse.getErrors().size() > 0) {
            model.addAttribute("errors", storeProductListResponse.getErrors());
            return "error-page";
        }
        List<StoreProduct> otherStoreProducts = storeProductListResponse.getObject();
        List<String> otherUPCs = otherStoreProducts.stream().filter(x -> !x.getUpc().equals(upc)).map(f -> f.getUpc()).collect(Collectors.toList());
        otherUPCs.add(0, null);
        Response<List<Product>> productResponse = productService.findAll();
        if (productResponse.getErrors().size() > 0) {
            model.addAttribute("errors", productResponse.getErrors());
            return "error-page";
        }
        List<Product> products = productResponse.getObject();
        LinkedHashMap<Boolean, String> promos = new LinkedHashMap<>();
        promos.put(true, "Yes");
        promos.put(false, "No");
        model.addAttribute("storeProduct", storeProduct);
        model.addAttribute("otherUPCs", otherUPCs);
        model.addAttribute("products", products);
        model.addAttribute("promos", promos);
        return "store-product-edit";
    }

    @GetMapping("/add-store-product")
    public String addStoreProduct( Model model) {

        StoreProduct storeProduct = new StoreProduct(null,null,0,0.01,0,false);
        Response<List<StoreProduct>> storeProductListResponse = storeProductService.findAll();
        if (storeProductListResponse.getErrors().size() > 0) {
            model.addAttribute("errors", storeProductListResponse.getErrors());
            return "error-page";
        }
        List<StoreProduct> otherStoreProducts = storeProductListResponse.getObject();
        List<String> otherUPCs = otherStoreProducts.stream().map(f -> f.getUpc()).collect(Collectors.toList());
        otherUPCs.add(0, null);
        Response<List<Product>> productResponse = productService.findAll();
        if (productResponse.getErrors().size() > 0) {
            model.addAttribute("errors", productResponse.getErrors());
            return "error-page";
        }
        List<Product> products = productResponse.getObject();
        LinkedHashMap<Boolean, String> promos = new LinkedHashMap<>();
        promos.put(true, "Yes");
        promos.put(false, "No");
        model.addAttribute("storeProduct", storeProduct);
        model.addAttribute("otherUPCs", otherUPCs);
        model.addAttribute("products", products);
        model.addAttribute("promos", promos);
        return "store-product-add";
    }

    @PostMapping("/request-add-store-product")
    public String requestAddStoreProduct(@ModelAttribute StoreProduct product, Model model) {
        Response<StoreProduct> storeProductResponse = storeProductService.createStoreProduct(product);
        if (storeProductResponse.getErrors().size() > 0) {
            model.addAttribute("errors", storeProductResponse.getErrors());
            return "error-page";
        }
        return "redirect:/store-product";
    }

    @PostMapping("/request-edit-store-product")
    public String requestEditStoreProduct(@ModelAttribute StoreProduct product, Model model) {
        Response<StoreProduct> storeProductResponse = storeProductService.updateStoreProduct(product);
        if (storeProductResponse.getErrors().size() > 0) {
            model.addAttribute("errors", storeProductResponse.getErrors());
            return "error-page";
        }
        return "redirect:/store-product";
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

    @GetMapping("receipt")
    public String receiptsPage(Model model) {
        Response<List<Receipt>> receiptsResponse = receiptService.findAll();
        if (receiptsResponse.getErrors().size() > 0) {
            model.addAttribute("errors", receiptsResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("receipts", receiptsResponse.getObject());
        return "receipts";
    }

    @PostMapping("/request-delete-receipt")
    public String removeReceipt(@ModelAttribute("receiptNumber") int receiptNumber, Model model) {
        Response<Receipt> receiptResponse = receiptService.deleteReceipt(receiptNumber);
        if (receiptResponse.getErrors().size() > 0) {
            model.addAttribute("errors", receiptResponse.getErrors());
            return "error-page";
        }
        return "redirect:/receipt";
    }

    @GetMapping("edit-receipt")
    public String editReceipt(@ModelAttribute("receiptNumber") int receiptNumber, Model model) {
        Response<Receipt> receiptResponse = receiptService.findReceiptById(receiptNumber);
        if (receiptResponse.getErrors().size() > 0) {
            model.addAttribute("errors", receiptResponse.getErrors());
            return "error-page";
        }
        Response<List<CustomerCard>> customerCardResponse = customerService.findAll();
        if (customerCardResponse.getErrors().size() > 0) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        Response<List<Employee>> employeeResponse = employeeService.findAll();
        if (employeeResponse.getErrors().size() > 0) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        List<Employee> employees = employeeResponse.getObject();
        List<CustomerCard> customerCards = customerCardResponse.getObject();
        List<String> employeeIds = employees.stream().map(f -> f.getEmployeeId()).collect(Collectors.toList());
        List<Integer> cardNumbers = customerCards.stream().map(f -> f.getCardNumber()).collect(Collectors.toList());
        model.addAttribute("employeeIds", employeeIds);
        model.addAttribute("cardNumbers", cardNumbers);
        model.addAttribute("receipt", receiptResponse.getObject());
        return "receipt-edit";
    }


    @GetMapping("add-receipt")
    public String addReceipt( Model model) {

        Response<List<CustomerCard>> customerCardResponse = customerService.findAll();
        if (customerCardResponse.getErrors().size() > 0) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        Response<List<Employee>> employeeResponse = employeeService.findAll();
        if (employeeResponse.getErrors().size() > 0) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }

       Receipt receipt=new Receipt(null,null,null,null,0.05,0.01);
        List<Employee> employees = employeeResponse.getObject();
        List<CustomerCard> customerCards = customerCardResponse.getObject();
        List<String> employeeIds = employees.stream().map(f -> f.getEmployeeId()).collect(Collectors.toList());
        List<Integer> cardNumbers = customerCards.stream().map(f -> f.getCardNumber()).collect(Collectors.toList());
        model.addAttribute("employeeIds", employeeIds);
        model.addAttribute("cardNumbers", cardNumbers);
        model.addAttribute("receipt", receipt);
        return "receipt-add";
    }

    @PostMapping("/request-add-receipt")
    public String requestAddReceipt(@ModelAttribute("employeeId") String employeeId,
                                     @ModelAttribute("cardNumber") Integer cardNumber,
                                     @ModelAttribute("printDate") String printDate,
                                     @ModelAttribute("sumTotal") Double sumTotal,
                                     @ModelAttribute("vat") Double vat,
                                     Model model) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date dateProper = formatter.parse(printDate);
        Receipt receipt = new Receipt(null, employeeId, cardNumber, dateProper, sumTotal, vat);
        Response<Receipt> receiptResponse = receiptService.createReceipt(receipt);
        if (receiptResponse.getErrors().size() > 0) {
            model.addAttribute("errors", receiptResponse.getErrors());
            return "error-page";
        }
        return "redirect:/receipt";
    }

    @PostMapping("/request-edit-receipt")
    public String requestEditReceipt(@ModelAttribute("receiptNumber") Integer receiptNumber,
                                     @ModelAttribute("employeeId") String employeeId,
                                     @ModelAttribute("cardNumber") Integer cardNumber,
                                     @ModelAttribute("printDate") String printDate,
                                     @ModelAttribute("sumTotal") Double sumTotal,
                                     @ModelAttribute("vat") Double vat,
                                     Model model) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date dateProper = formatter.parse(printDate);
        Receipt receipt = new Receipt(receiptNumber, employeeId, cardNumber, dateProper, sumTotal, vat);
        Response<Receipt> receiptResponse = receiptService.updateReceipt(receipt);
        if (receiptResponse.getErrors().size() > 0) {
            model.addAttribute("errors", receiptResponse.getErrors());
            return "error-page";
        }
        return "redirect:/receipt";
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
    @GetMapping({"/customer/all/basic"})
    @ResponseBody
    public Response<List<BasicCustomerCard>> getBasicCustomerInfo() {
        try {
            return this.customerService.getBasicCustomerInfo();
        } catch (Throwable var2) {
            throw var2;
        }
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

    @GetMapping({"/store-product/all/{productId}"})
    @ResponseBody
    public Response<List<StoreProduct>> getAllStoreProductsFromProduct(@PathVariable("productId") int productId) {
        try {
            return this.storeProductService.getAllStoreProductsFromProduct(productId);
        } catch (Throwable var3) {
            throw var3;
        }
    }

    // За UPC-товару знайти ціну продажу товару, кількість наявних одиниць товару.
    @GetMapping({"/store-product/{upc}"})
    @ResponseBody
    public Response<List<BasicStoredProduct>> getBasicStoredProductInfo(@PathVariable("upc") String upc) {
        try {
            return this.storeProductService.getBasicStoredProductInfo(upc);
        } catch (Throwable var3) {
            throw var3;
        }
    }

    // За UPC-товару знайти ціну продажу товару, кількість наявних одиниць товару, назву та характеристики товару.
    @GetMapping({"/store-product-advanced/{upc}"})
    @ResponseBody
    public Response<List<AdvancedStoreProduct>> getAdvancedStoredProductInfo(@PathVariable("upc") String upc) {
        try {
            return this.storeProductService.getAdvancedStoredProductInfo(upc);
        } catch (Throwable var3) {
            throw var3;
        }
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

    @SneakyThrows
    @GetMapping("/receipt/{id}")
    @ResponseBody
    public Response<List<Receipt>> findEmployeeReceiptsPeriod(@PathVariable("id") String id,
                                                              @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                              @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return receiptService.findReceiptsOfEmployeeFromPeriod(id, startDate, endDate);
    }

    @GetMapping({"/receipt/sum/{id}"})
    @ResponseBody
    public Response<Double> findSumEmployeeFromPeriod(@PathVariable("id") String id, @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            return this.receiptService.sumAllReceiptsByEmployeeFromPeriod(id, startDate, endDate);
        } catch (Throwable var5) {
            throw var5;
        }
    }

    //Визначити загальну кількість одиниць певного товару, проданого за певний період часу NOT WORKING

    @GetMapping({"/amount_of_sales_by_period"})
    @ResponseBody
    public Response<Integer> getAmountOfSalesForPeriodByProductId(@RequestParam("id") int id, @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            return this.productService.getAmountOfSalesForPeriodByProductId(id, startDate, endDate);
        } catch (Throwable var5) {
            throw var5;
        }
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

    @SneakyThrows
    @GetMapping("/store-product/regular/by-amount")
    @ResponseBody
    public Response<List<StoreProduct>> getAllRegularStoreProductsSortedByAmount() {
        return storeProductService.findAllPromotionalSortedByAmount(false);
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
