package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.mappers.WorkoutPlanMapper;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.repositories.WorkoutPlanrepository;
import com.example.gym_bro_rest_api.services.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkoutPlanServiceImpl implements WorkoutPlanService {
    private final WorkoutPlanMapper workoutPlanMapper;
    private final WorkoutPlanrepository workoutPlanrepository;
    private final ExerciseRepository exerciseRepository;
    private final CacheManager cacheManager;

    private void evictWorkoutPlanCache(Long workoutPlanId, Long userId) {
        Cache workoutPlanCache = cacheManager.getCache("WORKOUTPLAN_CACHE");

        if (workoutPlanCache != null) {
            workoutPlanCache.evict(workoutPlanId + "-" + userId);
        }

        Cache workoutPlanListCache = cacheManager.getCache("WORKOUTPLAN_LIST_CACHE");

        if (workoutPlanListCache != null) {
            workoutPlanListCache.evict(userId);
        }
    }

    private List<Exercise> convertExerciseDtoList(List<ExerciseDTO> list, User user) {
        return list.stream()
                .map(exerciseDTO -> {
                    Exercise exercise = exerciseRepository.findById(exerciseDTO.getId())
                            .orElseThrow(NotFoundException::new);

                    if (!exercise.getUser().getId().equals(user.getId())) {
                        throw new NoAccessException();
                    }

                    return exercise;
                })
                .toList();
    }
    @Override
    public WorkoutPlanDTO saveNewWorkoutPlan(WorkoutPlanDTO workoutPlanDTO, User user) {
        List<Exercise> exercises = new ArrayList<>();

        if (workoutPlanDTO.getExercises() != null && !workoutPlanDTO.getExercises().isEmpty()) {
            exercises = convertExerciseDtoList(workoutPlanDTO.getExercises(), user);
        }

        WorkoutPlan workoutPlan = WorkoutPlan.builder()
                .name(workoutPlanDTO.getName())
                .exercises(exercises)
                .setsReps(workoutPlanDTO.getSetsReps())
                .user(user)
                .build();

        return workoutPlanMapper.workoutPlanToWorkoutPlanDto(workoutPlanrepository.save(workoutPlan));
    }

    @Override
    public WorkoutPlanDTO getWorkoutPlanById(Long id, User user) {
        WorkoutPlan workoutPlan = workoutPlanrepository.findById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(workoutPlan.getUser().getId(), user.getId())) {
            throw new NoAccessException();
        }

        return workoutPlanMapper.workoutPlanToWorkoutPlanDto(workoutPlan);
    }

    @Override
    public void updateWorkoutPlanById(Long id, WorkoutPlanDTO workoutPlanDTO, User user) {
            WorkoutPlan workoutPlan = workoutPlanrepository.findById(id).orElseThrow(NotFoundException::new);

            if (!Objects.equals(workoutPlan.getUser().getId(), user.getId())) {
                throw new NoAccessException();
            }

            List<Exercise> exercises = new ArrayList<>();

            if (workoutPlanDTO.getExercises() != null && !workoutPlanDTO.getExercises().isEmpty()) {
                exercises = new ArrayList<>(convertExerciseDtoList(workoutPlanDTO.getExercises(), user));
            }

            workoutPlan.setName(workoutPlanDTO.getName());
            workoutPlan.setExercises(exercises);
            workoutPlan.setSetsReps(workoutPlanDTO.getSetsReps());
            workoutPlanrepository.save(workoutPlan);
    }

    @Override
    public void deleteWorkoutPlanById(Long id, User user) {
        WorkoutPlan workoutPlan = workoutPlanrepository.findById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(workoutPlan.getUser().getId(), user.getId())) {
            throw new NoAccessException();
        }

        workoutPlanrepository.deleteById(id);
    }

    @Override
    public Page<WorkoutPlanDTO> listExercisesOfUser(User user, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PaginationUtils.buildPageRequest(pageNumber, pageSize);
        Page<WorkoutPlan> workoutPlansPage = workoutPlanrepository.findWorkoutPlansByUserId(user.getId(), pageRequest);

        return workoutPlansPage.map(workoutPlanMapper::workoutPlanToWorkoutPlanDto);
    }
}
