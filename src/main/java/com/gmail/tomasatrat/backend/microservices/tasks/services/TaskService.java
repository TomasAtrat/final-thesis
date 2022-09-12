package com.gmail.tomasatrat.backend.microservices.tasks.services;

import com.gmail.tomasatrat.backend.common.ICrudService;
import com.gmail.tomasatrat.backend.common.IDataEntity;
import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
import com.gmail.tomasatrat.backend.data.entity.Task;
import com.gmail.tomasatrat.backend.microservices.orders.components.OrderClient;
import com.gmail.tomasatrat.backend.microservices.tasks.components.TaskClient;
import com.gmail.tomasatrat.backend.repositories.OrderInfoRepository;
import com.gmail.tomasatrat.backend.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService implements ICrudService {

    private final TaskClient taskClient;
    private TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        taskClient = new TaskClient();
    }

    @Override
    public List<Task> findAll() {
        return this.taskRepository.findAll();
    }

    @Override
    public IDataEntity addItem(IDataEntity item) {
        return this.taskRepository.save((Task) item);
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
