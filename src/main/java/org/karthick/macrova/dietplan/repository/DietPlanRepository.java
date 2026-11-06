package org.karthick.macrova.dietplan.repository;

import java.util.List;
import java.util.Optional;
import org.karthick.macrova.dietplan.dto.DietPlanHistoryDTO;
import org.karthick.macrova.dietplan.entity.DietPlan;
import org.karthick.macrova.dietplan.enums.PlanStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DietPlanRepository extends MongoRepository<DietPlan, String> {
  @Query(
    value = "{ userId:  ?0 }",
    fields = "{ _id: 1, title: 1, plan: 1, weight: 1, targetWeight: 1, duration: 1, planStatus: 1 }",
    sort = "{ createdAt:  -1 }"
  )
  List<DietPlanHistoryDTO> findDietPlanHistoryByUserId(String userId);

  @Query(
    value = "{ userId:  ?0 }",
    fields = "{ _id: 1, title: 1, plan: 1, weight: 1, targetWeight: 1, duration: 1, planStatus: 1 }",
    sort = "{ createdAt:  -1 }"
  )
  Optional<DietPlanHistoryDTO> findFirstDietPlanHistoryByUserId(String userId);

  List<DietPlan> findByUserId(String userId);

  Optional<DietPlan> findByIdAndUserId(String id, String userId);

  Optional<DietPlan> findByUserIdAndPlanStatus(
    String userId,
    PlanStatus planStatus
  );
}
