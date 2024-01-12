package com.curso.ecommerce.controller;


import com.curso.ecommerce.model.Producto;
import com.curso.ecommerce.model.Usuario;
import com.curso.ecommerce.service.ProductoService;
import com.curso.ecommerce.service.UploadFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UploadFileService uploadFileService;

    @GetMapping("")
    public String show(Model model) {
        model.addAttribute("productos", productoService.findAll());
        return "productos/show";
    }

    @GetMapping("/create")
    public String create() {
        return "productos/create";
    }


    @PostMapping("/save")
    public String save(Producto producto, @RequestParam("img") MultipartFile file) throws IOException {
        LOGGER.info("Esta es el objeto producto {}", producto);
        Usuario user = new Usuario(1, "", "", "", "", "", "", "");
        producto.setUsuario(user);

        // -----> img
        // -----> Cuando se crea un producto, siempre el id del producto es nulo
        if (producto.getId() == null) {
            String filename = uploadFileService.saveImage(file);
            producto.setImagen(filename);
        }
        productoService.save(producto);
        return "redirect:/productos";
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Producto producto = new Producto();
        Optional<Producto> optionalProducto = productoService.get(id);
        producto = optionalProducto.get();
        LOGGER.info("Producto encontrado: {}", producto);
        model.addAttribute("producto", producto);
        return "productos/edit";
    }


    @PostMapping("/update")
    public String update(Producto producto, @RequestParam("img") MultipartFile file ) throws IOException {
        Producto p = new Producto();
        p = productoService.get(producto.getId()).get();
        if (file.isEmpty()) {
            //Caso cuando editamos el producto, pero no cambiamos la imagen
            producto.setImagen(p.getImagen());
        } else {
            //Elimina siempre encuando no sea la imagen default
            if ( !p.getImagen().equals("default.jpg") ) {
                uploadFileService.deleteImage(p.getImagen());
            }
            String filename = uploadFileService.saveImage(file);
            producto.setImagen(filename);
        }
        producto.setUsuario(p.getUsuario());
        productoService.update(producto);
        return "redirect:/productos";
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, Model model) {
        Producto p = new Producto();

        p = productoService.get(id).get();

        //Elimina siempre encuando no sea la imagen default
        if ( !p.getImagen().equals("default.jpg") ) {
            uploadFileService.deleteImage(p.getImagen());
        }

        productoService.delete(id);
        return "redirect:/productos";
    }




}
