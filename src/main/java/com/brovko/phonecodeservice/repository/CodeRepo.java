package com.brovko.phonecodeservice.repository;

import com.brovko.phonecodeservice.domain.Code;
import org.springframework.data.repository.CrudRepository;

public interface CodeRepo extends CrudRepository<Code, Long> {
	Code findByName(String name);
}
