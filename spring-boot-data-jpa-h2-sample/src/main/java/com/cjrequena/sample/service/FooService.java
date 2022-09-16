package com.cjrequena.sample.service;

import com.cjrequena.sample.db.entity.FooEntity;
import com.cjrequena.sample.db.repository.FooRepository;
import com.cjrequena.sample.dto.FooDTO;
import com.cjrequena.sample.exception.service.FooNotFoundServiceException;
import com.cjrequena.sample.mapper.FooMapper;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 * @author cjrequena
 */
@Log4j2
@Service
@Transactional
public class FooService {

  private FooMapper fooMapper;
  private FooRepository fooRepository;

  /**
   *
   * @param fooRepository
   */
  @Autowired
  public FooService(FooRepository fooRepository, FooMapper fooMapper) {
    this.fooRepository = fooRepository;
    this.fooMapper = fooMapper;
  }

  public FooDTO create(FooDTO dto) {
    FooEntity entity = this.fooMapper.toEntity(dto);
    this.fooRepository.saveAndFlush(entity);
    dto = this.fooMapper.toDTO(entity);
    return dto;
  }

  public FooDTO retrieveById(Long id) throws FooNotFoundServiceException {
    Optional<FooEntity> optional = this.fooRepository.findById(id);
    if (!optional.isPresent()) {
      throw new FooNotFoundServiceException("Foo Not Found");
    }
    return fooMapper.toDTO(optional.get());
  }

  public List<FooDTO> retrieve() {
    List<FooEntity> entities = this.fooRepository.findAll();
    List<FooDTO> dtoList = entities.stream().map(
      (entity) -> this.fooMapper.toDTO(entity)
    ).collect(Collectors.toList());
    return dtoList;
  }

  public FooDTO update(FooDTO dto) throws FooNotFoundServiceException {
    Optional<FooEntity> optional = fooRepository.findById(dto.getId());
    optional.ifPresent(
      entity -> {
        entity = this.fooMapper.toEntity(dto);
        fooRepository.saveAndFlush(entity);
        log.debug("Updated User: {}", entity);
      }
    );
    optional.orElseThrow(() -> new FooNotFoundServiceException("Not Found"));
    return this.fooMapper.toDTO(optional.get());
  }

  public FooDTO patch(Long id, JsonPatch patchDocument) {
    return null;
  }

  public FooDTO patch(Long id, JsonMergePatch mergePatchDocument) {
    return null;
  }

  public void delete(Long id) throws FooNotFoundServiceException {
    Optional<FooEntity> optional = fooRepository.findById(id);
    optional.ifPresent(
      entity -> {
        fooRepository.delete(entity);
        log.debug("Deleted User: {}", entity);
      }
    );
    optional.orElseThrow(() -> new FooNotFoundServiceException("Not Found"));
  }
}
