package cluser.crm.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import cluser.crm.models.User;

@RestController
@CrossOrigin
public class UserController {
    @GetMapping(value = "/print")
    public String exportAdminWorkTrackerCSV() {

        return "ok";
    }
}