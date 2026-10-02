package ue.edu.co.splitbill.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import ue.edu.co.splitbill.dto.ExpenseRequest;
import ue.edu.co.splitbill.dto.ExpenseResponse;
import ue.edu.co.splitbill.dto.MessageResponse;
import ue.edu.co.splitbill.service.ExpenseService;

// CRUD 3: gastos de un grupo
@RestController
@RequestMapping("/api")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // GET: gastos del grupo
    @GetMapping("/groups/{groupId}/expenses")
    public List<ExpenseResponse> list(@PathVariable Long groupId) {
        return this.expenseService.listByGroup(groupId);
    }

    // POST: registrar gasto en el grupo
    @PostMapping("/groups/{groupId}/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(@PathVariable Long groupId, @Valid @RequestBody ExpenseRequest request) {
        return this.expenseService.create(groupId, request);
    }

    // PUT: editar gasto
    @PutMapping("/expenses/{expenseId}")
    public ExpenseResponse update(@PathVariable Long expenseId, @Valid @RequestBody ExpenseRequest request) {
        return this.expenseService.update(expenseId, request);
    }

    // DELETE: eliminar gasto (borrado logico)
    @DeleteMapping("/expenses/{expenseId}")
    public MessageResponse delete(@PathVariable Long expenseId) {
        this.expenseService.delete(expenseId);
        return new MessageResponse("Gasto eliminado");
    }
}
