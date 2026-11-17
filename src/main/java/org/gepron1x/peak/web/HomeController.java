package org.gepron1x.peak.web;

import lombok.RequiredArgsConstructor;
import org.gepron1x.peak.repository.AscentRepository;
import org.gepron1x.peak.repository.ClimberRepository;
import org.gepron1x.peak.repository.MountainRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MountainRepository mountains;
    private final ClimberRepository climbers;
    private final AscentRepository ascents;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("mountainCount", mountains.count());
        model.addAttribute("climberCount", climbers.count());
        model.addAttribute("ascentCount", ascents.count());
        model.addAttribute("activeAscentCount", ascents.countByEndTimeIsNull());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
