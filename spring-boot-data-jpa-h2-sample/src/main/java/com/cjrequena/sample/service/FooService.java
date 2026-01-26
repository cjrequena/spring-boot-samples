package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.exception.DomainException;
import com.cjrequena.sample.domain.exception.FooNotFoundException;
import com.cjrequena.sample.domain.mapper.FooMapper;
import com.cjrequena.sample.domain.model.Foo;
import com.cjrequena.sample.persistence.entity.FooEntity;
import com.cjrequena.sample.persistence.repository.FooRepository;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = DomainException.class)
public class FooService {

  private final FooMapper fooMapper;
  private final FooRepository fooRepository;

  public Foo create(Foo foo) {
    FooEntity entity = fooMapper.toEntity(foo);
    FooEntity saved = fooRepository.saveAndFlush(entity);
    log.debug("Created Foo id={}", saved.getId());
    return fooMapper.toDomain(saved);
  }

  @Transactional(readOnly = true)
  public Foo retrieveById(Long id) {

    FooEntity entity = fooRepository
      .findById(id)
      .orElseThrow(() -> new FooNotFoundException("Foo with ID " + id + " was not found"));

    return fooMapper.toDomain(entity);
  }

  @Transactional(readOnly = true)
  public List<Foo> retrieve() {
    return fooRepository.findAll().stream()
      .map(fooMapper::toDomain)
      .toList();
  }

  public Foo update(Foo foo) {
    fooRepository.findById(foo.getId())
      .orElseThrow(() -> new FooNotFoundException("Foo with ID " + foo.getId() + " was not found"));

    FooEntity updated = fooRepository.saveAndFlush(fooMapper.toEntity(foo));

    log.debug("Updated Foo id={}", updated.getId());
    return fooMapper.toDomain(updated);
  }

  // TODO: implement JSON-PATCH
  public Foo patch(Long id, JsonPatch patchDocument) {
    throw new UnsupportedOperationException("JSON Patch not yet implemented");
  }

  // TODO: implement JSON-MERGE-PATCH
  public Foo patch(Long id, JsonMergePatch mergePatchDocument) {
    throw new UnsupportedOperationException("JSON Merge Patch not yet implemented");
  }

  public void delete(Long id) {
    FooEntity entity = fooRepository
      .findById(id)
      .orElseThrow(() -> new FooNotFoundException("Foo with ID " + id + " was not found"));

    fooRepository.delete(entity);
    log.debug("Deleted Foo id={}", id);
  }
}
