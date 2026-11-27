package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.model.AddressForm;
import com.spring.henallux.hecathlon.model.DeliveryForm;
import com.spring.henallux.hecathlon.model.Order;
import com.spring.henallux.hecathlon.model.OrderStatus;
import com.spring.henallux.hecathlon.model.PasswordForm;
import com.spring.henallux.hecathlon.model.ProfileForm;
import com.spring.henallux.hecathlon.model.User;
import com.spring.henallux.hecathlon.model.ValidationRules;
import com.spring.henallux.hecathlon.services.AccountService;
import com.spring.henallux.hecathlon.services.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/compte")
public class AccountController {
    private final AccountService accountService;
    private final OrderService orderService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Autowired
    public AccountController(AccountService accountService, OrderService orderService) {
        this.accountService = accountService;
        this.orderService = orderService;
    }

    @ModelAttribute("account")
    public User account(@AuthenticationPrincipal User principal) {
        return accountService.getUser(principal.getUsername());
    }

    @ModelAttribute("orders")
    public List<Order> orders(@AuthenticationPrincipal User principal) {
        return orderService.findByCustomer(principal.getUsername());
    }

    @ModelAttribute("title")
    public String title() {
        return "Hecathlon";
    }

    @GetMapping
    public String overview(Model model, @ModelAttribute("orders") List<Order> orders) {
        addKeyFigures(model, orders);
        return "account-overview";
    }

    @GetMapping("/commandes")
    public String orders(Model model,
                         @ModelAttribute("orders") List<Order> orders,
                         @RequestParam(name = "filtre", required = false) String filter) {
        List<Order> shown = switch (filter == null ? "" : filter) {
            case "en-cours" -> orders.stream().filter(o -> o.getStatus().isInProgress()).toList();
            case "livrees" -> orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).toList();
            default -> orders;
        };
        addKeyFigures(model, orders);
        model.addAttribute("filter", filter);
        model.addAttribute("shownOrders", shown);
        return "account-orders";
    }

    @GetMapping("/commandes/{orderId}")
    public String order(Model model, @PathVariable Long orderId, @AuthenticationPrincipal User principal) {
        Order order = orderService.findForCustomer(orderId, principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.TRACKING_STEPS);
        return "account-order";
    }

    @GetMapping("/informations")
    public String information(Model model, @ModelAttribute("account") User account) {
        model.addAttribute("profileForm", ProfileForm.of(account));
        model.addAttribute("passwordForm", new PasswordForm());
        return "account-info";
    }

    @PostMapping("/informations")
    public String saveInformation(Model model,
                                  @Valid @ModelAttribute("profileForm") ProfileForm form,
                                  BindingResult errors,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        if (errors.hasErrors()) {
            model.addAttribute("passwordForm", new PasswordForm());
            return "account-info";
        }
        User saved = accountService.updateProfile(currentEmail(), form);
        refreshLoggedInUser(saved, request, response);
        return "redirect:/compte/informations?enregistre";
    }

    @PostMapping("/mot-de-passe")
    public String changePassword(Model model,
                                 @ModelAttribute("account") User account,
                                 @Valid @ModelAttribute("passwordForm") PasswordForm form,
                                 BindingResult errors) {
        if (!errors.hasFieldErrors("confirmPassword") && !form.getConfirmPassword().equals(form.getNewPassword())) {
            errors.rejectValue("confirmPassword", "password.mismatch");
        }
        if (!errors.hasErrors() && !accountService.changePassword(account.getEmail(), form)) {
            errors.rejectValue("currentPassword", "password.wrong");
        }
        if (errors.hasErrors()) {
            model.addAttribute("profileForm", ProfileForm.of(account));
            return "account-info";
        }
        return "redirect:/compte/informations?motdepasse";
    }

    @GetMapping("/adresses")
    public String address(Model model, @ModelAttribute("account") User account) {
        model.addAttribute("addressForm", AddressForm.of(account));
        model.addAttribute("countries", DeliveryForm.COUNTRIES);
        return "account-address";
    }

    @PostMapping("/adresses")
    public String saveAddress(Model model, @Valid @ModelAttribute("addressForm") AddressForm form, BindingResult errors) {
        if (!ValidationRules.COUNTRIES.contains(form.getCountry())) {
            errors.rejectValue("country", "NotBlank");
        }
        if (errors.hasErrors()) {
            model.addAttribute("countries", DeliveryForm.COUNTRIES);
            return "account-address";
        }
        accountService.updateAddress(currentEmail(), form);
        return "redirect:/compte/adresses?enregistre";
    }

    private void addKeyFigures(Model model, List<Order> orders) {
        int year = LocalDate.now().getYear();
        model.addAttribute("ordersThisYear", orders.stream().filter(o -> o.getDate().getYear() == year).count());
        model.addAttribute("inProgressCount", orders.stream().filter(o -> o.getStatus().isInProgress()).count());
        model.addAttribute("deliveredCount", orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count());
    }

    private static String currentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private void refreshLoggedInUser(User user, HttpServletRequest request, HttpServletResponse response) {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(user, current.getCredentials(), user.getAuthorities()));
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

}
