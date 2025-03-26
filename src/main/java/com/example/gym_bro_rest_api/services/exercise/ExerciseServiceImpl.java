package com.example.gym_bro_rest_api.services.exercise;

import com.example.gym_bro_rest_api.controller.NoAccessException;
import com.example.gym_bro_rest_api.controller.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.mappers.ExerciseMapper;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;
    private final Validator validator;

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

            Set<ConstraintViolation<Exercise>> violations = validator.validate(exercise);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            Exercise updated = exerciseRepository.save(exercise);

            return exerciseMapper.exerciseToExerciseDto(updated);
        });
    }

    @Override
    public void deleteExerciseById(Long id, User user) {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!user.getId().equals(exercise.getUser().getId())) {
            throw new NoAccessException();
        }

        exerciseRepository.deleteById(id);
    }

    @Override
    public Page<ExerciseDTO> listExercisesOfUser(User user, Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
        Page<Exercise> exercisePage = exerciseRepository.findExercisesByUserId(user.getId(), pageRequest);

        return exercisePage.map(exerciseMapper::exerciseToExerciseDto);
    }

    public static PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber;
        int queryPageSize;

        if (pageNumber != null && pageNumber > 0 ) {
            queryPageNumber = pageNumber - 1;
        } else {
            queryPageNumber = DEFAULT_PAGE;
        }

        if (pageSize == null) {
            queryPageSize = DEFAULT_PAGE_SIZE;
        } else {
            System.out.println(pageSize.toString());
            if (pageSize > 1000) {
                queryPageSize = 1000;
            } else {
                queryPageSize = pageSize;
            }
        }

        Sort sort = Sort.by(Sort.Order.desc("creationDate"));

        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }

}
