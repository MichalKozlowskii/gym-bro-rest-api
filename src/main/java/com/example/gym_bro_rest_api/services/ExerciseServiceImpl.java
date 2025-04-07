package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.mappers.ExerciseMapper;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.services.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;
    
    private final static int DEFAULT_PAGE = 0;
    private final static int DEFAULT_PAGE_SIZE = 10;

    @Override
    public ExerciseDTO saveNewExercise(ExerciseDTO exerciseDTO, User user) {
        Exercise exercise = Exercise.builder()
                .name(exerciseDTO.getName())
                .demonstrationUrl(exerciseDTO.getDemonstrationUrl())
                .user(user)
                .build();

        return exerciseMapper.exerciseToExerciseDto(exerciseRepository.save(exercise));
    }

    @Override
    @Cacheable(cacheNames = "EXERCISE_CACHE", key = "#id + '-' + #user.id")
    public ExerciseDTO getExerciseById(Long id, User user) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(exercise.getUser().getId(), user.getId())) {
            throw new NoAccessException();
        }

        return exerciseMapper.exerciseToExerciseDto(exercise);
    }

    @Override
    public ExerciseDTO updateExerciseById(Long id, ExerciseDTO exerciseDTO, User user) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(exercise.getUser().getId(), user.getId())) {
            throw new NoAccessException();
        }

        exercise.setName(exerciseDTO.getName());
        exercise.setDemonstrationUrl(exerciseDTO.getDemonstrationUrl());

        Exercise updated = exerciseRepository.save(exercise);

        return exerciseMapper.exerciseToExerciseDto(updated);
    }

    @Override
    public void deleteExerciseById(Long id, User user) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(exercise.getUser().getId(), user.getId())) {
            throw new NoAccessException();
        }

        exerciseRepository.deleteById(id);
    }

    @Override
    public Page<ExerciseDTO> listExercisesOfUser(User user, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PaginationUtils.buildPageRequest(pageNumber, pageSize);
        Page<Exercise> exercisePage = exerciseRepository.findExercisesByUserId(user.getId(), pageRequest);

        return exercisePage.map(exerciseMapper::exerciseToExerciseDto);
    }
}
