package com.gmail.tomasatrat.backend.microservices.tasks.services;

import com.gmail.tomasatrat.app.security.SecurityUtils;
import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.common.enums.PriorityEnum;
import com.gmail.tomasatrat.backend.data.entity.Task;
import com.gmail.tomasatrat.backend.data.entity.User;
import com.gmail.tomasatrat.backend.data.entity.VTasksByUser;
import com.gmail.tomasatrat.backend.microservices.tasks.components.TaskClient;
import com.gmail.tomasatrat.backend.repositories.TaskRepository;
import com.gmail.tomasatrat.backend.repositories.UserRepository;
import com.gmail.tomasatrat.backend.repositories.VTasksByUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService implements ICrudService {

    private final TaskClient taskClient;
    private TaskRepository taskRepository;
    private UserRepository userRepository;
    private VTasksByUserRepository vTasksByUserRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, VTasksByUserRepository vTasksByUserRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.vTasksByUserRepository = vTasksByUserRepository;
        taskClient = new TaskClient();
    }

    @Override
    public List<Task> findAll() {
        String username = SecurityUtils.getUsername();
        User user = this.userRepository.findByUsername(username);
        List<Task> allTask = this.taskRepository.findAll();
        if (user.getRole().equals("admin")) {
            allTask.sort(Comparator.comparing(o -> PriorityEnum.getPriorityByStringValue(o.getPriority())));
            return allTask;
        } else {
            List<Task> allTaskByUserId = new ArrayList<>();
            for (Task task : allTask) {
                if (task.getUserId().getId().equals(user.getId())) {
                    allTaskByUserId.add(task);
                }
            }
            allTaskByUserId.sort(Comparator.comparing(o -> PriorityEnum.getPriorityByStringValue(o.getPriority())));
            return allTaskByUserId;
        }
    }

    @Override
    public IDataEntity addItem(IDataEntity item) {
        Task task = (Task) item;

        if (task.getUserId() == null) {
            VTasksByUser view = vTasksByUserRepository.findAutoUser();
            task.setUserId(userRepository.findByUsername(view.getUsername()));
        }

        return this.taskRepository.save(task);
    }

    @Override
    public Optional<Task> findByID(Long id) {
        return this.taskRepository.findById(id);
    }

    @Override
    public void delete(IDataEntity item) {
        this.taskRepository.delete((Task) item);
    }

}
