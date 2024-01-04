package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.models.Profile;

import javax.validation.Valid;

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {

    private ProfileDao profileDao;

    @Autowired
    public ProfileController(ProfileDao profileDao){
        this.profileDao = profileDao;
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Profile getById(@PathVariable int id){
        try {
            var profile = profileDao.getByUserId(id);

            if(profile == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            else{
                return profile;
            }
        } catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... Let's try this again.");
        }
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody Profile profile,@PathVariable int id){
        try{
            this.profileDao.update(id,profile);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... Let's try again.");

        }
    }

}
