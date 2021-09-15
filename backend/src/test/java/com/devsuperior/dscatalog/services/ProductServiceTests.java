package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO productDTO;
    private Category category;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1_000L;
        dependentId = 4L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        category = Factory.createCategory();
        productDTO = Factory.createProductDTO();

//        UNIT TESTS
        // FIND ALL
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        // FIND BY ID
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        // SAVE
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
        // GET ONE
        Mockito.when(repository.getOne(existingId)).thenReturn(product);
        Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
        Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    @Test
    public void delete_Should_DoNothing_When_IdExists() {

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void delete_Should_ThrowResourceNotFoundException_When_IdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    public void delete_Should_ThrowDatabaseException_When_IdIsDependent() {

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
    }

    @Test
    public void findAllPaged_Should_ReturnPage() {

        // ARRANGE
        Pageable pageable = PageRequest.of(0, 10);

        // ACT
        Page<ProductDTO> result = service.findAllPaged(pageable);

        // ASSERTION
        Assertions.assertNotNull(result);

        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findById_Should_ReturnProductDTO_When_IdExists() {

//      ACT
        ProductDTO productDTO = service.findById(existingId);

//      ASSERT
        Assertions.assertNotNull(productDTO);

        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findById_Should_ThrowResourceNotFoundException_When_IdDoesNotExist() {

//        ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//          ACT
            service.findById(nonExistingId);
        });

        Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
    }

    @Test
    public void update_Should_ReturnProductDTO_When_IdExists() {

//      ACT
        ProductDTO result = service.update(existingId, productDTO);

//      ASSERT
        Assertions.assertNotNull(result);

        Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
    }

    @Test
    public void update_Should_ThrowResourceNotFoundException_When_IdDoesNotExist() {

//        ASSERT
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
//          ACT
            service.update(nonExistingId, productDTO);
        });

        Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
    }
}