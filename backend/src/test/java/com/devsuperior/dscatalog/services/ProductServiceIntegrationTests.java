package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTests {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1_000L;
        countTotalProducts = 25L;
    }

    @Test
    public void delete_Should_DeleteResource_When_IdExists() {
        service.delete(existingId);

        Assertions.assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    public void delete_Should_ThrowResourceNotFoundException_When_IdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    public void findAllPaged_Should_ReturnPage_When_Page0Size10() {
//        ARRANGE
        PageRequest pageRequest = PageRequest.of(0, 10);

//        ACT
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

//        ASSERT
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(countTotalProducts, result.getTotalElements());
    }

    @Test
    public void findAllPaged_Should_ReturnAnEmptyPage_When_PageDoesNotExist() {
//        ARRANGE
        PageRequest pageRequest = PageRequest.of(50, 10);

//        ACT
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

//        ASSERT
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPaged_Should_ReturnASortedPage_When_SortedByName() {
//        ARRANGE
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

//        ACT
        Page<ProductDTO> result = service.findAllPaged(pageRequest);

//        ASSERT
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }

}
