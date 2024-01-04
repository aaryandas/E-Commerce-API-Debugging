package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {

    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao){
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    @GetMapping()
    @PreAuthorize("permitAll()")
    public Profile getById(Principal principal){
        try {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            int userId = user.getId();

            var profile = profileDao.getByUserId(userId);

            if(profile == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            else{
                return profile;
            }
        } catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... Let's try this again.");
        }
    }

    @PutMapping()
    @PreAuthorize("permitAll()")
    public Profile updateProfile(@RequestBody Profile profile, Principal principal){
        try{
            String username = principal.getName();

            User user = userDao.getByUserName(username);
            int userId = user.getId();

            profileDao.update(userId, profile);

            return profileDao.getByUserId(userId);

        }catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... Let's try again.");
        }
    }

}
