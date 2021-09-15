package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    private long existingID;
    private long nonExistingID;
    private long countTotalProducts;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        existingID = 1L;
        nonExistingID = 1_000L;
        countTotalProducts = 25L;
    }

    @Test
    public void save_Should_PersistObjectWithAutoincrement_When_IdIsNull() {
//        ARRANGE
        Product product = Factory.createProduct();
        product.setId(null);

//        ACT
        product = repository.save(product);

//        ASSERT
        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void findById_Should_ReturnNonEmptyOptionalObject_When_IdExists() {
//        ACT
        Optional<Product> result = repository.findById(existingID);
//        ASSERT
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void findById_Should_ReturnEmptyOptionalObject_When_IdDoesNotExist() {
//        ACT
        Optional<Product> result = repository.findById(nonExistingID);
//        ASSERT
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void delete_Should_DeleteObject_When_IdExists() {
//        ARRANGE
//        long existingID = 1L;

//        ACT
        repository.deleteById(existingID);
        Optional<Product> result = repository.findById(existingID);

//        ASSERT
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void delete_Should_ThrowEmptyResultDataAccessException_When_IdDoesNotExist() {
//        ASSERT
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
//        ARRANGE
//            long nonExistingID = 1_000L;

//        ACT
            repository.deleteById(nonExistingID);
        });

    }

}
