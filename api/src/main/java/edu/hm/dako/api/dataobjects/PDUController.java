package edu.hm.dako.api.dataobjects;

import edu.hm.dako.api.dataobjects.objects.PDU;
import edu.hm.dako.api.dataobjects.repositories.PDURepository;
import edu.hm.dako.api.errorhandling.exceptions.DataNotFoundException;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * controller for the PDU entity that defines endpoints
 *
 * @author Linus Englert
 */
@RestController
@Configuration
@ComponentScan(basePackages = {"edu.hm.dako.api"})
@Api(tags="PDU")
public class PDUController {
    private final PDURepository repository;
    private final PDUModelAssembler assembler;
    private final String PATH = "/pdus";

    public PDUController(PDURepository repository, PDUModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    //select all
    @GetMapping(PATH)
    CollectionModel<EntityModel<PDU>> selectAll() {
        List<EntityModel<PDU>> list = repository.findAll().stream().map(assembler::toModel).toList();
        return assembler.toCollection(list);
    }

    //select
    @GetMapping(PATH + "/{id}")
    EntityModel<PDU> select(@PathVariable Long id) {
        PDU value = repository.findById(id).orElseThrow(() ->
                new DataNotFoundException(PDU.class, id));
        return assembler.toModel(value);
    }

    //insert
    @PostMapping(PATH)
    ResponseEntity<?> insert(@RequestBody PDU value) {
        EntityModel<PDU> entityModel = assembler.toModel(repository.save(value));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    //update
    @PutMapping(PATH + "/{id}")
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody PDU value) {
        PDU updated =  repository.findById(id).map(v -> {
            v.setPduType(value.getPduType());
            v.setUsername(value.getUsername());
            v.setClientThreadName(value.getClientThreadName());
            v.setServerThreadName(value.getServerThreadName());
            v.setAuditTime(value.getAuditTime());
            v.setMessage(value.getMessage());
            return repository.save(value);
        }).orElseGet(() -> {
            value.setId(id);
            return repository.save(value);
        });

        EntityModel<PDU> entityModel = assembler.toModel(updated);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    //delete
    @DeleteMapping(PATH + "/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}