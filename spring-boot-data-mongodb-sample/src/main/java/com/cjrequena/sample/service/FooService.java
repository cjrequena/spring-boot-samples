package com.cjrequena.sample.service;

import com.cjrequena.sample.db.entity.FooEntity;
import com.cjrequena.sample.db.repository.FooRepository;
import com.cjrequena.sample.dto.FooDTO;
import com.cjrequena.sample.exception.service.FooNotFoundServiceException;
import com.cjrequena.sample.exception.service.ServiceException;
import com.cjrequena.sample.mapper.FooMapper;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 */
@Log4j2
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = ServiceException.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FooService {

    private final FooMapper fooMapper;
    private final FooRepository fooRepository;

    public Mono<FooDTO> create(FooDTO dto) {
        FooEntity entity = this.fooMapper.toEntity(dto);
        Mono<FooDTO> dto$ = this.fooRepository.save(entity).map(this.fooMapper::toDTO);
        return dto$;
    }

    public Mono<FooDTO> retrieveById(String id) {
        Mono<FooEntity> entity$ = fooRepository.findById(id).switchIfEmpty(Mono.error(new FooNotFoundServiceException("Foo not found by id: " + id)));
        Mono<FooDTO> dto$ = entity$.map((entity) -> this.fooMapper.toDTO(entity));
        return dto$;
    }

    public List<FooDTO> retrieve() {
        Flux<FooEntity> entities$ = this.fooRepository.findAll();
        Flux<FooDTO> dtos$ = entities$.map((entity) -> this.fooMapper.toDTO(entity));
        return dtos$.collectList().block();
    }

    public FooDTO update(FooDTO dto) throws FooNotFoundServiceException {
        Mono<FooDTO> dto$ = fooRepository.findById(dto.getId())
                .flatMap((_entity$) -> this.fooRepository.save(this.fooMapper.toEntity(dto)).map(this.fooMapper::toDTO))
                .switchIfEmpty(Mono.error(new FooNotFoundServiceException("The account " + dto.getId() + " was not Found")));
        return dto$.block();
    }

    public FooDTO patch(Long id, JsonPatch patchDocument) {
        return null;
    }

    public FooDTO patch(Long id, JsonMergePatch mergePatchDocument) {
        return null;
    }

    public void delete(String id) throws FooNotFoundServiceException {
        fooRepository.findById(id)
                .flatMap((_entity$) -> this.fooRepository.deleteById(id))
                .switchIfEmpty(Mono.error(new FooNotFoundServiceException("The account " + id + " was not Found")));
    }
}
