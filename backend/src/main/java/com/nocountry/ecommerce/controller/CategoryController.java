package com.nocountry.ecommerce.controller;

import com.nocountry.ecommerce.dto.Mensaje;
import com.nocountry.ecommerce.model.Category;
import com.nocountry.ecommerce.service.CategoryService;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<List<Category>> list() {
        List<Category> list = categoryService.list();
        return new ResponseEntity(list, HttpStatus.OK);
    }
    @SecurityRequirement(name = "jwt")
    @Operation(summary = "Add a new Category",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Category created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Category already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error"))),
            })
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Category categoriaEnt) {


        if (StringUtils.isBlank(categoriaEnt.getName()))
            return new ResponseEntity(new Mensaje("the name can't be empty"), HttpStatus.BAD_REQUEST);
        if (StringUtils.isBlank(categoriaEnt.getDescription()))
            return new ResponseEntity(new Mensaje("the description can't be empty"), HttpStatus.BAD_REQUEST);
        if (categoriaEnt.getState() == null )
            return new ResponseEntity<>("State can't be empty", HttpStatus.BAD_REQUEST);
        else{
            Category categoria = new Category( categoriaEnt.getName(),categoriaEnt.getDescription(),categoriaEnt.getNumber(),categoriaEnt.getState());
            categoryService.save(categoria);
            return new ResponseEntity(new Mensaje("category created successfully"), HttpStatus.OK);
        }


    }
    @SecurityRequirement(name = "jwt")
    @PutMapping("/updatebyname/{name}")
    public ResponseEntity<?> updateByName(@PathVariable("name") String name, @RequestBody Category categoriaEnt) {
        if (categoryService.findByName(name)==null)
            return new ResponseEntity(new Mensaje(" category does not exist"), HttpStatus.NOT_FOUND);


        Category category = categoryService.findByName(name);
        category.setName(categoriaEnt.getName());
        category.setDescription(categoriaEnt.getDescription());
        category.setNumber(categoriaEnt.getNumber());
        category.setState(categoriaEnt.getState());
        categoryService.save(category);
        return new ResponseEntity(new Mensaje("category updated successfully"), HttpStatus.OK);
    }

    @SecurityRequirement(name = "jwt")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody Category categoriaEnt) {
        if (!categoryService.existsById(id))
            return new ResponseEntity(new Mensaje(" category does not exist"), HttpStatus.NOT_FOUND);


        Category category = categoryService.getOne(id).get();
        category.setName(categoriaEnt.getName());
        category.setDescription(categoriaEnt.getDescription());
        category.setNumber(categoriaEnt.getNumber());
        category.setState(categoriaEnt.getState());
        categoryService.save(category);
        return new ResponseEntity(new Mensaje("category updated successfully"), HttpStatus.OK);
    }
    @SecurityRequirement(name = "jwt")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        if (!categoryService.existsById(id))
            return new ResponseEntity(new Mensaje("category does not exist"), HttpStatus.NOT_FOUND);
        categoryService.delete(id);
        return new ResponseEntity(new Mensaje("category deleted successfully"), HttpStatus.OK);
    }

    @SecurityRequirement(name = "jwt")
    @DeleteMapping("/deletebyname/{name}")
    public ResponseEntity<?> deleteByName(@PathVariable("name") String name) {
        if (categoryService.findByName(name)==null)
            return new ResponseEntity(new Mensaje("category does not exist"), HttpStatus.NOT_FOUND);
        String id=categoryService.findByName(name).getId();
        categoryService.delete(id);
        return new ResponseEntity(new Mensaje("category deleted successfully"), HttpStatus.OK);
    }

    @Operation(summary = "Get all categories", responses = {
            @ApiResponse(responseCode = "200", description = "Categories list",
                    content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
            @ApiResponse(responseCode = "404", description = "Categories not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @GetMapping("/detail/{name}")
    public ResponseEntity<Category> getById(@PathVariable("name") String name) {
        if (categoryService.findByName(name)==null)
            return new ResponseEntity(new Mensaje("category does not exist"), HttpStatus.NOT_FOUND);
        Category category = categoryService.findByName(name);
        return new ResponseEntity(category, HttpStatus.OK);
    }
}
