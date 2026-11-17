package org.gepron1x.peak.web;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.gepron1x.peak.service.AscentService;
import org.gepron1x.peak.service.BusinessException;
import org.gepron1x.peak.service.ClimberService;
import org.gepron1x.peak.service.MountainService;
import org.gepron1x.peak.web.form.AscentForm;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ascents")
@RequiredArgsConstructor
public class AscentController {

    private final AscentService ascentService;
    private final MountainService mountainService;
    private final ClimberService climberService;

    @GetMapping
    public String list(Model model) {
        populateList(model);
        return "ascents/list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") AscentForm form,
                         BindingResult binding,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!binding.hasErrors()) {
            try {
                ascentService.create(form.getGroupName(), form.getMountainId(), form.getStartTime());
                redirectAttributes.addFlashAttribute("success",
                        "Восхождение группы «" + form.getGroupName().trim() + "» создано");
                return "redirect:/ascents";
            } catch (BusinessException ex) {
                binding.reject("business", ex.getMessage());
            }
        }
        populateList(model);
        return "ascents/list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model,
                          RedirectAttributes redirectAttributes) {
        return renderDetails(id, model, redirectAttributes);
    }

    @PostMapping("/{id}/participants")
    public String addParticipant(@PathVariable Long id,
                                 @RequestParam(required = false) Long climberId,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (climberId == null) {
            model.addAttribute("participantError", "Выберите альпиниста");
        } else {
            try {
                ascentService.addParticipant(id, climberId);
                redirectAttributes.addFlashAttribute("success",
                        "Альпинист добавлен в состав группы");
                return "redirect:/ascents/" + id;
            } catch (BusinessException ex) {
                model.addAttribute("participantError", ex.getMessage());
            }
        }
        return renderDetails(id, model, redirectAttributes);
    }

    @PostMapping("/{id}/finish")
    public String finish(@PathVariable Long id,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime endTime,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        try {
            ascentService.finish(id, endTime);
            redirectAttributes.addFlashAttribute("success", "Восхождение завершено");
            return "redirect:/ascents/" + id;
        } catch (BusinessException ex) {
            model.addAttribute("finishError", ex.getMessage());
            model.addAttribute("endTimeValue", endTime);
        }
        return renderDetails(id, model, redirectAttributes);
    }

    private String renderDetails(Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("ascent", ascentService.getDetails(id));
            model.addAttribute("climbers", climberService.findAll());
            return "ascents/detail";
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/ascents";
        }
    }

    private void populateList(Model model) {
        model.addAttribute("ascents", ascentService.findAll());
        model.addAttribute("mountains", mountainService.findAll());
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new AscentForm());
        }
    }
}
