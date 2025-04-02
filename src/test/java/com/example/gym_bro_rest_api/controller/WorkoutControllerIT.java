package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.*;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.model.SetsReps;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutCreationDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutViewDTO;
import com.example.gym_bro_rest_api.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
class WorkoutControllerIT {
    @Autowired
    WorkoutController workoutController;

    @Autowired
    WorkoutRepository workoutRepository;

    @Autowired
    ExerciseSetRepository exerciseSetRepository;

    @Autowired
    WorkoutPlanrepository workoutPlanrepository;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    User user;
    User anotherUser;
    Exercise exercise1;
    Exercise exercise2;
    Exercise exercise3;
    WorkoutPlan workoutPlan;
    ExerciseSetDTO exerciseSetDTO;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        user = userRepository.save(User.builder()
                .username("test_user")
                .password("password")
                .build());

        anotherUser = userRepository.save(User.builder()
                .username("test_user1")
                .password("password")
                .build());

        exercise1 = exerciseRepository.save(Exercise.builder()
                .name("bench press")
                .user(user)
                .demonstrationUrl("fafeafasdz")
                .build());

        exercise2 = exerciseRepository.save(Exercise.builder()
                .name("barbell bicep curl")
                .user(user)
                .demonstrationUrl("fafeafasdz")
                .build());

        exercise3 = exerciseRepository.save(Exercise.builder()
                .name("barbell row")
                .user(user)
                .demonstrationUrl("fafeafasdz")
                .build());

        SetsReps suggestedSetsReps1 = SetsReps.builder()
                .sets(4)
                .reps(8)
                .build();

        SetsReps suggestedSetsReps2 = SetsReps.builder()
                .sets(3)
                .reps(8)
                .build();

        SetsReps suggestedSetsReps3 = SetsReps.builder()
                .sets(4)
                .reps(8)
                .build();

        workoutPlan = workoutPlanrepository.save(WorkoutPlan.builder()
                        .name("Workout A")
                        .user(user)
                        .exercises(new ArrayList<>(List.of(exercise1, exercise2, exercise3)))
                        .setsReps(new ArrayList<>(List.of(suggestedSetsReps1, suggestedSetsReps2, suggestedSetsReps3)))
                .build());

        exerciseSetDTO = ExerciseSetDTO.builder()
                .exercise(ExerciseDTO.builder().id(exercise1.getId()).build())
                .weight(70.)
                .reps(8)
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    Workout saveTestWorkout() {
        return workoutRepository.save(Workout.builder()
                .user(user)
                .workoutPlan(workoutPlan)
                .sets(new ArrayList<>())
                .build());
    }

    @Test
    void testCreateNewWorkout_Web_ValidationFailed() throws Exception {
        WorkoutCreationDTO workoutCreationDTO = WorkoutCreationDTO.builder().build();

        mockMvc.perform(post("/api/workout/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutCreationDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.workoutPlanId").value("Workout plan ID must not be null"));
    }

    @Test
    void testCreateNewWorkout_Web_Success() throws Exception {
        WorkoutCreationDTO workoutCreationDTO = WorkoutCreationDTO.builder()
                .workoutPlanId(workoutPlan.getId())
                .build();

        mockMvc.perform(post("/api/workout/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutCreationDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.success").value("Workout created."));
    }

    @Test
    void testGetWorkoutById_Web_NoAccess() throws Exception {
        Workout testWorkout = saveTestWorkout();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                anotherUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/workout/{id}", testWorkout.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetWorkoutById_Web_NotFound() throws Exception {
        mockMvc.perform(get("/api/workout/{id}", 13553151L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetWorkoutById_Web_Success() throws Exception {
        Workout testWorkout = saveTestWorkout();

        mockMvc.perform(get("/api/workout/{id}", testWorkout.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testWorkout.getId()));
    }

    @Test
    void testCreateNewWorkout_Success() {
        WorkoutCreationDTO workoutCreationDTO = WorkoutCreationDTO.builder()
                .workoutPlanId(workoutPlan.getId())
                .userId(user.getId())
                .build();

        ResponseEntity response = workoutController.createNewWorkout(workoutCreationDTO, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String[] location = response.getHeaders().getLocation().getPath().split("/");
        Long savedId = Long.valueOf(location[3]);

        assertThat(workoutRepository.findById(savedId)).isNotNull();
    }

    @Test
    void tetsCreateNewWorkout_WorkoutNotFound() {
        WorkoutCreationDTO workoutCreationDTO = WorkoutCreationDTO.builder()
                .workoutPlanId(555555555L)
                .userId(user.getId())
                .build();

        assertThrows(NotFoundException.class, () ->
                workoutController.createNewWorkout(workoutCreationDTO, user));
    }

    @Test
    void testCreateNewWorkout_NoAccessToWorkout() {
        WorkoutCreationDTO workoutCreationDTO = WorkoutCreationDTO.builder()
                .workoutPlanId(workoutPlan.getId())
                .userId(anotherUser.getId())
                .build();

        assertThrows(NoAccessException.class, () ->
                workoutController.createNewWorkout(workoutCreationDTO, anotherUser));
    }

    @Test
    void testAddNewSet_Success() {
        Workout workout = saveTestWorkout();
        ResponseEntity response = workoutController.addNewSet(exerciseSetDTO, workout.getId(), user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String[] location = response.getHeaders().getLocation().getPath().split("/");
        Long savedId = Long.valueOf(location[3]);

        assertThat(workoutRepository.findById(savedId)).isNotNull();

        Workout updated = workoutRepository.findById(savedId).get();

        assertThat(updated.getSets().size()).isEqualTo(1);
        assertThat(updated.getSets().getFirst().getExercise()).isEqualTo(exercise1);
    }

    @Test
    void testAddSet_WorkoutNotFound() {
        assertThrows(NotFoundException.class, () ->
                workoutController.addNewSet(exerciseSetDTO, 1313112344L, user));
    }

    @Test
    void testAddSet_NoAccessToWorkout() {
        Workout workout = saveTestWorkout();

        assertThrows(NoAccessException.class, () ->
                workoutController.addNewSet(exerciseSetDTO, workout.getId(), anotherUser));
    }

    @Test
    void testDeleteSet_Success() {
        Workout workout = saveTestWorkout();

        ExerciseSet exerciseSet = exerciseSetRepository.save(ExerciseSet.builder()
                        .user(user)
                        .workout(workout)
                        .exercise(exercise1)
                        .reps(8)
                        .weight(80.)
                .build());

        workout.getSets().add(exerciseSet);

        ResponseEntity response = workoutController.deleteSet(workout.getId(), exerciseSet.getId(), user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Workout updatedWorkout = workoutRepository.findById(workout.getId()).get();

        assertThat(updatedWorkout.getSets()).isEmpty();

        assertThat(exerciseSetRepository.findById(exerciseSet.getId())).isEmpty();
    }

    @Test
    void testDeleteSet_WorkoutNotFound() {
        assertThrows(NotFoundException.class, () ->
                workoutController.deleteSet(41241241L, 1L, user));
    }

    @Test
    void testDeleteSet_SetNotFound() {
        Workout workout = saveTestWorkout();

        assertThrows(NotFoundException.class, () ->
                workoutController.deleteSet(workout.getId(), 113531531513L, user));
    }

    @Test
    void testDeleteSet_NoAccessToWorkout() {
        Workout workout = saveTestWorkout();

        assertThrows(NoAccessException.class, () ->
                workoutController.deleteSet(workout.getId(), 1L, anotherUser));
    }

    @Test
    void testDeleteSet_NoAccessToSet() {
        Workout workout = saveTestWorkout();

        ExerciseSet exerciseSet = exerciseSetRepository.save(ExerciseSet.builder()
                .user(anotherUser)
                .workout(workout)
                .exercise(exercise1)
                .reps(8)
                .weight(80.)
                .build());

        workout.getSets().add(exerciseSet);

        assertThrows(NoAccessException.class, () ->
                workoutController.deleteSet(workout.getId(), exerciseSet.getId(), user));
    }

    @Test
    void testDeleteSet_SetNotFromThisWorkout() {
        Workout workout = saveTestWorkout();

        Workout workout2 = workoutRepository.save(Workout.builder()
                .user(user)
                .workoutPlan(workoutPlan)
                .sets(new ArrayList<>())
                .build());

        ExerciseSet exerciseSet = exerciseSetRepository.save(ExerciseSet.builder()
                .user(user)
                .workout(workout2)
                .exercise(exercise1)
                .reps(8)
                .weight(80.)
                .build());

        workout.getSets().add(exerciseSet);

        ResponseEntity response = workoutController.deleteSet(workout.getId(), exerciseSet.getId(), user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        assertThat(exerciseRepository.findById(exerciseSet.getId())).isNotEmpty();
    }

    @Test
    void testDeleteWorkoutById_Success() {
        Workout workout = saveTestWorkout();

        ExerciseSet exerciseSet = exerciseSetRepository.save(ExerciseSet.builder()
                .user(user)
                .workout(workout)
                .exercise(exercise1)
                .reps(8)
                .weight(80.)
                .build());

        workout.getSets().add(exerciseSet);

        ResponseEntity response = workoutController.deleteWorkout(workout.getId(), user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(workoutRepository.findById(workout.getId())).isEmpty();
        assertThat(exerciseSetRepository.findById(exerciseSet.getId())).isEmpty();
    }

    @Test
    void testDeleteWorkoutById_NotFound() {
        assertThrows(NotFoundException.class, () ->
                workoutController.getWorkoutById(4142141251L, user));
    }

    @Test
    void testDeleteWorkoutById_NoAccess() {
        Workout workout = saveTestWorkout();

        assertThrows(NoAccessException.class, () ->
                workoutController.getWorkoutById(workout.getId(), anotherUser));
    }

    @Test
    void testGetWorkoutById_Success() {
        Workout workout = saveTestWorkout();

        ExerciseSet exerciseSet = exerciseSetRepository.save(ExerciseSet.builder()
                .user(user)
                .workout(workout)
                .exercise(exercise1)
                .reps(8)
                .weight(80.)
                .build());

        ExerciseSet exerciseSet2 = exerciseSetRepository.save(ExerciseSet.builder()
                .user(user)
                .workout(workout)
                .exercise(exercise1)
                .reps(8)
                .weight(80.)
                .build());

        ExerciseSet exerciseSet3 = exerciseSetRepository.save(ExerciseSet.builder()
                .user(user)
                .workout(workout)
                .exercise(exercise2)
                .reps(8)
                .weight(80.)
                .build());

        workout.getSets().add(exerciseSet);
        workout.getSets().add(exerciseSet2);
        workout.getSets().add(exerciseSet3);

        WorkoutViewDTO workoutViewDTO = workoutController.getWorkoutById(workout.getId(), user);

        assertThat(workoutViewDTO).isNotNull();
        assertThat(workoutViewDTO.getWorkoutPlanDTO()).isNotNull();
        assertThat(workoutViewDTO.getUserId()).isEqualTo(user.getId());
        assertThat(workoutViewDTO.getExerciseSetMap().size()).isEqualTo(2);
    }

    @Test
    void testGetWorkoutById_NotFound() {
        assertThrows(NotFoundException.class, () ->
                workoutController.getWorkoutById(142142141L, user));
    }

    @Test
    void testGetWorkoutById_NoAccess() {
        Workout workout = saveTestWorkout();

        assertThrows(NoAccessException.class, () ->
                workoutController.getWorkoutById(workout.getId(), anotherUser));
    }

    @Test
    void testListWorkoutsOfUser_EmptyList() {
        workoutRepository.deleteAll();

        Page<WorkoutViewDTO> dtos = workoutController.listWorkouts(user, 1, 10);

        assertThat(dtos.getContent().size()).isEqualTo(0);
    }

    @Test
    void testListWorkoutsOfUser_20Workouts1Page() {
        for (int i = 0; i < 20; i++) {
            saveTestWorkout();
        }

        Page<WorkoutViewDTO> dtos = workoutController.listWorkouts(user, 1, 20);

        assertThat(dtos.getContent().size()).isEqualTo(20);
    }

    @Test
    void testListWorkoutsOfUser_25Workouts2ndPage() {
        for (int i = 0; i < 25; i++) {
            saveTestWorkout();
        }

        Page<WorkoutViewDTO> dtos = workoutController.listWorkouts(user, 2, 20);

        assertThat(dtos.getContent().size()).isEqualTo(5);
    }

    @Test
    void testListWorkoutsOfUser_Limit() {
        for (int i = 0; i < 1001; i++) {
            saveTestWorkout();
        }

        Page<WorkoutViewDTO> dtos = workoutController.listWorkouts(user, 1, 1001);

        assertThat(dtos.getContent().size()).isEqualTo(1000);
    }
}