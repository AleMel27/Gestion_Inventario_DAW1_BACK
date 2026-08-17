package com.gestionInventario.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestionInventario.dtos.response.RolDTO;
import com.gestionInventario.services.RolService;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<List<RolDTO>> listarTodos() {
        List<RolDTO> roles = rolService.listarTodos();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
}