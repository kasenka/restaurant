package org.example.controller;

import jakarta.validation.Valid;
import org.example.model.MenuItem;
import org.example.repository.MenuItemRepository;
import org.example.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Controller
@RequestMapping("/menu")
@SessionAttributes({"items", "page", "categories", "authorize"})
public class MenuController {

    @Autowired
    private MenuItemRepository menuRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @GetMapping("")
    public String index(Model model, Principal principal){
        List<MenuItem> items = menuRepository.findAll();
        model.addAttribute("items", items);
        model.addAttribute("page", "menu");
        model.addAttribute("categories", items.stream().map(item -> item.getType()).distinct().toList());

        if (principal != null) {
            model.addAttribute("authorize", true);
        }
        return "menu";
    }

    @PostMapping("/filter")
    public String filter(@RequestParam(required = false) String string,
                         @RequestParam(required = false) String type,
                         Model model){
        List<MenuItem> items = menuRepository.findAll();

        if (string != null && !type.equals("all")){
            items = menuRepository.filterAndSearch(type, string);
        } else if (string != null) {
            items = menuRepository.search(string);
        } else if (!type.equals("all")){
            items = menuRepository.filterByType(type);
        }
        model.addAttribute("items", items);
        return "menu";
    }


    @GetMapping("/create")
    @PreAuthorize("hasRole('WORKER')")
    public String createPage(Model model){
        model.addAttribute("menuItem", new MenuItem());
        return "create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('WORKER')")
    public String create(@Valid @ModelAttribute("menuItem") MenuItem menuItem,
                         BindingResult result, Model model){

        if (result.hasErrors()) {
            if (result.hasFieldErrors("price")) {
                model.addAttribute("priceError", "Цена должна быть положительным числом");
            }
            return "create";
        }
        MenuItem newItem = new MenuItem();

        newItem.setName(menuItem.getName());
        newItem.setDescription(menuItem.getDescription());
        newItem.setType(menuItem.getType());
        newItem.setIngredients(menuItem.getIngredients());
        newItem.setPrice(menuItem.getPrice());

        menuRepository.save(newItem);

        return "redirect:/menu";
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    public String show(@PathVariable long id, Model model){

        MenuItem item = menuRepository.findById(id).get();
        model.addAttribute("item", item);
        model.addAttribute("id", id);

        return "menuWorkerShow";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('WORKER')")
    public String update(@PathVariable long id, @Valid @ModelAttribute MenuItem menuItem,
                         BindingResult result, Model model){

        if (result.hasErrors()) {
            if (result.hasFieldErrors("price")) {
                model.addAttribute("priceError", "Цена должна быть положительным числом");
                model.addAttribute("item", menuItem);
                model.addAttribute("id", id);
            }
            return "menuWorkerShow";
        }

        MenuItem item = menuRepository.findById(id)
                .orElseThrow(() -> {
                    return new RuntimeException("Menu item not found with id: " + id);
                });

        item.setName(menuItem.getName());
        item.setDescription(menuItem.getDescription());
        item.setType(menuItem.getType());
        item.setIngredients(menuItem.getIngredients());
        item.setPrice(menuItem.getPrice());

        menuRepository.save(item);

        return "redirect:/menu";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('WORKER')")
    public String delete(@PathVariable long id){

        menuRepository.deleteById(id);

        return "redirect:/menu";
    }
}
