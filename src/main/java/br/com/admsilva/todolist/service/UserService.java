package br.com.admsilva.todolist.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.favre.lib.crypto.bcrypt.BCrypt;

import br.com.admsilva.todolist.model.UserModel;
import br.com.admsilva.todolist.repository.IUserRepository;
import br.com.admsilva.todolist.utils.Utils;

@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    public List<UserModel> getAllUsers() {
        return this.userRepository.findAll();
    }

    public UserModel getUserById(UUID id) throws Exception {
        return this.getUserModelById(id);
    }

    public void saveUser(UserModel userModel) throws Exception {
        this.checkUserNameExists(userModel.getUsername());
        var passwordHashred = this.hashredPassword(userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHashred);
        this.userRepository.save(userModel);
    }

    public UserModel changeUser(UserModel changedUserModel, UUID id) throws Exception {
        var user = this.getUserModelById(id);
        Utils.copyNonNullProperties(changedUserModel, user);
        var passwordHashred = this.hashredPassword(user.getPassword().toCharArray());
        user.setPassword(passwordHashred);
        return this.userRepository.save(user);
    }

    public void destroyUser(UUID id) throws Exception {
        this.getUserModelById(id);
        this.userRepository.deleteById(id);
    }

    private UserModel getUserModelById(UUID id) throws Exception {
        var user = this.userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new Exception("Usuario nao existe.");
        }
        return user;
    }

    private UserModel checkUserNameExists(String userName) throws Exception {
        var user = this.userRepository.findByUsername(userName);
        if (user != null) {
            throw new Exception("Usuario ja existe.");
        }
        return user;
    }

    private String hashredPassword(char[] password) {
        return BCrypt.withDefaults().hashToString(12, password);
    }
}
