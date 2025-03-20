package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.NotFoundException;
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
