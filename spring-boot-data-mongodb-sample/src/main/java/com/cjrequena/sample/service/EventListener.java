package com.cjrequena.sample.service;

import com.cjrequena.sample.db.entity.FooEntity;
import com.cjrequena.sample.dto.FooDTO;
import com.cjrequena.sample.mapper.FooMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventListener {
  private final ReactiveMongoTemplate reactiveMongoTemplate;
  private final FooMapper fooMapper;

  public Flux<FooDTO> subscribe(){
    return reactiveMongoTemplate
      .changeStream(FooEntity.class)
      .watchCollection("foo")
      .listen()
      .map((event)->this.fooMapper.toDTO(event.getBody()))
      .doOnNext(log::info);
  }

}
