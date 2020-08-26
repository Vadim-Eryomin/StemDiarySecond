package ru.coistem.StemDiary.Controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.io.FileWriter

@Controller
class AndroidController {
    @PostMapping("/androidlogin")
    fun androidLogin(@RequestBody data: String){
        val writer = FileWriter("log.txt")
        writer.append("data: $data\n")
        writer.flush()
    }
}