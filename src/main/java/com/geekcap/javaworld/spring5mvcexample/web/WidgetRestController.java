package com.geekcap.javaworld.spring5mvcexample.web;

import com.geekcap.javaworld.spring5mvcexample.model.Widget;
import com.geekcap.javaworld.spring5mvcexample.repository.WidgetRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
public class WidgetRestController {
    private static final Logger logger = LogManager.getLogger(WidgetRestController.class);
    @Autowired
    private WidgetRepository widgetRepository;

    @GetMapping("/rest/widget/{id}")
    public ResponseEntity<Widget> getWidget(@PathVariable Long id) {
        return widgetRepository.findById(id)
                .map(widget -> ResponseEntity.ok().body(widget))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rest/widgets")
    //public ResponseEntity<Iterable<Widget>> getWidgets() {
    public Iterable<Widget> getWidgets() {
        return widgetRepository.findAll();
    }

    @PostMapping("/rest/widget")
    public ResponseEntity<Widget> createWidget(@RequestBody Widget widget) {
        logger.info("Received widget: name: " + widget.getName() + ", description: " + widget.getDescription());
        Widget newWidget = widgetRepository.save(widget);
        try {
            return ResponseEntity.created(new URI("/rest/widget/" + newWidget.getId())).body(newWidget);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/rest/widget/{id}")
    public ResponseEntity<Widget> updateWidget(@RequestBody Widget widget, @PathVariable Long id) {
        widget.setId(id);
        return ResponseEntity.ok().body(widgetRepository.save(widget));
    }

    @PutMapping("/rest/proper/widget/{id}")
    public ResponseEntity<Widget> updateWidgetProper(@RequestBody Widget widget, @PathVariable Long id, @RequestHeader("If-Match") Long ifMatch) {
        Optional<Widget> existingWidget = widgetRepository.findById(id);
        if (existingWidget.isPresent()) {
            if (ifMatch == 7) {
                widget.setId(id);
                return ResponseEntity.ok().body(widgetRepository.save(widget));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/rest/widget/{id}")
    public ResponseEntity deleteWidget(@PathVariable Long id) {
        widgetRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
