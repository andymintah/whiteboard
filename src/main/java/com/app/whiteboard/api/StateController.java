package com.app.whiteboard.api;

import com.app.whiteboard.crdt.StrokeCRDT;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/com/app/whiteboard")
public class StateController {
    private final StrokeCRDT crdt;


    public StateController(StrokeCRDT crdt) {
        this.crdt = crdt;
    }


    @GetMapping("/state")
    public Object getState() {
        return crdt.getAllStrokes();
    }
}
