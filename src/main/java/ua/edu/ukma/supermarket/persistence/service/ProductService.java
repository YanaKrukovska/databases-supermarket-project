package ua.edu.ukma.supermarket.persistence.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public class ProductService {

    private final Connection connection;

    @Autowired
    public ProductService(Connection connection) {
        this.connection = connection;
    }

}
