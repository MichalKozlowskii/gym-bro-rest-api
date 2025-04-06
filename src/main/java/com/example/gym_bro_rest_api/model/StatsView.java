package com.example.gym_bro_rest_api.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class StatsView {
    ExerciseDTO exerciseDto;
    ExerciseSetDTO mostRepsSet;
    ExerciseSetDTO leastRepsSet;
    ExerciseSetDTO mostWeightSet;
    ExerciseSetDTO leastWeightSet;
    ExerciseSetDTO newestSet;
    ExerciseSetDTO oldestSet;
    List<ExerciseSetDTO> plotData;
}
