package com.example.gym_bro_rest_api.services.workoutplan;

import com.example.gym_bro_rest_api.controller.NoAccessException;
import com.example.gym_bro_rest_api.controller.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.mappers.WorkoutPlanMapper;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import com.example.gym_bro_rest_api.repositories.WorkoutPlanrepository;
import com.example.gym_bro_rest_api.services.exercise.ExerciseQueryService;
import com.example.gym_bro_rest_api.services.exercise.ExerciseServiceImpl;
import lombok.RequiredArgsConstructor;
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
    private final ExerciseQueryService exerciseQueryService;

    private List<Exercise> convertExerciseDtoList(List<ExerciseDTO> list, User user) {
        return list.stream()
                .map(exerciseDTO -> {
                    Exercise exercise = exerciseQueryService.getExerciseById(exerciseDTO.getId());

                    if (!exercise.getUser().getId().equals(user.getId())) {
                        throw new NoAccessException();
                    }

                    return exercise;
                })
                .toList();
    }
    @Override
    public WorkoutPlanDTO saveNewWorkoutPlan(WorkoutPlanDTO workoutPlanDTO, User user) {
        List<Exercise> exercises;
        if (workoutPlanDTO.getExercises() == null || workoutPlanDTO.getExercises().isEmpty()) {
            exercises = new ArrayList<>();
        } else {
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
    public Optional<WorkoutPlanDTO> getWorkoutPlanById(Long id) {
        return workoutPlanrepository.findById(id)
                .map(workoutPlanMapper::workoutPlanToWorkoutPlanDto);
    }

    @Override
    public Optional<Object> updateWorkoutPlanById(Long id, WorkoutPlanDTO workoutPlanDTO, User user) {
        return workoutPlanrepository.findById(id).map(workoutPlan -> {
            if (!Objects.equals(workoutPlan.getUser().getId(), user.getId())) {
                throw new NoAccessException();
            }

            List<Exercise> exercises = new ArrayList<>(convertExerciseDtoList(workoutPlanDTO.getExercises(), user));

            workoutPlan.setName(workoutPlanDTO.getName());
            workoutPlan.setExercises(exercises);
            workoutPlan.setSetsReps(new ArrayList<>(workoutPlanDTO.getSetsReps()));
            WorkoutPlan updated = workoutPlanrepository.save(workoutPlan);

            return workoutPlanMapper.workoutPlanToWorkoutPlanDto(updated);
        });
    }

    @Override
    public void deleteWorkoutPlanById(Long id, User user) {
        if (!workoutPlanrepository.existsById(id)) {
            throw new NotFoundException();
        }

        if (!workoutPlanrepository.existsByIdAndUserId(id, user.getId())) {
            throw new NoAccessException();
        }

        workoutPlanrepository.deleteById(id);
    }

    @Override
    public Page<WorkoutPlanDTO> listExercisesOfUser(User user, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = ExerciseServiceImpl.buildPageRequest(pageNumber, pageSize);
        Page<WorkoutPlan> workoutPlansPage = workoutPlanrepository.findWorkoutPlansByUserId(user.getId(), pageRequest);

        return workoutPlansPage.map(workoutPlanMapper::workoutPlanToWorkoutPlanDto);
    }
}
