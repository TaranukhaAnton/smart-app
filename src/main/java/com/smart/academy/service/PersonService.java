package com.smart.academy.service;

import com.smart.academy.domain.Person;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Optional;

/**
 * Service Interface for managing {@link Person}.
 */
public interface PersonService {

    /**
     * Save a person.
     *
     * @param person the entity to save.
     * @return the persisted entity.
     */
    Person save(Person person);

    /**
     * Get all the people.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Person> findAll(Pageable pageable);


    /**
     * Get the "id" person.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Person> findOne(Long id);

    /**
     * Delete the "id" person.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Generate pdf report.
     *
     */
    byte[] createPdfReport() throws JRException;

    /**
     * Generate xls report.
     *
     */
    byte[] createXlsReport() throws JRException, IOException;

    /**
     * Lookup external person and save them to DB.
     *
     */
    void lookupAndSaveNewPerson();
}
