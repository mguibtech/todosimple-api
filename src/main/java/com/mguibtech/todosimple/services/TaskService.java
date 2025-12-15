package com.mguibtech.todosimple.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mguibtech.todosimple.models.Task;
import com.mguibtech.todosimple.models.User;
import com.mguibtech.todosimple.repositories.TaskRepository;

import jakarta.transaction.Transactional;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id) {
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new RuntimeException(
            "Tarefa não encontrada! Id: " + id + " Tipo: " + Task.class.getName()
        ));
    }

    public List<Task> findAllByUserId(long userId){
        List<Task> tasks = this.taskRepository.findByUser_id(userId);
        return  tasks;
    }

    @Transactional
    public Task create(Task obj) {
        User user = this.userService.findById(obj.getUser().getId());
        obj.setId(null);
        obj.setUser(user);
        obj.setCompleted(false);
        obj = this.taskRepository.save(obj);
        return obj;
    }

    @Transactional
    public Task update(Task obj) {
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        if (obj.getCompleted() != null) {
            newObj.setCompleted(obj.getCompleted());
        }
        return this.taskRepository.save(newObj);
    }

    @Transactional
    public void delete(Long id) {
        findById(id);
        try{
            this.taskRepository.deleteById(id);
        }catch (Exception e){
            throw new RuntimeException("Não é possível excluir pois há entidades relacionadas!");
        }
    }

    @Transactional
    public Task complete(Long id) {
        Task newObj = findById(id);
        newObj.setCompleted(!newObj.getCompleted());
        return this.taskRepository.save(newObj);
    }
}
