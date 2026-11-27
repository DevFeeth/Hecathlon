package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.constants.Constants;
import com.spring.henallux.hecathlon.model.Cart;
import com.spring.henallux.hecathlon.model.DeliveryForm;
import com.spring.henallux.hecathlon.model.DeliveryMode;
import com.spring.henallux.hecathlon.model.Order;
import com.spring.henallux.hecathlon.model.User;
import com.spring.henallux.hecathlon.model.ValidationRules;
import com.spring.henallux.hecathlon.services.AccountService;
import com.spring.henallux.hecathlon.services.OrderService;
import com.spring.henallux.hecathlon.services.payment.PaymentException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Controller
@RequestMapping("/commande")
@SessionAttributes({Constants.CART, Constants.DELIVERY})
public class OrderController {
    private final OrderService orderService;
    private final AccountService accountService;

    @Autowired
    public OrderController(OrderService orderService, AccountService accountService) {
        this.orderService = orderService;
        this.accountService = accountService;
    }

    @ModelAttribute(Constants.CART)
    public Cart cart() {
        return new Cart();
    }

    @GetMapping("/livraison")
    public String delivery(Model model,
                           @ModelAttribute(Constants.CART) Cart cart,
                           @SessionAttribute(name = Constants.DELIVERY, required = false) DeliveryForm saved,
                           @AuthenticationPrincipal User user) {
        if (cart.isEmpty()) {
            return "redirect:/panier";
        }
        DeliveryForm form = saved;
        if (form == null) {
            User account = accountService.getUser(user.getUsername());
            form = new DeliveryForm();
            form.setFirstName(account.getFirstName());
            form.setLastName(account.getLastName());
            form.setEmail(account.getEmail());
            if (account.hasAddress()) {
                form.setPhone(account.getPhone());
                form.setAddress(account.getAddress());
                form.setAddressComplement(account.getAddressComplement());
                form.setPostalCode(account.getPostalCode());
                form.setCity(account.getCity());
                form.setCountry(account.getCountry());
            }
        }
        model.addAttribute("deliveryForm", form);
        addCartSummary(model, cart, form.getDeliveryMode());
        return "checkout-delivery";
    }

    @PostMapping("/livraison")
    public String saveDelivery(Model model,
                               @ModelAttribute(Constants.CART) Cart cart,
                               @Valid @ModelAttribute("deliveryForm") DeliveryForm form,
                               BindingResult errors) {
        if (cart.isEmpty()) {
            return "redirect:/panier";
        }
        if (form.getDeliveryMode() == DeliveryMode.HOME) {
            for (String field : new String[]{"address", "postalCode", "city"}) {
                if (!errors.hasFieldErrors(field) && !StringUtils.hasText((String) errors.getFieldValue(field))) {
                    errors.rejectValue(field, "NotBlank");
                }
            }
            if (!ValidationRules.COUNTRIES.contains(form.getCountry())) {
                errors.rejectValue("country", "NotBlank");
            }
        }
        if (errors.hasErrors()) {
            addCartSummary(model, cart, form.getDeliveryMode() == null ? DeliveryMode.HOME : form.getDeliveryMode());
            return "checkout-delivery";
        }
        model.addAttribute(Constants.DELIVERY, form);
        return "redirect:/commande/recapitulatif";
    }

    @GetMapping("/recapitulatif")
    public String review(Model model,
                         @ModelAttribute(Constants.CART) Cart cart,
                         @SessionAttribute(name = Constants.DELIVERY, required = false) DeliveryForm delivery) {
        if (cart.isEmpty()) {
            return "redirect:/panier";
        }
        if (delivery == null) {
            return "redirect:/commande/livraison";
        }
        addCartSummary(model, cart, delivery.getDeliveryMode());
        return "checkout-review";
    }

    @PostMapping("/recapitulatif")
    public String confirm(Model model,
                          @ModelAttribute(Constants.CART) Cart cart,
                          @SessionAttribute(name = Constants.DELIVERY, required = false) DeliveryForm delivery,
                          @RequestParam(defaultValue = "false") boolean acceptTerms,
                          @AuthenticationPrincipal User user,
                          SessionStatus sessionStatus) {
        if (cart.isEmpty()) {
            return "redirect:/panier";
        }
        if (delivery == null) {
            return "redirect:/commande/livraison";
        }
        if (!acceptTerms) {
            model.addAttribute("termsError", true);
            addCartSummary(model, cart, delivery.getDeliveryMode());
            return "checkout-review";
        }
        Order order = orderService.placeOrder(user.getUsername(), cart, delivery);
        sessionStatus.setComplete();
        return "redirect:/commande/" + order.getId() + "/paiement";
    }

    @GetMapping("/{orderId}/paiement")
    public String payment(Model model, @PathVariable Long orderId, @AuthenticationPrincipal User user) {
        Order order = findOrder(orderId, user);
        if (order.getStatus().isPaid()) {
            return "redirect:/commande/confirmation/" + orderId;
        }
        model.addAttribute("title", "Hecathlon");
        model.addAttribute("order", order);
        return "checkout-payment";
    }

    @PostMapping("/{orderId}/paiement")
    public String pay(@PathVariable Long orderId, @AuthenticationPrincipal User user) {
        Order order = findOrder(orderId, user);
        if (order.getStatus().isPaid()) {
            return "redirect:/commande/confirmation/" + orderId;
        }
        String base = ServletUriComponentsBuilder.fromCurrentContextPath().path("/commande/" + orderId + "/paiement").toUriString();
        try {
            return "redirect:" + orderService.startPayment(order, base + "/retour", base + "/annulation");
        } catch (PaymentException e) {
            return "redirect:/commande/" + orderId + "/paiement?indisponible";
        }
    }

    @GetMapping("/{orderId}/paiement/retour")
    public String paymentReturn(@PathVariable Long orderId,
                                @RequestParam(name = "token", required = false) String paymentId,
                                @AuthenticationPrincipal User user) {
        Order order = findOrder(orderId, user);
        try {
            if (orderService.completePayment(order, paymentId)) {
                return "redirect:/commande/confirmation/" + orderId;
            }
        } catch (PaymentException e) {
            return "redirect:/commande/" + orderId + "/paiement?echec";
        }
        return "redirect:/commande/" + orderId + "/paiement?echec";
    }

    @GetMapping("/{orderId}/paiement/annulation")
    public String paymentCancelled(@PathVariable Long orderId) {
        return "redirect:/commande/" + orderId + "/paiement?annule";
    }

    @GetMapping("/confirmation/{orderId}")
    public String confirmation(Model model, @PathVariable Long orderId, @AuthenticationPrincipal User user) {
        Order order = findOrder(orderId, user);
        if (!order.getStatus().isPaid()) {
            return "redirect:/commande/" + orderId + "/paiement";
        }
        model.addAttribute("title", "Hecathlon");
        model.addAttribute("order", order);
        return "checkout-confirmation";
    }

    private Order findOrder(Long orderId, User user) {
        return orderService.findForCustomer(orderId, user.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private void addCartSummary(Model model, Cart cart, DeliveryMode mode) {
        BigDecimal deliveryCost = orderService.deliveryCost(cart, mode);
        BigDecimal total = cart.getSubtotal().add(deliveryCost);
        model.addAttribute("title", "Hecathlon");
        model.addAttribute("homeDeliveryCost", cart.getDeliveryCost());
        model.addAttribute("deliveryCost", deliveryCost);
        model.addAttribute("total", total);
        model.addAttribute("vat", Cart.vatIncluded(total));
        model.addAttribute("countries", ValidationRules.COUNTRIES);
    }
}
