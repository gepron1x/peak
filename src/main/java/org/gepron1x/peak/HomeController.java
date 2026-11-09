package org.gepron1x.peak;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "<html>" +
                "<head><style>body{background-color:white;text-align:center;padding-top:100px;font-family:sans-serif;}</style></head>" +
                "<body>" +
                "<h1>Приложение альпинистского клуба</h1>" +
                "<p>Пронюк Георгий 241-326</p>" +
                "<p>Карсаев Айс 241-326</p>" +
                "</body>" +
                "</html>";
    }
}
