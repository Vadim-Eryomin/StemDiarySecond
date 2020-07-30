package ru.coistem.StemDiary.Controllers;

import org.springframework.web.bind.annotation.*;
import ru.coistem.StemDiary.Database;
import ru.coistem.StemDiary.JSONer;

import java.sql.ResultSet;
import java.sql.SQLException;

import static ru.coistem.StemDiary.Controllers.WebSocketController.getFromJSONString;

@RestController
public class FetchPageController {
    @GetMapping("/profile")
    public String profile() throws SQLException {
        ResultSet set = Database.query("SELECT * FROM login");
        set.next();
        return JSONer.toJSON(set, new String[]{"login", "password"});
    }

    @PostMapping("/auth")
    public boolean auth(@RequestBody String data) throws SQLException {
        String login = getFromJSONString(data, "login");
        String password = getFromJSONString(data, "password");
        ResultSet set = Database.query("SELECT * FROM login WHERE login = '" + login + "' and password = '" + password + "'");
        return set.next();
    }
}
