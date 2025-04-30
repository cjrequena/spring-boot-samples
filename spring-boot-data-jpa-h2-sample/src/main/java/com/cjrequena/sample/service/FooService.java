package com.cjrequena.sample.service;

import com.cjrequena.sample.db.entity.FooEntity;
import com.cjrequena.sample.dto.FooDTO;
import com.cjrequena.sample.exception.service.FooNotFoundServiceException;
import com.cjrequena.sample.exception.service.ServiceException;
import com.cjrequena.sample.mapper.FooMapper;
import com.cjrequena.sample.repository.FooRepository;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = ServiceException.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FooService {

  private final FooMapper fooMapper;
  private final FooRepository fooRepository;

  public FooDTO create(FooDTO dto) {
    FooEntity entity = this.fooMapper.toEntity(dto);
    this.fooRepository.saveAndFlush(entity);
    dto = this.fooMapper.toDTO(entity);
    return dto;
  }

  public FooDTO retrieveById(Long id) throws FooNotFoundServiceException {
    Optional<FooEntity> optional = this.fooRepository.findById(id);
    if (!optional.isPresent()) {
      throw new FooNotFoundServiceException("Foo with (ID=" + id + ") was not found");
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
    if (optional.isPresent()) {
      FooEntity entity = this.fooMapper.toEntity(dto);
      fooRepository.saveAndFlush(entity);
      log.debug("Updated account with id {}", entity.getId());
      return this.fooMapper.toDTO(entity);
    } else {
      throw new FooNotFoundServiceException("The account " + dto.getId() + " was not Found");
    }
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
