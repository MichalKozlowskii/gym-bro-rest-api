package com.example.gym_bro_rest_api.services.exercise;

import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseQueryServiceImpl implements ExerciseQueryService {
    private final ExerciseRepository exerciseRepository;
    @Override
    public Exercise getExerciseById(Long id) {
        return exerciseRepository.findById(id).orElseThrow(NotFoundException::new);
    }
}
