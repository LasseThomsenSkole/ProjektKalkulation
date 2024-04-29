package controller;

import Service.Service;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller("/")
public class Controller {
    private Service service;

    @GetMapping("")
    public String index(){
        return "index";
    }
}
