package userservice.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import userservice.controller.UserController;
import userservice.dto.UserResponseDto;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserResponseDto, EntityModel<UserResponseDto>> {

    @Override
    public @NonNull EntityModel<UserResponseDto> toModel(@NonNull UserResponseDto user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).get(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll()).withRel("users"),
                linkTo(methodOn(UserController.class).update(user.id(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).delete(user.id())).withRel("delete")
        );
    }
}
