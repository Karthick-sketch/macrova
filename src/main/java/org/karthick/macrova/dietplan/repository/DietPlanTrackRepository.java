package org.karthick.macrova.dietplan.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.karthick.macrova.dietplan.dto.DietPlanOverviewDTO;
import org.karthick.macrova.dietplan.entity.DietPlanTrack;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface DietPlanTrackRepository
  extends MongoRepository<DietPlanTrack, String>
{
  Optional<DietPlanTrack> findFirstByCreatedAtAndDietPlanId(
    LocalDate dateTime,
    String dietPlanId
  );

  @Query(
    value = "{ createdAt:  { $gte: ?0, $lte: ?1 }, dietPlanId: ?2 }",
    sort = "{ createdAt: 1 }"
  )
  List<DietPlanTrack> findByDateRangeAndDietPlanId(
    Date from,
    Date to,
    String dietPlanId
  );

  @Query(
    value = "{ dietPlanId: ?0 }",
    fields = "{ _id:  1, weight:  1, tdee: 1, intake:  1, protein:  1, carbs:  1, fat:  1, createdAt:  1 }",
    sort = "{ createdAt: -1 }"
  )
  List<DietPlanOverviewDTO> findAllDietPlanOverview(String dietPlanId);
}
