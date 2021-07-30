package com.example.gistcompetitioncnserver.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gistps/api/v1/user")
public class UserController {

    @Autowired
    private UserDaoService userDaoService;

    @GetMapping("")
    public List<User> retrieveAllUsers(){
        return userDaoService.retrieveAllUsers();
    }

    @GetMapping("/{id}")
    public EntityModel<User> retrieveUser(@PathVariable long id){

        Optional<User> user = userDaoService.findUserById(id);

        if(!user.isPresent()){
            throw new UsernameNotFoundException(String.format("ID[%s] not found", id));
        }

        // use HATEOS
        EntityModel<User> resource = new EntityModel<>(user.get());
        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        resource.add(linkTo.withRel("all-users"));

        return resource;

    }

    @PostMapping("")
    public ResponseEntity<User> createUser(@RequestBody User user){

        User savedUser = userDaoService.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userDaoService.deleteById(id);
    }

}
