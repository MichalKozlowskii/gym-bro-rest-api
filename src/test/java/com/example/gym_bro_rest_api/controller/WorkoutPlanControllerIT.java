package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.model.SetsReps;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import com.example.gym_bro_rest_api.repositories.WorkoutPlanrepository;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class WorkoutPlanControllerIT {
    @Autowired
    WorkoutPlanController workoutPlanController;

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

    User user1;
    User user2;
    Exercise testExercise;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        user1 = userRepository.save(User.builder()
                .username("test1")
                .password("password")
                .build());

        user2 = userRepository.save(User.builder()
                .username("test2")
                .password("password2")
                .build());

        testExercise = exerciseRepository.save(Exercise.builder()
                .user(user1)
                .name("1")
                .build());


        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user1, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    WorkoutPlan saveTestWorkoutPlan() {
        return workoutPlanrepository.save(WorkoutPlan.builder()
                .name("test")
                .exercises(new ArrayList<>(List.of(testExercise)))
                .user(user1)
                .setsReps(new ArrayList<>(List.of(new SetsReps(3, 8))))
                .build());
    }

    @Test
    void testListExercisesOfUser_Web_2ndPage_10size() throws Exception {
        for (int i = 0; i<20; i++) {
            saveTestWorkoutPlan();
        }

        mockMvc.perform(get("/api/workout-plan")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(10));
    }

    @Test
    void testListExercisesOfUser_Web_1page_20size() throws Exception {
        for (int i = 0; i<20; i++) {
            saveTestWorkoutPlan();
        }

        mockMvc.perform(get("/api/workout-plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(20));
    }

    @Test
    void testUpdateWorkoutPlanById_Web_ValidationFailed() throws Exception {
        WorkoutPlan testWorkoutPlan = saveTestWorkoutPlan();
        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name(" ")
                .build();


        mockMvc.perform(put("/api/workout-plan/{id}", testWorkoutPlan.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutPlanDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name must not be blank."));
    }

    @Test
    void testUpdateWorkoutPlanById_Web_NoAccess() throws Exception {
        WorkoutPlan testWorkoutPlan = saveTestWorkoutPlan();
        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("Plan A")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user2, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(put("/api/workout-plan/{id}", testWorkoutPlan.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutPlanDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateWorkoutPlanById_Web_NotFound() throws Exception {
        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("Plan A")
                .build();


        mockMvc.perform(put("/api/workout-plan/{id}", 1513513513153L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutPlanDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateWorkoutPlanById_Web_Success() throws Exception {
        WorkoutPlan testWorkoutPlan = saveTestWorkoutPlan();
        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("Plan A")
                .build();


        mockMvc.perform(put("/api/workout-plan/{id}", testWorkoutPlan.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutPlanDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/workout-plan/{id}", testWorkoutPlan.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testWorkoutPlan.getId()))
                .andExpect(jsonPath("$.name").value(workoutPlanDTO.getName()));
    }

    @Test
    void testCreateNewWorkoutPlan_Web_ValidationFailed() throws Exception {
        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name(" ")
                .build();

        mockMvc.perform(post("/api/workout-plan/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutPlanDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name must not be blank."));
    }

    @Test
    void testCreateNewWorkoutPlan_Web_Success() throws Exception {
        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                        .name("Plan A")
                                .build();

        mockMvc.perform(post("/api/workout-plan/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutPlanDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.success").value("Workout plan created."));
    }

    @Test
    void testGetWorkoutPlanById_Web_NoAccess() throws Exception {
        WorkoutPlan testWorkoutPlan = saveTestWorkoutPlan();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user2, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/workout-plan/{id}", testWorkoutPlan.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetWorkoutPlanById_Web_NotFound() throws Exception {
        mockMvc.perform(get("/api/workout-plan/{id}", 13553151L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetWorkoutPlanById_Web_Success() throws Exception {
        WorkoutPlan testWorkoutPlan = saveTestWorkoutPlan();

        mockMvc.perform(get("/api/workout-plan/{id}", testWorkoutPlan.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testWorkoutPlan.getId()))
                .andExpect(jsonPath("$.name").value(testWorkoutPlan.getName()));
    }

    @Test
    void testListExercisesOfUser_Limit() {
        for (int i = 0; i < 1001; i++) {
            saveTestWorkoutPlan();
        }

        Page<WorkoutPlanDTO> dtos = workoutPlanController.listWorkoutPlansOfUser(user1, 1, 1001);

        assertThat(dtos.getContent().size()).isEqualTo(1000);
    }

    @Test
    void testListExercisesOfUser_25Exercises2ndPage() {
        for (int i = 0; i < 25; i++) {
            saveTestWorkoutPlan();
        }

        Page<WorkoutPlanDTO> dtos = workoutPlanController.listWorkoutPlansOfUser(user1, 2, 20);

        assertThat(dtos.getContent().size()).isEqualTo(5);
    }

    @Test
    void testListExercisesOfUser_20Exercises1Page() {
        for (int i = 0; i < 20; i++) {
            saveTestWorkoutPlan();
        }

        Page<WorkoutPlanDTO> dtos = workoutPlanController.listWorkoutPlansOfUser(user1, 1, 20);

        assertThat(dtos.getContent().size()).isEqualTo(20);
    }

    @Test
    void testListExercisesOfUser_EmptyList() {
        Page<WorkoutPlanDTO> dtos = workoutPlanController.listWorkoutPlansOfUser(user1, 1, 10);

        assertThat(dtos.getContent().size()).isEqualTo(0);
    }

    @Test
    void testDeleteWorkoutPlanById_NoAccess() {
        WorkoutPlan workoutPlan = saveTestWorkoutPlan();

        assertThrows(NoAccessException.class, () ->
                workoutPlanController.deleteWorkoutPlanById(workoutPlan.getId(), user2));
    }

    @Test
    void testDeleteWorkoutPlanById_NotFound() {
        assertThrows(NotFoundException.class, () ->
                workoutPlanController.deleteWorkoutPlanById(41242141L, user1));
    }

    @Test
    void testDeleteWorkoutPlanById_Success() {
        WorkoutPlan workoutPlan = saveTestWorkoutPlan();

        ResponseEntity response = workoutPlanController.deleteWorkoutPlanById(workoutPlan.getId(), user1);

        assertThat(workoutPlanrepository.findById(workoutPlan.getId())).isEmpty();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testUpdateWorkoutPlanById_NoAccess() {
        WorkoutPlan workoutPlan = saveTestWorkoutPlan();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("dwdawda")
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        assertThrows(NoAccessException.class, () ->
                workoutPlanController.updateWorkoutPlanById(workoutPlan.getId(), workoutPlanDTO, user2));
    }

    @Test
    void testUpdateWorkoutPlanById_NotFound() {
        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("workoutplan")
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        System.out.println(workoutPlanDTO.getName());

        assertThrows(NotFoundException.class, () ->
                workoutPlanController.updateWorkoutPlanById(3421421L, workoutPlanDTO, user1));
    }

    @Test
    void testUpdateWorkoutPlanById_Success() {
        WorkoutPlan workoutPlan = saveTestWorkoutPlan();

        ExerciseDTO testExerciseDto = ExerciseDTO.builder()
                .id(testExercise.getId())
                .build();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("test_updated")
                .exercises(new ArrayList<>(List.of(testExerciseDto)))
                .setsReps(new ArrayList<>(List.of(new SetsReps(3, 8))))
                .build();

        ResponseEntity<Map<String, String>> response = workoutPlanController.updateWorkoutPlanById(
                workoutPlan.getId(), workoutPlanDTO, user1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        WorkoutPlan updated = workoutPlanrepository.findById(workoutPlan.getId()).get();

        assertThat(updated.getName()).isEqualTo(workoutPlanDTO.getName());
    }

    @Test
    void testGetWorkoutPlanById_NoAccess() {
        WorkoutPlan saved = workoutPlanrepository.save(WorkoutPlan.builder().name("1").user(user1).build());

        assertThrows(NoAccessException.class, () -> workoutPlanController.getWorkoutPlanById(saved.getId(), user2));
    }

    @Test
    void testGetWorkoutPlanById_NotFound() {
        assertThrows(NotFoundException.class, () -> workoutPlanController.getWorkoutPlanById(13241L, user1));
    }

    @Test
    void testGetWorkoutPlanById_Success() {
        WorkoutPlan saved = workoutPlanrepository.save(WorkoutPlan.builder().name("1").user(user1).build());

        WorkoutPlanDTO workoutPlanDTO = workoutPlanController.getWorkoutPlanById(saved.getId(), user1);

        assertThat(workoutPlanDTO).isNotNull();
        assertThat(workoutPlanDTO.getUserId()).isEqualTo(user1.getId());
    }

    @Test
    void testSaveNewWorkoutPlan_ExerciseNoAccess() {
        ExerciseDTO testExerciseDto = ExerciseDTO.builder()
                .id(testExercise.getId())
                .build();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("test")
                .exercises(List.of(testExerciseDto))
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        assertThrows(NoAccessException.class, () -> workoutPlanController.createNewWorkoutPlan(workoutPlanDTO, user2));
    }

    @Test
    void testSaveNewWorkoutPlan_ExerciseNotFound() {
        ExerciseDTO testExerciseDto = ExerciseDTO.builder()
                .id(14214L)
                .build();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("test")
                .exercises(List.of(testExerciseDto))
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        assertThrows(NotFoundException.class, () -> workoutPlanController.createNewWorkoutPlan(workoutPlanDTO, user1));
    }
    
    @Test
    void testSaveNewWorkoutPlan_Success() {
        ExerciseDTO testExerciseDto = ExerciseDTO.builder()
                .id(testExercise.getId())
                .build();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("workout_plan1")
                .exercises(List.of(testExerciseDto))
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        ResponseEntity<Map<String, String>> response = workoutPlanController.createNewWorkoutPlan(workoutPlanDTO, user1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String[] location = response.getHeaders().getLocation().getPath().split("/");
        Long savedId = Long.valueOf(location[3]);

        assertThat(workoutPlanrepository.findById(savedId)).isNotNull();
    }
}