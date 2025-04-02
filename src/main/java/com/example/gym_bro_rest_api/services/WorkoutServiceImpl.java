package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.ExerciseSet;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.mappers.WorkoutMapper;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutCreationDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutViewDTO;
import com.example.gym_bro_rest_api.repositories.WorkoutPlanrepository;
import com.example.gym_bro_rest_api.repositories.WorkoutRepository;
import com.example.gym_bro_rest_api.services.utils.PaginationUtils;
import com.example.gym_bro_rest_api.services.workoutplan.WorkoutPlanQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutPlanrepository workoutPlanrepository;
    private final WorkoutRepository workoutRepository;
    private final ExerciseSetService exerciseSetService;
    private final WorkoutMapper workoutMapper;

    @Override
    public Long saveNewWorkout(WorkoutCreationDTO workoutCreationDTO, User user) {
        WorkoutPlan workoutPlan = workoutPlanrepository.findById(workoutCreationDTO.getWorkoutPlanId())
                .orElseThrow(NotFoundException::new);

        if (!workoutPlan.getUser().getId().equals(user.getId())) {
            throw new NoAccessException();
        }

        Workout workout = workoutRepository.save(Workout.builder()
                .workoutPlan(workoutPlan)
                .user(user)
                .build());

        return workout.getId();
    }

    @Override
    public Workout addNewSet(Long workoutId, ExerciseSetDTO exerciseSetDTO, User user) {
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(NotFoundException::new);

        if (!workout.getUser().getId().equals(user.getId())) {
            throw new NoAccessException();
        }

        ExerciseSet exerciseSet = exerciseSetService.saveNewExerciseSet(exerciseSetDTO, workout);

        List<ExerciseSet> sets = workout.getSets();
        sets.add(exerciseSet);
        workout.setSets(sets);

        return workoutRepository.save(workout);
    }

    @Override
    public Boolean deleteSet(Long workoutId, Long setId, User user) {
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(NotFoundException::new);

        if (!workout.getUser().getId().equals(user.getId())) {
            throw new NoAccessException();
        }

        ExerciseSet exerciseSet = exerciseSetService.getExerciseSetById(setId).orElseThrow(NotFoundException::new);

        if (!exerciseSet.getUser().getId().equals(user.getId())) {
            throw new NoAccessException();
        }

        if (!exerciseSet.getWorkout().equals(workout)) {
            return false;
        }

        List<ExerciseSet> sets = workout.getSets();
        if (!sets.removeIf(set -> set.equals(exerciseSet))) {
            return false;
        }

        workout.setSets(sets);
        Workout updatedWorkout = workoutRepository.save(workout);

        return exerciseSetService.deleteSetById(setId);
    }

    @Override
    public void deleteWorkoutById(Long workoutId, User user) {
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(NotFoundException::new);

        if (!workout.getUser().getId().equals(user.getId())) {
            throw new NoAccessException();
        }

        if (!workout.getSets().isEmpty()) {
            workout.getSets().forEach(set -> {
                exerciseSetService.deleteSetById(set.getId());
            });
        }

        workoutRepository.delete(workout);
    }

    @Override
    public WorkoutViewDTO getWorkoutById(Long workoutId, User user) {
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(NotFoundException::new);

        if (!user.getId().equals(workout.getUser().getId())) {
            throw new NoAccessException();
        }

        return workoutMapper.workoutToWorkoutViewDTO(workout);
    }

    @Override
    public Page<WorkoutViewDTO> listWorkoutsOfUser(User user, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PaginationUtils.buildPageRequest(pageNumber, pageSize);
        Page<Workout> workoutsPage = workoutRepository.findWorkoutsByUserId(user.getId(), pageRequest);

        return workoutsPage.map(workoutMapper::workoutToWorkoutViewDTO);
    }

}
