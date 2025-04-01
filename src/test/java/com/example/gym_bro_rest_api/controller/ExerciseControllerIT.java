package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.common.contenttype.ContentType;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class ExerciseControllerIT {
    @Autowired
    ExerciseController exerciseController;

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

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user1, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    Exercise saveTestExercise() {
        Exercise testExercise = Exercise.builder()
                .user(user1)
                .name("1")
                .build();

        return exerciseRepository.save(testExercise);
    }

    @Test
    void testListExercisesOfUser_Web_2ndPage_10size() throws Exception {
        for (int i = 0; i<20; i++) {
            saveTestExercise();
        }

        mockMvc.perform(get("/api/exercise")
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
            saveTestExercise();
        }

        mockMvc.perform(get("/api/exercise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(20));
    }

    @Test
    void testUpdateExerciseById_Web_ValidationFailed() throws Exception {
        Exercise testExercise = saveTestExercise();
        ExerciseDTO exerciseDTO = ExerciseDTO.builder().name(" ").demonstrationUrl("12321").build();

        mockMvc.perform(put("/api/exercise/{id}", testExercise.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name must not be blank."));
        ;
    }

    @Test
    void testUpdateExerciseById_Web_NoAccess() throws Exception {
        Exercise testExercise = saveTestExercise();
        ExerciseDTO exerciseDTO = ExerciseDTO.builder().name("Exercise 11").demonstrationUrl("12321").build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user2, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(put("/api/exercise/{id}", testExercise.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    void testUpdateExerciseById_Web_NotFound() throws Exception {
        ExerciseDTO exerciseDTO = ExerciseDTO.builder().name("Exercise 11").demonstrationUrl("12321").build();

        mockMvc.perform(put("/api/exercise/{id}", 543252352L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateExerciseById_Web_Success() throws Exception {
        Exercise testExercise = saveTestExercise();
        ExerciseDTO exerciseDTO = ExerciseDTO.builder().name("Exercise 11").demonstrationUrl("12321").build();

        mockMvc.perform(put("/api/exercise/{id}", testExercise.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/exercise/{id}", testExercise.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testExercise.getId()))
                .andExpect(jsonPath("$.demonstrationUrl").value(testExercise.getDemonstrationUrl()))
                .andExpect(jsonPath("$.name").value(exerciseDTO.getName()));

    }

    @Test
    void testCreateNewExercise_Web_ValidationFailed() throws Exception {
        ExerciseDTO exerciseDTO = ExerciseDTO.builder().name(" ").build();

        mockMvc.perform(post("/api/exercise/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name must not be blank."));
    }

    @Test
    void testCreateNewExercise_Web_Success() throws Exception {
        ExerciseDTO exerciseDTO = ExerciseDTO.builder().name("exercise 1").build();

        mockMvc.perform(post("/api/exercise/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(exerciseDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.success").value("Exercise created."));
    }

    @Test
    void testGetExerciseById_Web_NoAccess() throws Exception {
        Exercise testExercise = saveTestExercise(); // Exercise belongs to user1

        // Explicitly authenticate as user2
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user2, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/exercise/{id}", testExercise.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetExerciseById_Web_NotFound() throws Exception {
        mockMvc.perform(get("/api/exercise/{id}", 13553151L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetExerciseById_Web_Success() throws Exception {
        Exercise testExercise = saveTestExercise();

        mockMvc.perform(get("/api/exercise/{id}", testExercise.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testExercise.getId()))
                .andExpect(jsonPath("$.name").value(testExercise.getName()));
    }

    @Test
    void testListExercisesOfUser_Limit() {
        for (int i = 0; i < 1001; i++) {
            saveTestExercise();
        }

        Page<ExerciseDTO> dtos = exerciseController.listExercisesOfUser(user1, 1, 1001);

        assertThat(dtos.getContent().size()).isEqualTo(1000);
    }

    @Test
    void testListExercisesOfUser_20Exercises1Page() {
        for (int i = 0; i < 20; i++) {
            saveTestExercise();
        }

        Page<ExerciseDTO> dtos = exerciseController.listExercisesOfUser(user1, 1, 20);

        assertThat(dtos.getContent().size()).isEqualTo(20);
    }

    @Test
    void testDeleteExerciseById_NoAccess() {
        Exercise exercise = saveTestExercise();

        assertThrows(NoAccessException.class, () -> {
           exerciseController.deleteExerciseById(exercise.getId(), user2);
        });
    }

    @Test
    void testDeleteExerciseById_NotFound() {
        assertThrows(NotFoundException.class, () -> {
           exerciseController.deleteExerciseById(1321414L, user1);
        });
    }

    @Test
    void testDeleteExerciseById() {
        Exercise existing = saveTestExercise();

        ResponseEntity response = exerciseController.deleteExerciseById(existing.getId(), user1);

        assertThat(exerciseRepository.existsById(existing.getId())).isEqualTo(false);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testUpdateExerciseById_NoAccess() {
        Exercise existing = saveTestExercise();

        ExerciseDTO updateDto = ExerciseDTO.builder()
                .name("2")
                .demonstrationUrl("fakpofkaepof")
                .build();

        assertThrows(NoAccessException.class, () -> {
           exerciseController.updateExerciseById(existing.getId(), updateDto, user2);
        });
    }

    @Test
    void testUpdateExerciseById_NotFound() {
        ExerciseDTO updateDto = ExerciseDTO.builder()
                .name("2")
                .demonstrationUrl("fakpofkaepof")
                .build();

        assertThrows(NotFoundException.class, () -> {
            exerciseController.updateExerciseById(1321049L, updateDto, user1);
        });
    }

    @Test
    void testUpdateExerciseById() {
        Exercise existing = saveTestExercise();

        ExerciseDTO updateDto = ExerciseDTO.builder()
                .name("2")
                .demonstrationUrl("fakpofkaepof")
                .build();

        ResponseEntity response = exerciseController.updateExerciseById(existing.getId(), updateDto, user1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Exercise updated = exerciseRepository.findById(existing.getId()).orElse(null);

        assertThat(updated.getName()).isEqualTo(updateDto.getName());
        assertThat(updated.getDemonstrationUrl()).isEqualTo(updateDto.getDemonstrationUrl());
    }

    @Test
    void testGetExerciseById_NoAccess() {
        Exercise exercise = saveTestExercise();
        assertThrows(NoAccessException.class, () -> {
            exerciseController.getExerciseById(exercise.getId(), user2);
        });
    }

    @Test
    void testGetExerciseById_NotFound() {
        assertThrows(NotFoundException.class, () -> {
            exerciseController.getExerciseById(1321313L, user1);
        });
    }

    @Test
    void testGetExerciseById() {
        Exercise exercise = saveTestExercise();

        ExerciseDTO exerciseDto =exerciseController.getExerciseById(exercise.getId(), user1);

        assertThat(exerciseDto).isNotNull();
        assertThat(exerciseDto.getName()).isEqualTo("1");
        assertThat(exerciseDto.getUserId()).isNotNull();
    }

    @Test
    void testCreateNewExercise_NameOK() {
        ExerciseDTO testDto = ExerciseDTO.builder()
                .name("exercise1")
                .build();

        ResponseEntity<Map<String, String>> response = exerciseController.createNewExercise(testDto, user1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String[] location = response.getHeaders().getLocation().getPath().split("/");
        Long savedId = Long.valueOf(location[3]);

        assertThat(exerciseRepository.findById(savedId)).isNotNull();
    }

    @Test
    void testCreateNewExercise_BadName() {
        ExerciseDTO testDto = ExerciseDTO.builder()
                .name("")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            exerciseController.createNewExercise(testDto, user1);
        });

        System.out.println(exception.getMessage());
    }
}