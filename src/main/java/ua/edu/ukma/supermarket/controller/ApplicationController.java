package ua.edu.ukma.supermarket.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.supermarket.persistence.model.*;
import ua.edu.ukma.supermarket.persistence.service.*;

import javax.annotation.security.RolesAllowed;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ApplicationController {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
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

    @Autowired
    private final SaleService saleService;

    public ApplicationController(CategoryService categoryService, EmployeeService employeeService, CustomerService customerService, ProductService productService, StoreProductService storeProductService, ReceiptService receiptService, SaleService saleService) {
        this.categoryService = categoryService;
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.productService = productService;
        this.storeProductService = storeProductService;
        this.receiptService = receiptService;
        this.saleService = saleService;
    }

    @GetMapping("/")
    public String basePage(Model model) {
        Category sampleCategory = new Category(23, "Weapons");
        model.addAttribute("category", sampleCategory);
        return "redirect:/login";
    }

    @GetMapping("/product")
    public String productsPage(Model model) {
        Response<List<Product>> productsResponse = productService.findAll();
        LinkedHashMap<Product, String> productsWithCategories = addCategoriesToProducts(model, productsResponse);
        if (productsWithCategories == null) {
            return "error-page";
        }
        model.addAttribute("products", productsWithCategories);
        return "products";
    }

    @GetMapping("product/sort")
    public String productSort(Model model) {
        Response<List<Product>> productsResponse = productService.getAllProductsSortedByName();
        LinkedHashMap<Product, String> productsWithCategories = addCategoriesToProducts(model, productsResponse);
        if (productsWithCategories == null) {
            return "error-page";
        }
        model.addAttribute("products", productsWithCategories);
        return "products";
    }

    private LinkedHashMap<Product, String> addCategoriesToProducts(Model model, Response<List<Product>> productsResponse) {
        List<Product> products = productsResponse.getObject();
        LinkedHashMap<Product, String> productsWithCategories = new LinkedHashMap<>();
        for (Product currentProduct : products) {
            Response<Category> categoryResponse = categoryService.findCategoryById(currentProduct.getCategoryNumber());
            if (!categoryResponse.getErrors().isEmpty()) {
                model.addAttribute("errors", categoryResponse.getErrors());
                return null;
            }
            Category currentCategory = categoryResponse.getObject();
            productsWithCategories.put(currentProduct, currentCategory.getCategoryName());
        }
        return productsWithCategories;
    }


    @GetMapping("/edit-product")
    public String editProduct(@ModelAttribute("productId") int id, Model model) {
        Response<Product> productResponse = productService.findProductById(id);
        if (!productResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", productResponse.getErrors());
            return "error-page";
        }
        Product product = productResponse.getObject();
        Response<List<Category>> categoryResponse = categoryService.findAll();
        if (!productResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        List<Category> categories = categoryResponse.getObject();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "product-edit";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @GetMapping("/add-product")
    public String addProduct(Model model) {

        Product product = new Product(-1, null, null, 0);
        Response<List<Category>> categoryResponse = categoryService.findAll();
        if (!categoryResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        List<Category> categories = categoryResponse.getObject();
        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "product-add";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-add-product")
    public String requestAddProduct(@ModelAttribute Product product, Model model) {
        Response<Product> productResponse = productService.createProduct(product);
        if (!productResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", productResponse.getErrors());
            return "error-page";
        }
        return "redirect:/product";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-edit-product")
    public String requestEditProduct(@ModelAttribute Product product, Model model) {
        Response<Product> productResponse = productService.updateProduct(product);
        if (!productResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", productResponse.getErrors());
            return "error-page";
        }
        return "redirect:/product";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-delete-product")
    public String removeProduct(@ModelAttribute("productId") int id, Model model) {
        Response<Product> productResponse = productService.deleteProduct(id);
        if (!productResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", productResponse.getErrors());
            return "error-page";
        }
        return "redirect:/product";
    }


    @GetMapping("/category")
    public String categoriesPage(Model model) {
        Response<List<Category>> categoryResponse = categoryService.findAll();
        if (!categoryResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("categories", categoryResponse.getObject());
        return "categories";
    }

    @GetMapping("/category/products")
    public String categoryProductsSorted(@ModelAttribute("categoryNumber") Integer id, Model model) {
        Response<List<Product>> productsResponse = productService.getAllProductsFromCategorySortedByName(id);
        if (!productsResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", productsResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("products", productsResponse.getObject());
        return "category-products";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @GetMapping("/edit-category")
    public String editCategory(@ModelAttribute("categoryNumber") int id, Model model) {
        Response<Category> categoryResponse = categoryService.findCategoryById(id);
        if (!categoryResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("category", categoryResponse.getObject());
        return "category-edit";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @GetMapping("/add-category")
    public String addCategory(Model model) {
        Category category = new Category(-1, null);
        model.addAttribute("category", category);
        return "category-add";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-edit-category")
    public String requestEditCategory(@ModelAttribute Category category, Model model) {
        Response<Category> categoryResponse = categoryService.updateCategory(category.getCategoryNumber(), category.getCategoryName());
        if (!categoryResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        return "redirect:/category";
    }


    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-add-category")
    public String requestAddCategory(@ModelAttribute Category category, Model model) {
        Response<Category> categoryResponse = categoryService.createCategory(category.getCategoryName());
        if (!categoryResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        return "redirect:/category";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-delete-category")
    public String removeCategory(@ModelAttribute("categoryNumber") int id, Model model) {
        Response<Category> categoryResponse = categoryService.deleteCategory(id);
        if (!categoryResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        return "redirect:/category";
    }

    @GetMapping("/employee")
    public String employeesPage(Model model) {
        Response<List<Employee>> employeeResponse = employeeService.findAll();
        if (!employeeResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("employees", employeeResponse.getObject());
        return "employees";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @GetMapping("/edit-employee")
    public String editEmployee(@ModelAttribute("employeeId") String id, Model model) {
        Response<Employee> employeeResponse = employeeService.findEmployeeById(id);
        if (!employeeResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        String[] roles = {"Manager", "Cashier"};
        model.addAttribute("employee", employeeResponse.getObject());
        model.addAttribute("roles", roles);
        return "employee-edit";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @GetMapping("/add-employee")
    public String addEmployee(Model model) {
        Employee employee = new Employee(null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null);
        String[] roles = {"Manager", "Cashier"};
        model.addAttribute("employee", employee);
        model.addAttribute("roles", roles);
        return "employee-add";
    }

    @RolesAllowed({"ROLE_MANAGER"})
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
                                     @ModelAttribute("zipCode") String zipCode,
                                     @ModelAttribute("username") String username,
                                     @ModelAttribute("password") String password,
                                     Model model) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date birthDateProper = formatter.parse(birthDate);
        Date startDateProper = formatter.parse(startDate);
        Employee employee = new Employee(employeeId, surname, name, patronymic, role, salary, birthDateProper,
                startDateProper, phoneNumber, city, street, zipCode, username, password);
        Response<Employee> employeeResponse = employeeService.createEmployee(employee);
        if (!employeeResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        return "redirect:/employee";
    }

    @RolesAllowed({"ROLE_MANAGER"})
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
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date birthDateProper = formatter.parse(birthDate);
        Date startDateProper = formatter.parse(startDate);
        Employee employee = new Employee(employeeId, surname, name, patronymic, role, salary, birthDateProper, startDateProper, phoneNumber, city, street, zipCode);
        Response<Employee> employeeResponse = employeeService.updateEmployee(employee);
        if (!employeeResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        return "redirect:/employee";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-delete-employee")
    public String removeEmployee(@ModelAttribute("employeeId") String id, Model model) {
        Response<Employee> employeeResponse = employeeService.deleteEmployee(id);
        if (!employeeResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        return "redirect:/employee";
    }

    @GetMapping("/customer")
    public String customersPage(Model model) {
        Response<List<CustomerCard>> customerCardResponse = customerService.findAll();
        if (!customerCardResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("customers", customerCardResponse.getObject());
        return "customers";
    }

    @GetMapping("/customer/basic")
    public String customerBasicPage(Model model) {
        Response<List<BasicCustomerCard>> customerCardResponse = customerService.getBasicCustomerInfo();
        if (!customerCardResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("customers", customerCardResponse.getObject());
        return "basic-customers";
    }

    @GetMapping("/edit-customer")
    public String editCustomer(@ModelAttribute("cardNumber") int id, Model model) {
        Response<CustomerCard> customerCardResponse = customerService.findCustomerCardById(id);
        if (!customerCardResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("customer", customerCardResponse.getObject());
        return "customer-edit";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @GetMapping("/add-customer")
    public String addCustomer(Model model) {

        CustomerCard customerCard = new CustomerCard(0, null, null, null, null, null, null, null, 1);
        model.addAttribute("customer", customerCard);
        return "customer-add";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-add-customer")
    public String requestAddCustomer(@ModelAttribute CustomerCard customerCard, Model model) {

        Response<CustomerCard> customerCardResponse = customerService.createCustomerCard(customerCard);
        if (!customerCardResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        return "redirect:/customer";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-delete-customer")
    public String removeCustomer(@ModelAttribute("cardNumber") int id, Model model) {
        Response<CustomerCard> customerCardResponse = customerService.deleteCustomerCard(id);
        if (!customerCardResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        return "redirect:/customer";
    }

    @PostMapping("/request-edit-customer")
    public String requestEditCustomer(@ModelAttribute CustomerCard customerCard, Model model) {
        Response<CustomerCard> customerCardResponse = customerService.updateCustomerCard(customerCard);
        if (!customerCardResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        return "redirect:/customer";
    }

    @GetMapping("/store-product")
    public String storeProductsPage(Model model) {
        Response<List<StoreProduct>> storeProductsResponse = storeProductService.findAll();


        if (!storeProductsResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", storeProductsResponse.getErrors());
            return "error-page";
        }
        List<StoreProduct> storeProducts = storeProductsResponse.getObject();
        LinkedHashMap<StoreProduct, String> storeProductsWithNames = new LinkedHashMap<>();
        for (StoreProduct currentStoreProduct : storeProducts) {
            Response<Product> productResponse = productService.findProductById(currentStoreProduct.getProductId());
            if (!productResponse.getErrors().isEmpty()) {
                model.addAttribute("errors", productResponse.getErrors());
                return "error-page";
            }
            Product currentProduct = productResponse.getObject();
            storeProductsWithNames.put(currentStoreProduct, currentProduct.getProductName());
        }
        model.addAttribute("products", storeProductsWithNames);
        return "store-products";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @GetMapping("/edit-store-product")
    public String editStoreProduct(@ModelAttribute("upc") String upc, Model model) {
        Response<StoreProduct> storeProductResponse = storeProductService.findStoreProductByUpc(upc);
        if (!storeProductResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", storeProductResponse.getErrors());
            return "error-page";
        }
        StoreProduct storeProduct = storeProductResponse.getObject();
        Response<List<StoreProduct>> storeProductListResponse = storeProductService.findAll();
        if (!storeProductListResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", storeProductListResponse.getErrors());
            return "error-page";
        }
        List<StoreProduct> otherStoreProducts = storeProductListResponse.getObject();
        List<String> otherUPCs = otherStoreProducts.stream().filter(x -> !x.getUpc().equals(upc)).map(StoreProduct::getUpc).collect(Collectors.toList());
        otherUPCs.add(0, null);
        Response<List<Product>> productResponse = productService.findAll();
        if (!productResponse.getErrors().isEmpty()) {
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

    @RolesAllowed({"ROLE_MANAGER"})
    @GetMapping("/add-store-product")
    public String addStoreProduct(Model model) {

        StoreProduct storeProduct = new StoreProduct(null, null, 0, 0.01, 0, false);
        Response<List<StoreProduct>> storeProductListResponse = storeProductService.findAll();
        if (!storeProductListResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", storeProductListResponse.getErrors());
            return "error-page";
        }
        List<StoreProduct> otherStoreProducts = storeProductListResponse.getObject();
        List<String> otherUPCs = otherStoreProducts.stream().map(StoreProduct::getUpc).collect(Collectors.toList());
        otherUPCs.add(0, null);
        Response<List<Product>> productResponse = productService.findAll();
        if (!productResponse.getErrors().isEmpty()) {
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

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-add-store-product")
    public String requestAddStoreProduct(@ModelAttribute StoreProduct product, Model model) {
        Response<StoreProduct> storeProductResponse = storeProductService.createStoreProduct(product);
        if (!storeProductResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", storeProductResponse.getErrors());
            return "error-page";
        }
        return "redirect:/store-product";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-edit-store-product")
    public String requestEditStoreProduct(@ModelAttribute StoreProduct product, Model model) {
        Response<StoreProduct> storeProductResponse = storeProductService.updateStoreProduct(product);
        if (!storeProductResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", storeProductResponse.getErrors());
            return "error-page";
        }
        return "redirect:/store-product";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-delete-store-product")
    public String removeStoreProduct(@ModelAttribute("upc") String upc, Model model) {
        Response<StoreProduct> storeProductResponse = storeProductService.deleteStoreProduct(upc);
        if (!storeProductResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", storeProductResponse.getErrors());
            return "error-page";
        }
        return "redirect:/store-product";
    }

    @GetMapping("receipt")
    public String receiptsPage(Model model) {
        Response<List<Receipt>> receiptsResponse = receiptService.findAll();
        if (!receiptsResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", receiptsResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("receipts", receiptsResponse.getObject());
        return "receipts";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-delete-receipt")
    public String removeReceipt(@ModelAttribute("receiptNumber") int receiptNumber, Model model) {
        Response<Receipt> receiptResponse = receiptService.deleteReceipt(receiptNumber);
        if (!receiptResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", receiptResponse.getErrors());
            return "error-page";
        }
        return "redirect:/receipt";
    }

    @GetMapping("edit-receipt")
    public String editReceipt(@ModelAttribute("receiptNumber") int receiptNumber, Model model) {
        Response<Receipt> receiptResponse = receiptService.findReceiptById(receiptNumber);
        if (!receiptResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", receiptResponse.getErrors());
            return "error-page";
        }
        Response<List<CustomerCard>> customerCardResponse = customerService.findAll();
        if (!customerCardResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        Response<List<Employee>> employeeResponse = employeeService.findAll();
        if (!employeeResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        List<Employee> employees = employeeResponse.getObject();
        List<CustomerCard> customerCards = customerCardResponse.getObject();
        List<String> employeeIds = employees.stream().map(Employee::getEmployeeId).collect(Collectors.toList());
        List<Integer> cardNumbers = customerCards.stream().map(CustomerCard::getCardNumber).collect(Collectors.toList());
        model.addAttribute("employeeIds", employeeIds);
        model.addAttribute("cardNumbers", cardNumbers);
        model.addAttribute("receipt", receiptResponse.getObject());
        return "receipt-edit";
    }


    @GetMapping("add-receipt")
    public String addReceipt(Model model) {

        Response<List<CustomerCard>> customerCardResponse = customerService.findAll();
        if (!customerCardResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", customerCardResponse.getErrors());
            return "error-page";
        }
        Response<List<Employee>> employeeResponse = employeeService.findAll();
        if (!employeeResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }

        Receipt receipt = new Receipt(null, null, null, null, 0.0, 0.0);
        List<Employee> employees = employeeResponse.getObject();
        List<CustomerCard> customerCards = customerCardResponse.getObject();
        List<String> employeeIds = employees.stream().map(Employee::getEmployeeId).collect(Collectors.toList());
        List<Integer> cardNumbers = customerCards.stream().map(CustomerCard::getCardNumber).collect(Collectors.toList());
        model.addAttribute("employeeIds", employeeIds);
        model.addAttribute("cardNumbers", cardNumbers);
        model.addAttribute("receipt", receipt);
        return "receipt-add";
    }

    @RolesAllowed({"ROLE_MANAGER"})
    @PostMapping("/request-add-receipt")
    public String requestAddReceipt(@ModelAttribute("employeeId") String employeeId,
                                    @ModelAttribute("cardNumber") Integer cardNumber,
                                    @ModelAttribute("printDate") String printDate,
                                    @ModelAttribute("sumTotal") Double sumTotal,
                                    @ModelAttribute("vat") Double vat,
                                    Model model) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date dateProper = formatter.parse(printDate);
        Receipt receipt = new Receipt(null, employeeId, cardNumber, dateProper, sumTotal, vat);
        Response<Receipt> receiptResponse = receiptService.createReceipt(receipt);
        if (!receiptResponse.getErrors().isEmpty()) {
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
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Date dateProper = formatter.parse(printDate);
        Receipt receipt = new Receipt(receiptNumber, employeeId, cardNumber, dateProper, sumTotal, vat);
        Response<Receipt> receiptResponse = receiptService.updateReceipt(receipt);
        if (!receiptResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", receiptResponse.getErrors());
            return "error-page";
        }
        return "redirect:/receipt";
    }

    @GetMapping("receipt/products")
    public String seeReceiptProducts(@ModelAttribute("receiptNumber") Integer receiptNumber, Model model) {
        List<Product> products = productService.allProductsFromCheck(receiptNumber).getObject();
        model.addAttribute("products", products);
        return "receipt-products";
    }

    @SneakyThrows
    @GetMapping("/receipt/find")
    @ResponseBody
    public Response<Receipt> createReceipt(@RequestParam int id) {
        return receiptService.findReceiptById(id);
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

    //Скласти список усіх категорій, відсортованих за назвою
    @GetMapping("/category/all")
    public String getAllCategoriesSorted(Model model) {
        String[] categoryColumnNames = new String[]{"ID", "Name"};
        Response<List<Category>> categoryResponse = categoryService.getCategoriesSortedByName();
        if (!categoryResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", categoryResponse.getErrors());
            return "error-page";
        }
        List<Category> categories = categoryResponse.getObject();
        List<String[]> values = new LinkedList<>();
        for (Category value : categories) {
            values.add(new String[]{String.valueOf(value.getCategoryNumber()), value.getCategoryName()});
        }
        model.addAttribute("queryText", "Скласти список усіх категорій, відсортованих за назвою");
        model.addAttribute("columnNames", categoryColumnNames);
        model.addAttribute("values", values);
        return "base-table";
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

    //-	Скласти список працівників, що займають посаду касира, відсортованих за прізвищем
    @GetMapping("/employee/cashiers")
    public String getAllCashiersSortedBySurname(Model model) {
        String[] employeeColumnNames = new String[]{"ID", "Surname", "Name", "Patronymic", "Role", "Salary",
                "Birth date", "Start date", "Phone number", "City", "Street", "Zip code"};
        Response<List<Employee>> employeeResponse = employeeService.getCashiersSortedBySurname();
        if (!employeeResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        List<Employee> cashiers = employeeResponse.getObject();
        List<String[]> values = new LinkedList<>();
        for (Employee cashier : cashiers) {
            String[] fields = new String[]{cashier.getEmployeeId(), cashier.getSurname(), cashier.getName(),
                    cashier.getPatronymic(), cashier.getRole(), cashier.getSalary().toString(),
                    cashier.getBirthDate().toString(), cashier.getStartDate().toString(),
                    cashier.getPhoneNumber(), cashier.getCity(), cashier.getStreet(), cashier.getZipCode()};
            values.add(fields);
        }
        model.addAttribute("queryText", "Скласти список працівників, що займають посаду касира, відсортованих за прізвищем");
        model.addAttribute("columnNames", employeeColumnNames);
        model.addAttribute("values", values);
        return "base-table";
    }


    // За прізвищем працівника знайти його телефон та адресу;
    @GetMapping("/employee/contacts")
    public String findEmployeeNumberAddressBySurname(@ModelAttribute("surname") String surname, Model model) {
        String[] employeeColumnNames = new String[]{"Phone number", "City", "Street", "Zip code"};
        Response<List<Employee>> employeeResponse = employeeService.findPhoneNumberAndAddressBySurname(surname);
        if (!employeeResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", employeeResponse.getErrors());
            return "error-page";
        }
        List<Employee> employees = employeeResponse.getObject();
        List<String[]> values = new LinkedList<>();
        for (Employee cashier : employees) {
            String[] fields = new String[]{cashier.getPhoneNumber(), cashier.getCity(), cashier.getStreet(), cashier.getZipCode()};
            values.add(fields);
        }
        model.addAttribute("queryText", "За прізвищем працівника знайти його телефон та адресу: " + surname);
        model.addAttribute("columnNames", employeeColumnNames);
        model.addAttribute("values", values);
        return "base-table";
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

    //Скласти список всіх товарів, що належать певній категорії
    @GetMapping("/product/all/from-category")
    public String getAllProductsInCategorySorted(@ModelAttribute("categoryNumber") int categoryNumber, Model model) {
        String[] productColumnNames = new String[]{"ID", "Name", "Characteristics", "Category number"};
        Response<List<Product>> productResponse = productService.getAllProductsFromCategorySortedByName(categoryNumber);
        if (!productResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", productResponse.getErrors());
            return "error-page";
        }
        List<Product> products = productResponse.getObject();
        List<String[]> values = new LinkedList<>();
        for (Product product : products) {
            String[] fields = new String[]{String.valueOf(product.getProductId()), product.getProductName(), product.getCharacteristics(), String.valueOf(product.getCategoryNumber())};
            values.add(fields);
        }
        model.addAttribute("queryText", "Скласти список всіх товарів, що належать певній категорії: " + categoryNumber);
        model.addAttribute("columnNames", productColumnNames);
        model.addAttribute("values", values);
        return "base-table";
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
    public Response<BasicStoredProduct> getBasicStoredProductInfo(@PathVariable("upc") String upc) {
        return storeProductService.getBasicStoredProductInfo(upc);
    }

    // За UPC-товару знайти ціну продажу товару, кількість наявних одиниць товару, назву та характеристики товару.
    @SneakyThrows
    @GetMapping("/store-product-advanced/{upc}")
    @ResponseBody
    public Response<AdvancedStoreProduct> getAdvancedStoredProductInfo(@PathVariable("upc") String upc) {
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


    //Скласти список чеків, видрукуваних певним касиром за певний період часу
    @GetMapping("/receipt/detailed/from-employee")
    public String findDetailedReceiptsOfEmployeeFromPeriod(@ModelAttribute("employeeId") String employeeId,
                                                           @ModelAttribute("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                           @ModelAttribute("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, Model model) {
        List<ReceiptDetailed> receipts = receiptService.detailedReceiptsFromEmployeeFromPeriod(employeeId, startDate, endDate).getObject();
        String[] receiptColumnNames = new String[]{"Receipt ID", "Employee ID", "Card number", "Print date",
                "Total sum", "VAT", "Product name", "Product amount", "Product price"};
        List<String[]> values = new LinkedList<>();
        for (ReceiptDetailed receipt : receipts) {
            for (ReceiptDetailed.ProductDetails detail : receipt.getProductDetailsList()) {
                String[] fields = new String[]{String.valueOf(receipt.getReceiptNumber()), receipt.getEmployeeId(), receipt.getCardNumber().toString(),
                        receipt.getPrintDate().toString(), String.valueOf(receipt.getSumTotal()), String.valueOf(receipt.getVat()),
                        detail.getProductName(), String.valueOf(detail.getProductAmount()), String.valueOf(detail.getProductPrice())};
                values.add(fields);
            }

        }
        model.addAttribute("queryText", "Скласти список чеків, видрукуваних певним касиром за певний період часу: " + employeeService.findEmployeeById(employeeId).getObject().getName() +
                "   " + employeeService.findEmployeeById(employeeId).getObject().getSurname() + "  " + startDate.toString() + " - " + endDate.toString());
        model.addAttribute("columnNames", receiptColumnNames);
        model.addAttribute("values", values);
        return "base-table";
    }

    // Скласти список чеків, видрукуваних усіма касирами за певний період часу

    @GetMapping("/receipt/detailed")
    public String findAllDetailedReceiptsFromPeriod(
            @ModelAttribute("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @ModelAttribute("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, Model model) {

        List<ReceiptDetailed> receipts = receiptService.findAllDetailedReceiptsFromPeriod(startDate, endDate).getObject();
        String[] receiptColumnNames = new String[]{"Receipt ID", "Employee ID", "Card number", "Print date",
                "Total sum", "VAT", "Product name", "Product amount", "Product price"};
        List<String[]> values = new LinkedList<>();
        for (ReceiptDetailed receipt : receipts) {
            for (ReceiptDetailed.ProductDetails detail : receipt.getProductDetailsList()) {
                String[] fields = new String[]{String.valueOf(receipt.getReceiptNumber()), receipt.getEmployeeId(), receipt.getCardNumber().toString(),
                        receipt.getPrintDate().toString(), String.valueOf(receipt.getSumTotal()), String.valueOf(receipt.getVat()),
                        detail.getProductName(), String.valueOf(detail.getProductAmount()), String.valueOf(detail.getProductPrice())};
                values.add(fields);
            }

        }
        model.addAttribute("queryText", "Скласти список чеків, видрукуваних усіма касирами за певний період часу: " + startDate.toString() + " - " + endDate.toString());
        model.addAttribute("columnNames", receiptColumnNames);
        model.addAttribute("values", values);
        return "base-table";
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
    public Response<List<StoreProductWithName>> getAllPromoStoreProductsSortedByAmount() {
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
    public Response<List<StoreProductWithName>> getAllRegularStoreProductsSortedByAmount() {
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
    @GetMapping("/store-product/all/except")
    public String findStoreProductsNotInCategory(@ModelAttribute("categoryName") String categoryName, Model model) {
        List<StoreProduct> products = storeProductService.findAllStoreProductsNotInCategory(categoryName).getObject();

        String[] storeProductColumnNames = new String[]{"UPC", "Promo UPC", "Product ID", "Selling price",
                "Products number", "Is promotional product"};
        List<String[]> values = new LinkedList<>();
        for (StoreProduct product : products) {
            String[] fields = new String[]{product.getUpc(), product.getUpcPromo(), String.valueOf(product.getProductId()),
                    product.getSellingPrice() + "UAH", String.valueOf(product.getProductsNumber()),
                    product.isPromotionalProductString()};
            values.add(fields);
        }
        model.addAttribute("queryText", "Знайти всі товари в магазині, що не належать певній категорії: " + categoryName);
        model.addAttribute("columnNames", storeProductColumnNames);
        model.addAttribute("values", values);
        return "base-table";
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

    @GetMapping("/manager")
    public String managerPage(Model model) {
        model.addAttribute("categories", categoryService.findAll().getObject());
        model.addAttribute("products", productService.findAll().getObject());
        model.addAttribute("employees", employeeService.findAll().getObject());
        return "manager";
    }

    // Скласти список товарів у магазині, що належать певному товару
    @GetMapping("/store-products/from-product")
    public String storeProductsFromProduct(@ModelAttribute("productId") int productId, Model model) {
        List<StoreProduct> products = storeProductService.findAll().getObject();

        String[] storeProductColumnNames = new String[]{"UPC", "Promo UPC", "Product ID", "Selling price",
                "Products number", "Is promotional product"};
        products = products.stream().filter(x -> x.getProductId() == productId).collect(Collectors.toList());
        List<String[]> values = new LinkedList<>();
        for (StoreProduct product : products) {
            String[] fields = new String[]{product.getUpc(), product.getUpcPromo(), String.valueOf(product.getProductId()),
                    product.getSellingPrice() + "UAH", String.valueOf(product.getProductsNumber()),
                    product.isPromotionalProductString()};
            values.add(fields);
        }
        model.addAttribute("queryText", "Скласти список товарів у магазині, що належать певному товару: " + productService.findProductById(productId).getObject().getProductName());
        model.addAttribute("columnNames", storeProductColumnNames);
        model.addAttribute("values", values);
        return "base-table";
    }

    @GetMapping("/print-check")
    public String printCheck(@ModelAttribute("receiptNumber") int receiptNumber, Model model) {
        Receipt receipt = receiptService.findReceiptById(receiptNumber).getObject();
        List<ReceiptDetailed.ProductDetails> details = receiptService.findProductDetails(receipt);
        String check1 = "+-------------------------------------+\n" +
                "|                                     |\n" +
                "|            FIESTA MARKET            |\n" +
                "|                                     |\n" +
                "|          3015 Adams Avenue          |\n" +
                "|          " + receipt.getPrintDate().toString() + "          |\n" +
                "|         San Diego, CA 92116         |\n" +
                "|           00000000000" + receiptNumber + "            |\n" +
                "|                                     |\n";
        int sum = 0;
        for (ReceiptDetailed.ProductDetails item : details) {
            check1 += "|  " + item.getProductName() + "       " + item.getProductAmount() + "*" + item.getProductPrice() + " UAH  |\n";
            sum += item.getProductAmount() * item.getProductPrice();
        }
        String check2 = "|                                     |\n" +
                "|  Subtotal                  " + sum + " UAH    |\n" +
                "|                                     |\n" +
                "|  TOTAL                     " + sum + " UAH    |\n" +
                "|                                     |\n" +
                "|             THANK YOU!         :F_P:|\n" +
                "+-------------------------------------+";
        String finalCheck = check1 + check2;
        model.addAttribute("check", finalCheck);
        return "check-print";
    }

    @GetMapping("/cashier")
    public String cashierPage(Model model) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("employee", employeeService.findEmployeeById(employee.getEmployeeId()).getObject());
        return "cashier";
    }

    @SneakyThrows
    @GetMapping("/cashier/receipts")
    public String cashierReceiptsPage(@ModelAttribute("start") String start, @ModelAttribute("end") String end,
                                      Model model) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);

        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Receipt> receipts = receiptService.findReceiptsOfEmployeeFromPeriod(employee.getEmployeeId(),
                formatter.parse(start), formatter.parse(end)).getObject();
        model.addAttribute("receipts", receipts);
        return "cashier-receipts";
    }

    @GetMapping("/product-sorted")
    public String productSortPage(Model model) {
        Response<List<StoreProductWithName>> productsResponse = storeProductService.findAllWithName();
        if (!productsResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", productsResponse.getErrors());
            return "error-page";
        }

        model.addAttribute("products", productsResponse.getObject());
        return "products-sorted";
    }

    @GetMapping("/store/products/sort")
    public String storeProductSort(@ModelAttribute("sort") String sort, Model model) {
        Response<List<StoreProductWithName>> productsResponse;
        switch (sort) {
            case "promo-amount":
                productsResponse = storeProductService.findAllPromotionalSortedByAmount(true);
                break;
            case "regular-amount":
                productsResponse = storeProductService.findAllPromotionalSortedByAmount(false);
                break;
            case "promo-name":
                productsResponse = storeProductService.findAllSortedByName(true);
                break;
            default:
                productsResponse = storeProductService.findAllSortedByName(false);
                break;
        }

        model.addAttribute("products", productsResponse.getObject());
        return "products-sorted";
    }

    @GetMapping("/sale")
    public String salesPage(Model model) {
        Response<List<Sale>> salesResponse = saleService.findAll();
        if (!salesResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", salesResponse.getErrors());
            return "error-page";
        }
        model.addAttribute("sales", salesResponse.getObject());
        return "sales";
    }

    @GetMapping("/add-sale")
    public String addSale(Model model) {
        Response<List<StoreProduct>> storeProductResponse = storeProductService.findAll();
        if (!storeProductResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", storeProductResponse.getErrors());
            return "error-page";
        }
        Response<List<Receipt>> receiptResponse = receiptService.findAll();
        if (!receiptResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", receiptResponse.getErrors());
            return "error-page";
        }
        Sale sale = new Sale(null, null, 0, 0);
        model.addAttribute("storeProducts", storeProductResponse.getObject());
        model.addAttribute("receipts", receiptResponse.getObject());
        model.addAttribute("sale", sale);
        return "sale-add";
    }

    @PostMapping("/request-add-sale")
    public String requestAddSale(@ModelAttribute Sale sale, Model model) {
        Response<Sale> saleResponse = saleService.createSale(sale);
        Response<Receipt> updateReceiptResponse = receiptService.updateReceipt(receiptService.findReceiptById(Integer.valueOf(sale.getReceiptNumber())).getObject());
        if (!saleResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", saleResponse.getErrors());
            return "error-page";
        } else if (!updateReceiptResponse.getErrors().isEmpty()) {
            model.addAttribute("errors", updateReceiptResponse.getErrors());
            return "error-page";
        }
        return "redirect:/sale";
    }

}
