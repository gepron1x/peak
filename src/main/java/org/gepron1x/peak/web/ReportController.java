package org.gepron1x.peak.web;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.gepron1x.peak.service.BusinessException;
import org.gepron1x.peak.service.ClimberService;
import org.gepron1x.peak.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ClimberService climberService;

    @GetMapping
    public String reports(
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long climberId,
            Model model) {
        model.addAttribute("climbers", climberService.findAll());
        model.addAttribute("mountainStats", reportService.mountainStats());
        model.addAttribute("from", from);
        model.addAttribute("to", to);

        if (from != null || to != null) {
            try {
                model.addAttribute("period", reportService.periodReport(from, to));
            } catch (BusinessException ex) {
                model.addAttribute("periodError", ex.getMessage());
            }
        }

        if (climberId != null) {
            try {
                model.addAttribute("climberStats", reportService.climberStats(climberId));
            } catch (BusinessException ex) {
                model.addAttribute("climberStatsError", ex.getMessage());
            }
        }
        return "reports/index";
    }
}
