package com.poc.ecommerce.cqrs.query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReadRepository extends CrudRepository<ProductReadModel, Long> {}
