package ru.coistem.StemDiary.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.coistem.StemDiary.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class SimpleController {
    @GetMapping("/")
    public String index() throws SQLException {
        return "index";
    }

    @GetMapping("/error")
    @PostMapping("/error")
    public String error(){
        return "error";
    }
}
