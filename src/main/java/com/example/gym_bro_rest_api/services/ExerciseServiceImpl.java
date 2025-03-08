package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.NoAccessException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.mappers.ExerciseMapper;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;
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
    public Optional<ExerciseDTO> getExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .map(exerciseMapper::exerciseToExerciseDto);
    }

    @Override
    public Optional<ExerciseDTO> updateExerciseById(Long id, ExerciseDTO exerciseDTO, User user) {
        return exerciseRepository.findById(id).map(exercise -> {
            if (!Objects.equals(exercise.getUser().getId(), user.getId())) {
                throw new NoAccessException();
            }

            exercise.setName(exerciseDTO.getName());
            exercise.setDemonstrationUrl(exerciseDTO.getDemonstrationUrl());
            exerciseRepository.save(exercise);

            return exerciseMapper.exerciseToExerciseDto(exercise);
        });
    }
}
