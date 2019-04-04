package com.bolsadeideas.springboot.backend.apirest.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.services.IClienteService;


@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteService.findAll();
	}
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page){
		//Pageable pageable = PageRequest.of(page, 4);
		return clienteService.findAll(PageRequest.of(page, 4));
	}
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		try {
			cliente = clienteService.findById(id);	
			return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
		}catch(DataAccessException e) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}
	
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
		if(result.hasErrors()) {
//			List<String> error = new ArrayList<>();
//			for(FieldError err: result.getFieldErrors()) {
//				error.add("El campo '"+err.getField()+"'"+err.getDefaultMessage());
//			}
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> {return "El campo '"+err.getField()+"' "+err.getDefaultMessage();})
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		try {
			clienteNew = clienteService.save(cliente);
			response.put("success", "El cliente ha sido creado con éxito!");
			response.put("cliente", clienteNew);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert en la base de datos. ");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result,@PathVariable Long id) {
		Cliente clienteActual = clienteService.findById(id);
		Cliente clienteUpdate = null;
		Map<String,Object> response = new HashMap<>();
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> {return "El campo '"+err.getField()+"' "+err.getDefaultMessage();})
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}
		try {
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setMail(cliente.getMail());
			clienteActual.setCreateAt(new Date());
			clienteUpdate = clienteService.save(clienteActual);
			response.put("mensaje", "El cliente ha sido actualizado con éxito!");
			response.put("cliente", clienteUpdate);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al actualizar el insert en la base de datos. ");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			clienteService.delete(id);
			response.put("success", "El cliente ha sido eliminado con éxito!");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al eliminar el cliente de la base de datos. ");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
