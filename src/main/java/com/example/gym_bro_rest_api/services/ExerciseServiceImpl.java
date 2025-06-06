package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.mappers.ExerciseMapper;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.services.utils.CacheUtils;
import com.example.gym_bro_rest_api.services.utils.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;
    private final CacheUtils cacheUtils;

    private static final String EXERCISE_CACHE = "EXERCISE_CACHE";
    private static final String EXERCISE_LIST_CACHE = "EXERCISE_LIST_CACHE";

    private void evictExercisePages(Long userId) {
        cacheUtils.evict(EXERCISE_LIST_CACHE, userId);
    }

    private void evictExerciseCache(Long exerciseId, Long userId) {
        String key = exerciseId + "-" + userId;
        cacheUtils.evictSingle(EXERCISE_CACHE, key);
        evictExercisePages(userId);
    }

    @Override
    public ExerciseDTO saveNewExercise(ExerciseDTO exerciseDTO, User user) {
        Exercise exercise = Exercise.builder()
                .name(exerciseDTO.getName())
                .demonstrationUrl(exerciseDTO.getDemonstrationUrl())
                .user(user)
                .build();

        evictExercisePages(user.getId());

        return exerciseMapper.exerciseToExerciseDto(exerciseRepository.save(exercise));
    }

    @Override
    @Cacheable(cacheNames = EXERCISE_CACHE, key = "#id + '-' + #user.id")
    public ExerciseDTO getExerciseById(Long id, User user) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(exercise.getUser().getId(), user.getId())) {
            throw new NoAccessException();
        }

        return exerciseMapper.exerciseToExerciseDto(exercise);
    }

    @Override
    public void updateExerciseById(Long id, ExerciseDTO exerciseDTO, User user) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(exercise.getUser().getId(), user.getId())) {
            throw new NoAccessException();
        }

        evictExerciseCache(id, user.getId());

        exercise.setName(exerciseDTO.getName());
        exercise.setDemonstrationUrl(exerciseDTO.getDemonstrationUrl());

        exerciseRepository.save(exercise);
    }

    @Override
    public void deleteExerciseById(Long id, User user) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(exercise.getUser().getId(), user.getId())) {
            throw new NoAccessException();
        }

        evictExerciseCache(id, user.getId());

        exerciseRepository.deleteById(id);
    }

    @Override
    @Cacheable(cacheNames = EXERCISE_LIST_CACHE, key = "#user.id + '-' + #pageNumber + '-' + #pageSize")
    public Page<ExerciseDTO> listExercisesOfUser(User user, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PaginationUtils.buildPageRequest(pageNumber, pageSize);
        var page = exerciseRepository.findExercisesByUserId(user.getId(), pageRequest);

        String key = user.getId() + "-" + pageNumber + "-" + pageSize;
        cacheUtils.track(EXERCISE_LIST_CACHE, user.getId(), key);

        return page.map(exerciseMapper::exerciseToExerciseDto);
    }
}
