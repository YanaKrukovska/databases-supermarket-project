package ua.edu.ukma.supermarket.controller;

import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ua.edu.ukma.supermarket.persistence.Category;
import ua.edu.ukma.supermarket.persistence.CategoryRepository;

@Controller
public class ApplicationController {

    private final CategoryRepository categoryRepository = new CategoryRepository();

    @SneakyThrows
    @GetMapping("/category/{id}")
    public String singlePathVariable(@PathVariable("id") int id, Model model) {
        Category category = categoryRepository.findProductById(id);
        model.addAttribute("category", category);
        return "index";
    }
}
