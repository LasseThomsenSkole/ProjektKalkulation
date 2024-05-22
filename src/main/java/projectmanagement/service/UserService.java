package projectmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projectmanagement.model.Project;
import projectmanagement.model.User;
import projectmanagement.repository.UserRepository;

import java.util.List;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean login(String name, String password){
        User user = userRepository.getUserFromName(name);
        if (user != null){
            return user.getPassword().equals(password);
        }
        return false;
    }

    public boolean userAlreadyExists(String username){
        return userRepository.userAlreadyExists(username);
    }

    public User getUserFromName(String name){
        return userRepository.getUserFromName(name);
    }


    public void insertUser(String username, String password){
        userRepository.insertUser(username, password);
    }

}
