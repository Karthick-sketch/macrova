package org.karthick.macrova.aifoodsuggest.repository;

import org.karthick.macrova.aifoodsuggest.entity.AIFoodSuggestResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AIFoodSuggestResponseRepository
  extends MongoRepository<AIFoodSuggestResponse, String> {}
