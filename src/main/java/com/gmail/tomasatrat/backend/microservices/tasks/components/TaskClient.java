package com.gmail.tomasatrat.backend.microservices.tasks.components;

import com.gmail.tomasatrat.backend.common.wrappers.ErrorList;
import com.gmail.tomasatrat.backend.common.wrappers.ListOfOrderWrapper;
import com.gmail.tomasatrat.backend.common.wrappers.ListOfTaskWrapper;
import com.gmail.tomasatrat.backend.data.entity.OrderInfo;
import com.gmail.tomasatrat.backend.data.entity.Task;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.gmail.tomasatrat.backend.common.AppConstants.ORDERS_API;
import static com.gmail.tomasatrat.backend.common.AppConstants.WEB_SERVICE1;

public class TaskClient {

    private final RestTemplate restTemplate;
    private final String BASE_URL = WEB_SERVICE1 + ORDERS_API;

    public TaskClient() {
        this.restTemplate = new RestTemplate();
    }

    public List<Task> getTasks() {
        final String url = BASE_URL + "/";
        ListOfTaskWrapper taskWrapper = restTemplate.getForObject(url, ListOfTaskWrapper.class);
        return taskWrapper.getTasks();
    }

    public Task addTask(Task task) {
        final String url = BASE_URL + "/";
        restTemplate.postForObject(url, task, ErrorList.class);
        return task;
    }
}
