package com.hrmManagement.service;

import com.hrmManagement.entity.Employee;
import com.hrmManagement.entity.EmployeeEvent;
import com.hrmManagement.entity.enums.EmployeeEventType;
import com.hrmManagement.repository.EmployeeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/** Writes entries on an employee's timeline; called by the services that change employee state. */
@Service
@RequiredArgsConstructor
public class EmployeeTimelineService {

    private final EmployeeEventRepository eventRepository;

    @Transactional
    public EmployeeEvent record(Employee employee, EmployeeEventType type, String title,
                                String oldValue, String newValue, String details) {
        return record(employee, type, LocalDate.now(), title, oldValue, newValue, details);
    }

    @Transactional
    public EmployeeEvent record(Employee employee, EmployeeEventType type, LocalDate date, String title,
                                String oldValue, String newValue, String details) {
        EmployeeEvent event = new EmployeeEvent();
        event.setOrganization(employee.getOrganization());
        event.setEmployee(employee);
        event.setType(type);
        event.setEventDate(date);
        event.setTitle(title);
        event.setOldValue(oldValue);
        event.setNewValue(newValue);
        event.setDetails(details);
        return eventRepository.save(event);
    }
}
