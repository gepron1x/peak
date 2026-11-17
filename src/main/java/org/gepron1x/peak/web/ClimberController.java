package org.gepron1x.peak.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gepron1x.peak.service.BusinessException;
import org.gepron1x.peak.service.ClimberService;
import org.gepron1x.peak.web.form.ClimberForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/climbers")
@RequiredArgsConstructor
public class ClimberController {

    private final ClimberService climberService;

    @GetMapping
    public String list(Model model) {
        populate(model);
        return "climbers/list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") ClimberForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!binding.hasErrors()) {
            try {
                climberService.create(form.getLastName(), form.getFirstName(), form.getMiddleName(),
                        form.getPhone(), form.getAddress());
                redirectAttributes.addFlashAttribute("success",
                        "Альпинист «" + form.getLastName().trim() + " "
                                + form.getFirstName().trim() + " "
                                + form.getMiddleName().trim() + "» добавлен");
                return "redirect:/climbers";
            } catch (BusinessException ex) {
                binding.reject("business", ex.getMessage());
            }
        }
        populate(model);
        return "climbers/list";
    }

    private void populate(Model model) {
        model.addAttribute("climbers", climberService.findAll());
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new ClimberForm());
        }
    }
}
