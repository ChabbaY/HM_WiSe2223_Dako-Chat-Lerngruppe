package edu.hm.dako.api.dataobjects;

import edu.hm.dako.api.dataobjects.objects.PDU;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * decorates a PDU with links
 *
 * @author Linus Englert
 */
@Component
public class PDUModelAssembler implements RepresentationModelAssembler<PDU, EntityModel<PDU>> {
    @Override
    public @NotNull EntityModel<PDU> toModel(@NotNull PDU entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(PDUController.class).select(entity.getId())).withSelfRel(),
                linkTo(methodOn(PDUController.class).selectAll()).withRel("countries"));
    }

    public CollectionModel<EntityModel<PDU>> toCollection(
            List<EntityModel<PDU>> entities) {
        return CollectionModel.of(entities,
                linkTo(methodOn(PDUController.class).selectAll()).withSelfRel());
    }
}