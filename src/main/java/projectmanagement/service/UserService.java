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

    public boolean userAlreadyExists(String username){
        return userRepository.userAlreadyExists(username);
    }

    public User getUserFromName(String name){
        return userRepository.getUserFromName(name);
    }

    public int getIdFromUser(String name, String password){
        return userRepository.getIdFromUser(name, password);
    }

    public void insertUser(String username, String password){
        userRepository.insertUser(username, password);
    }

    public void assignUserToProject(int userId, int projectId){
        userRepository.assignUserToProject(userId, projectId);
    }
}
