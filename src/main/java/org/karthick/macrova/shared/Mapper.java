package org.karthick.macrova.shared;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Mapper {

  private final ModelMapper modelMapper;

  public <E, D> D convertToDto(E entity, Class<D> dtoClass) {
    return modelMapper.map(entity, dtoClass);
  }

  public <E, D> E convertToEntity(D dto, Class<E> entityClass) {
    return modelMapper.map(dto, entityClass);
  }
}
