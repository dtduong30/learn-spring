package com.codewithmosh.store.controllers;

import ch.qos.logback.core.model.Model;
import com.codewithmosh.store.entities.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    @RequestMapping("/hello")
    public Message sayHello(Model model) {
        return new Message("hello");
    }
}
