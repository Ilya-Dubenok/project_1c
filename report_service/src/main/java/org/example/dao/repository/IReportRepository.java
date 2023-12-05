package org.example.dao.repository;

import org.example.dao.entities.Report;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface IReportRepository extends MongoRepository<Report, UUID> {


}
