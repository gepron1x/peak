package org.gepron1x.peak.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gepron1x.peak.domain.Mountain;
import org.gepron1x.peak.service.AscentService;
import org.gepron1x.peak.service.BusinessException;
import org.gepron1x.peak.service.MountainService;
import org.gepron1x.peak.web.form.MountainForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mountains")
@RequiredArgsConstructor
public class MountainController {

    private final MountainService mountainService;
    private final AscentService ascentService;

    @GetMapping
    public String list(Model model) {
        populateList(model);
        return "mountains/list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") MountainForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!binding.hasErrors()) {
            try {
                mountainService.create(form.getName(), form.getHeight(),
                        form.getCountry(), form.getRegion());
                redirectAttributes.addFlashAttribute("success",
                        "Вершина «" + form.getName().trim() + "» добавлена");
                return "redirect:/mountains";
            } catch (BusinessException ex) {
                binding.reject("business", ex.getMessage());
            }
        }
        populateList(model);
        return "mountains/list";
    }

    @GetMapping("/{id}")
    public String ascents(@PathVariable Long id, Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("data", ascentService.getMountainAscents(id));
            return "mountains/ascents";
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/mountains";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            Mountain mountain = mountainService.get(id);
            model.addAttribute("form", MountainForm.from(mountain));
            model.addAttribute("mountainId", id);
            model.addAttribute("hasAscents", mountainService.hasAscents(id));
            return "mountains/edit";
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/mountains";
        }
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") MountainForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!binding.hasErrors()) {
            try {
                mountainService.update(id, form.getName(), form.getHeight(),
                        form.getCountry(), form.getRegion());
                redirectAttributes.addFlashAttribute("success", "Данные вершины обновлены");
                return "redirect:/mountains";
            } catch (BusinessException ex) {
                binding.reject("business", ex.getMessage());
            }
        }
        try {
            mountainService.get(id);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", "Гора не найдена");
            return "redirect:/mountains";
        }
        model.addAttribute("mountainId", id);
        model.addAttribute("hasAscents", mountainService.hasAscents(id));
        return "mountains/edit";
    }

    private void populateList(Model model) {
        model.addAttribute("mountains", mountainService.findAll());
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new MountainForm());
        }
    }
}
