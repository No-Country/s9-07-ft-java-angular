package com.nocountry.ecommerce.service.implementation;

import com.nocountry.ecommerce.dto.ProductDto;
import com.nocountry.ecommerce.model.Category;
import com.nocountry.ecommerce.model.Product;
import com.nocountry.ecommerce.repository.CategoryRepository;
import com.nocountry.ecommerce.repository.ProductRepository;

import com.nocountry.ecommerce.util.enums.ProductState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProduct() {
        // Arrange
        ProductDto productDto = new ProductDto();
        productDto.setName("Product");
        productDto.setDescription("Product description");
        productDto.setImage("imagen.jpg");
        productDto.setPrice(1000.0);
        productDto.setWeight(100.0);
        productDto.setCountry("US");
        productDto.setMinStock(10);
        productDto.setCategory("Category");

        Category category = new Category();
        category.setName("Category");
        category.setId("u1");
        category.setDescription("Category description");
        category.setNumber("1");
        category.setState(true);

        Product product = new Product();
        String id = UUID.randomUUID().toString();
        product.setId(id);
        product.setName("Product");
        product.setDescription("Product description");
        product.setStock(0);
        product.setImage("imagen.jpg");
        product.setPrice(1000.0);
        product.setState(ProductState.U);
        product.setWeight(100.0);
        product.setCountry("US");
        product.setMinStock(10);
        product.setCategory(category);

        when(categoryRepository.getByName("Category")).thenReturn(category);

        // Act
        Product productSaved = productServiceImpl.createProduct(productDto);
        productSaved.setId(id);
        // Assert
        verify(productRepository, times(1)).save(product);
        assertEquals(productSaved,product);
    }
}