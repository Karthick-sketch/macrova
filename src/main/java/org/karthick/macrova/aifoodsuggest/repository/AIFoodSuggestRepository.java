package org.karthick.macrova.aifoodsuggest.repository;

import org.karthick.macrova.aifoodsuggest.entity.AIFoodSuggest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AIFoodSuggestRepository
  extends MongoRepository<AIFoodSuggest, String> {}
