package com.gmail.tomasatrat.backend.microservices.tasks.services;

import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
import com.gmail.tomasatrat.backend.data.entity.Task;
import com.gmail.tomasatrat.backend.data.entity.User;
import com.gmail.tomasatrat.backend.microservices.orders.components.OrderClient;
import com.gmail.tomasatrat.backend.microservices.tasks.components.TaskClient;
import com.gmail.tomasatrat.backend.repositories.OrderInfoRepository;
import com.gmail.tomasatrat.backend.repositories.TaskRepository;
import com.gmail.tomasatrat.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService implements ICrudService {

    private final TaskClient taskClient;
    private TaskRepository taskRepository;

    //private UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        //this.userRepository = userRepository;
        taskClient = new TaskClient();
    }

    @Override
    public List<Task> findAll() {
        return this.taskRepository.findAll();
    }

    @Override
    public void addItem(IDataEntity item) {
        this.taskRepository.save((Task) item);
    }

    @Override
    public Optional<Task> findByID(Long id) {
        return this.taskRepository.findById(id);
    }

    @Override
    public void delete(IDataEntity item) {
        this.taskRepository.delete((Task) item);
    }

    /*public List<String> findAllUsers(){
        List<User> usuarios = this.userRepository.findAll();
        List<String> usuariosId = new ArrayList<>();
        for (int i = 0; i < usuarios.size(); i++) {
            usuariosId.add(usuarios.get(i).getId().toString());
        }
        return usuariosId;
    }*/

}
