package com.smart.academy.service.impl;

import com.smart.academy.service.PersonService;
import com.smart.academy.domain.Person;
import com.smart.academy.repository.PersonRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service Implementation for managing {@link Person}.
 */
@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    private final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PersonRepository personRepository;

    private final RestTemplate restTemplate;

    public PersonServiceImpl(PersonRepository personRepository, RestTemplate restTemplate) {
        this.personRepository = personRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Person save(Person person) {
        log.debug("Request to save Person : {}", person);
        return personRepository.save(person);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Person> findAll(Pageable pageable) {
        log.debug("Request to get all People");
        return personRepository.findAll(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findOne(Long id) {
        log.debug("Request to get Person : {}", id);
        return personRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Person : {}", id);
        personRepository.deleteById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public byte[] createPdfReport() throws JRException {

        final JasperPrint print = getJasperPrint();
        // Export the report to a PDF file.
        return JasperExportManager.exportReportToPdf(print);
    }


    @Override
    @Transactional(readOnly = true)
    public byte[] createXlsReport() throws JRException, IOException {
        final JasperPrint print = getJasperPrint();
        try (ByteArrayOutputStream xlsReport = new ByteArrayOutputStream()) {
            JRXlsxExporter exporter = new JRXlsxExporter(); // initialize exporter
            exporter.setExporterInput(new SimpleExporterInput(print)); // set compiled report as input
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsReport));  // set output file via path with filename
            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setOnePagePerSheet(true); // setup configuration
            configuration.setDetectCellType(true);
            exporter.setConfiguration(configuration); // set configuration
            exporter.exportReport();
            return xlsReport.toByteArray();
        }
    }

    private JasperPrint getJasperPrint() throws JRException {
        // Fetching the .jrxml file from the resources folder.
        final InputStream stream = this.getClass().getResourceAsStream("/example.jrxml");

        // Compile the Jasper report from .jrxml to .japser
        final JasperReport report = JasperCompileManager.compileReport(stream);

        // Fetching the employees from the data source.
        List<Person> all = personRepository.findAll();
        final JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(all);

        // Adding the additional parameters to the pdf.
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "javacodegeek.com");

        // Filling the report with the employee data and additional parameters information.
        return JasperFillManager.fillReport(report, parameters, source);
    }

    @Scheduled(cron="* */5 * * * ?")
    public void lookupAndSaveNewPerson() {
        List<Person> personList = lookupNewPerson();
        log.debug("Found {} person. Saving.", personList.size());
        personRepository.saveAll(personList);
    }


    private List<Person> lookupNewPerson() {
        String url = "https://api.mocki.io/v1/b043df5a";
        log.debug("Lookup new people. Call external resource {}", url);
        ResponseEntity<List<Person>> response = restTemplate.exchange(url, HttpMethod.GET, getHttpEntity(),
            new ParameterizedTypeReference<List<Person>>() {
            });
        return response.getBody();
    }

    private HttpEntity getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }
}
