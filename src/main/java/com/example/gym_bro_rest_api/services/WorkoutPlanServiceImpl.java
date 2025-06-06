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
import com.example.gym_bro_rest_api.services.utils.CacheUtils;
import com.example.gym_bro_rest_api.services.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WorkoutPlanServiceImpl implements WorkoutPlanService {
    private static final String WORKOUTPLAN_CACHE = "WORKOUTPLAN_CACHE";
    private static final String WORKOUTPLAN_LIST_CACHE = "WORKOUTPLAN_LIST_CACHE";

    private final WorkoutPlanMapper workoutPlanMapper;
    private final WorkoutPlanrepository workoutPlanrepository;
    private final ExerciseRepository exerciseRepository;
    private final CacheUtils cacheUtils;

    private List<Exercise> convertExerciseDtoList(List<ExerciseDTO> list, User user) {
        return list.stream()
                .map(exerciseDTO -> {
                    Exercise exercise = exerciseRepository.findById(exerciseDTO.getId())
                            .orElseThrow(NotFoundException::new);
                    if (!Objects.equals(exercise.getUser().getId(), user.getId())) {
                        throw new NoAccessException();
                    }
                    return exercise;
                })
                .toList();
    }

    private void evictWorkoutPlanCache(Long id, Long userId) {
        String idKey = id + "-" + userId;
        cacheUtils.evictSingle(WORKOUTPLAN_CACHE, idKey);
        cacheUtils.evict(WORKOUTPLAN_LIST_CACHE, userId);
    }

    private void evictWorkoutPlanPages(Long userId) {
        cacheUtils.evict(WORKOUTPLAN_LIST_CACHE, userId);
    }

    @Override
    public WorkoutPlanDTO saveNewWorkoutPlan(WorkoutPlanDTO workoutPlanDTO, User user) {
        List<Exercise> exercises = Collections.emptyList();
        if (workoutPlanDTO.getExercises() != null && !workoutPlanDTO.getExercises().isEmpty()) {
            exercises = convertExerciseDtoList(workoutPlanDTO.getExercises(), user);
        }

        WorkoutPlan workoutPlan = WorkoutPlan.builder()
                .name(workoutPlanDTO.getName())
                .exercises(exercises)
                .setsReps(workoutPlanDTO.getSetsReps())
                .user(user)
                .build();

        evictWorkoutPlanPages(user.getId());

        return workoutPlanMapper.workoutPlanToWorkoutPlanDto(workoutPlanrepository.save(workoutPlan));
    }

    @Override
    @Cacheable(cacheNames = WORKOUTPLAN_CACHE, key = "#id + '-' + #user.id")
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

        List<Exercise> exercises = Collections.emptyList();
        if (workoutPlanDTO.getExercises() != null && !workoutPlanDTO.getExercises().isEmpty()) {
            exercises = convertExerciseDtoList(workoutPlanDTO.getExercises(), user);
        }

        workoutPlan.setName(workoutPlanDTO.getName());
        workoutPlan.setExercises(exercises);
        workoutPlan.setSetsReps(workoutPlanDTO.getSetsReps());

        workoutPlanrepository.save(workoutPlan);

        evictWorkoutPlanCache(id, user.getId());
    }

    @Override
    public void deleteWorkoutPlanById(Long id, User user) {
        WorkoutPlan workoutPlan = workoutPlanrepository.findById(id).orElseThrow(NotFoundException::new);
        if (!Objects.equals(workoutPlan.getUser().getId(), user.getId())) {
            throw new NoAccessException();
        }

        workoutPlanrepository.deleteById(id);

        evictWorkoutPlanCache(id, user.getId());
    }

    @Override
    @Cacheable(cacheNames = WORKOUTPLAN_LIST_CACHE, key = "#user.id + '-' + #pageNumber + '-' + #pageSize")
    public Page<WorkoutPlanDTO> listExercisesOfUser(User user, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PaginationUtils.buildPageRequest(pageNumber, pageSize);
        Page<WorkoutPlan> workoutPlansPage = workoutPlanrepository.findWorkoutPlansByUserId(user.getId(), pageRequest);

        String key = user.getId() + "-" + pageNumber + "-" + pageSize;
        cacheUtils.track(WORKOUTPLAN_LIST_CACHE, user.getId(), key);

        return workoutPlansPage.map(workoutPlanMapper::workoutPlanToWorkoutPlanDto);
    }
}
