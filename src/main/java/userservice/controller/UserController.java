package userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.assembler.UserModelAssembler;
import userservice.dto.UserRequestDto;
import userservice.dto.UserResponseDto;
import userservice.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserModelAssembler assembler;

    @PostMapping("/users")
    public ResponseEntity<EntityModel<UserResponseDto>> create(@Valid @RequestBody UserRequestDto dto) {
        UserResponseDto created = service.create(dto);
        URI location = URI.create("/users/" + created.id());
        EntityModel<UserResponseDto> resource = assembler.toModel(created);
        return ResponseEntity.created(location).body(resource);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> get(@PathVariable Long id) {
        UserResponseDto user = service.getById(id);
        return ResponseEntity.ok(assembler.toModel(user));
    }

    @GetMapping("/users")
    public CollectionModel<EntityModel<UserResponseDto>> getAll() {
        List<EntityModel<UserResponseDto>> users = service.getAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(users);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto dto) {
        UserResponseDto updated = service.update(id, dto);
        return ResponseEntity.ok(assembler.toModel(updated));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}