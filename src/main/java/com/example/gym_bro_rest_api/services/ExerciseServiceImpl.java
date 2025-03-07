package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.mappers.ExerciseMapper;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    ExerciseRepository exerciseRepository;
    ExerciseMapper exerciseMapper;
    @Override
    public ExerciseDTO saveNewExercise(ExerciseDTO exerciseDTO, User user) {
        Exercise exercise = Exercise.builder()
                .name(exerciseDTO.getName())
                .demonstrationUrl(exerciseDTO.getDemonstrationUrl())
                .user(user)
                .build();

        return exerciseMapper.exerciseToExerciseDto(exerciseRepository.save(exercise));
    }
}
