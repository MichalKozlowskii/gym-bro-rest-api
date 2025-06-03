package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.mappers.ExerciseMapper;
import com.example.gym_bro_rest_api.mappers.ExerciseSetMapper;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.model.StatsView;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.repositories.ExerciseSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final ExerciseSetRepository exerciseSetRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;
    private final ExerciseSetMapper exerciseSetMapper;

    private Boolean isStatsViewValid(StatsView stats) {
        if (stats.getMostRepsSet() == null ||
                stats.getLeastRepsSet() == null ||
                stats.getMostWeightSet() == null ||
                stats.getLeastWeightSet() == null ||
                stats.getNewestSet() == null ||
                stats.getOldestSet() == null ||
                stats.getPlotData() == null || stats.getPlotData().isEmpty()) {
            return false;
        }

        return true;
    }
    @Override
    public Optional<StatsView> getExerciseStats(Long exerciseId, User user) {
        ExerciseDTO exerciseDTO = exerciseMapper.exerciseToExerciseDto(
                exerciseRepository.findById(exerciseId).orElseThrow(NotFoundException::new));

        if (!exerciseDTO.getUserId().equals(user.getId())) {
            throw new NoAccessException();
        }

        ExerciseSetDTO mostRepsSet = exerciseSetMapper.exerciseSetToExerciseSetDTO(
                exerciseSetRepository.findTopByExerciseIdOrderByRepsDesc(exerciseId)
                        .orElse(null));

        ExerciseSetDTO leastRepsSet =  exerciseSetMapper.exerciseSetToExerciseSetDTO(
                exerciseSetRepository.findTopByExerciseIdOrderByRepsAsc(exerciseId)
                        .orElse(null));

        ExerciseSetDTO mostWeightSet =  exerciseSetMapper.exerciseSetToExerciseSetDTO(
                exerciseSetRepository.findTopByExerciseIdOrderByWeightDesc(exerciseId)
                        .orElse(null));

        ExerciseSetDTO leastWeightSet =  exerciseSetMapper.exerciseSetToExerciseSetDTO(
                exerciseSetRepository.findTopByExerciseIdOrderByWeightAsc(exerciseId)
                        .orElse(null));

        ExerciseSetDTO newestSet =  exerciseSetMapper.exerciseSetToExerciseSetDTO(
                exerciseSetRepository.findTopByExerciseIdOrderByCreationDateDesc(exerciseId)
                        .orElse(null));

        ExerciseSetDTO oldestSet = exerciseSetMapper.exerciseSetToExerciseSetDTO(
                exerciseSetRepository.findTopByExerciseIdOrderByCreationDateAsc(exerciseId)
                        .orElse(null));

        List<ExerciseSetDTO> plotData = exerciseSetRepository.findBestSetPerDayByExerciseId(exerciseId)
                .stream()
                .map(exerciseSetMapper::exerciseSetToExerciseSetDTO)
                .toList();

        StatsView statsView = StatsView.builder()
                .mostRepsSet(mostRepsSet)
                .leastRepsSet(leastRepsSet)
                .mostWeightSet(mostWeightSet)
                .leastWeightSet(leastWeightSet)
                .newestSet(newestSet)
                .oldestSet(oldestSet)
                .plotData(plotData)
                .build();

        if (!isStatsViewValid(statsView)) {
            return Optional.empty();
        }

        return Optional.of(statsView);
    }
}
