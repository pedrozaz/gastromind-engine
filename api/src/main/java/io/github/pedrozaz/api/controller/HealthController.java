package io.github.pedrozaz.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final DataSource dataSource;

    @Autowired
    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public String checkHealth() {
        try (Connection conn = dataSource.getConnection()) {
            return "Status: UP | Database: Connected to " + conn.getCatalog();
        } catch (SQLException e) {
            return "Status: DOWN | Error: " + e.getMessage();
        }
    }
}
