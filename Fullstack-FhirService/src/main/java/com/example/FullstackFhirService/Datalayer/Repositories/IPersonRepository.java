package com.example.FullstackFhirService.Datalayer.Repositories;

import com.example.FullstackFhirService.Model.Models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPersonRepository extends JpaRepository<Person, Long> {
}
