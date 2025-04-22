package com.inkspire.inkspire.controller;

import com.inkspire.inkspire.model.LearningPlan;
import com.inkspire.inkspire.model.Milestone;
import com.inkspire.inkspire.model.Reminder;
import com.inkspire.inkspire.model.User;
import com.inkspire.inkspire.payload.ErrorResponse;
import com.inkspire.inkspire.payload.LearningPlanResponse;
import com.inkspire.inkspire.service.LearningPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ContentDisposition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/learning-plans")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class LearningPlanController {
    private final LearningPlanService learningPlanService;
    private static final Logger logger = LoggerFactory.getLogger(LearningPlanController.class);
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public LearningPlanController(LearningPlanService learningPlanService) {
        this.learningPlanService = learningPlanService;
    }

    @PostMapping
    public ResponseEntity<?> createPlan(
            @RequestBody LearningPlan plan,
            @AuthenticationPrincipal User user) {
        try {
            logger.info("Received create plan request with isPublic: {}", plan.isPublic());
            plan.setUser(user);
            boolean isPublic = plan.isPublic();
            plan.setPublic(isPublic);
            LearningPlan createdPlan = learningPlanService.createLearningPlan(plan, user.getId());
            logger.info("Created plan with ID: {} and isPublic: {}", createdPlan.getId(), createdPlan.isPublic());
            
            LearningPlanResponse response = new LearningPlanResponse();
            response.setId(createdPlan.getId());
            response.setTitle(createdPlan.getTitle());
            response.setDescription(createdPlan.getDescription());
            response.setMilestones(createdPlan.getMilestones());
            response.setLearningMaterials(createdPlan.getLearningMaterials());
            response.setCreatedAt(createdPlan.getCreatedAt());
            response.setUpdatedAt(createdPlan.getUpdatedAt());
            response.setPublic(createdPlan.isPublic());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating plan: ", e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Failed to create plan: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<LearningPlanResponse>> getAllPlans(@AuthenticationPrincipal User user) {
        try {
            List<LearningPlan> plans = learningPlanService.getPlansByUser(user.getId());
            List<LearningPlanResponse> responses = plans.stream()
                .map(plan -> {
                    LearningPlanResponse response = new LearningPlanResponse();
                    response.setId(plan.getId());
                    response.setTitle(plan.getTitle());
                    response.setDescription(plan.getDescription());
                    response.setMilestones(plan.getMilestones());
                    response.setLearningMaterials(plan.getLearningMaterials());
                    response.setCreatedAt(plan.getCreatedAt());
                    response.setUpdatedAt(plan.getUpdatedAt());
                    
                    // Explicitly log and set the public status
                    logger.info("Plan {} isPublic value from database: {}", plan.getId(), plan.isPublic());
                    response.setPublic(plan.isPublic());
                    
                    LearningPlanResponse.UserSummary userSummary = new LearningPlanResponse.UserSummary();
                    userSummary.setId(user.getId());
                    userSummary.setName(user.getName());
                    userSummary.setEmail(user.getEmail());
                    response.setUser(userSummary);
                    
                    return response;
                })
                .collect(Collectors.toList());
            
            // Log the response
            logger.info("Sending response with {} plans", responses.size());
            responses.forEach(r -> logger.info("Plan {} isPublic: {}", r.getId(), r.isPublic()));
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            logger.error("Error fetching plans: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningPlan> getPlan(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(learningPlanService.getLearningPlan(id, user.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlan(
            @PathVariable Long id,
            @RequestBody LearningPlan updatedPlan,
            @AuthenticationPrincipal User user) {
        try {
            LearningPlan existingPlan = learningPlanService.getLearningPlan(id, user.getId());
            
            logger.info("Updating plan ID: {} with isPublic: {}", id, updatedPlan.isPublic());
            
            existingPlan.setTitle(updatedPlan.getTitle());
            existingPlan.setDescription(updatedPlan.getDescription());
            existingPlan.setPublic(updatedPlan.isPublic()); // Ensure isPublic is set
            
            if (updatedPlan.getMilestones() != null) {
                existingPlan.getMilestones().clear();
                updatedPlan.getMilestones().forEach(milestone -> {
                    milestone.setCompleted(milestone.isCompleted());
                    existingPlan.getMilestones().add(milestone);
                });
            }
            
            existingPlan.setUpdatedAt(LocalDateTime.now());
            
            LearningPlan savedPlan = learningPlanService.updateLearningPlan(existingPlan);
            
            logger.info("Updated plan ID: {} with isPublic: {}", savedPlan.getId(), savedPlan.isPublic());
            
            // Return the full updated plan to ensure frontend gets the latest isPublic
            LearningPlanResponse response = new LearningPlanResponse();
            response.setId(savedPlan.getId());
            response.setTitle(savedPlan.getTitle());
            response.setDescription(savedPlan.getDescription());
            response.setMilestones(savedPlan.getMilestones());
            response.setLearningMaterials(savedPlan.getLearningMaterials());
            response.setCreatedAt(savedPlan.getCreatedAt());
            response.setUpdatedAt(savedPlan.getUpdatedAt());
            response.setPublic(savedPlan.isPublic());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating plan: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        learningPlanService.getLearningPlan(id, user.getId());
        learningPlanService.deleteLearningPlan(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<LearningPlan>> getRecommendedPlans(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(learningPlanService.getRecommendedPlans(user.getId()));
    }

    @PostMapping("/reminders")
    public ResponseEntity<Reminder> createReminder(
            @RequestBody Reminder reminder,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(learningPlanService.createReminder(reminder, user.getId()));
    }

    @GetMapping("/reminders")
    public ResponseEntity<?> getReminders(@AuthenticationPrincipal User user) {
        try {
            logger.info("Fetching reminders for user: {}", user.getId());
            List<Reminder> reminders = learningPlanService.getProgressReminders(user.getId());
            
            // Convert reminders to DTOs to avoid serialization issues
            List<Map<String, Object>> reminderDTOs = reminders.stream()
                .map(reminder -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", reminder.getId());
                    dto.put("message", reminder.getMessage());
                    dto.put("dueDate", reminder.getDueDate());
                    dto.put("createdAt", reminder.getCreatedAt());
                    dto.put("completed", reminder.isCompleted());
                    dto.put("planId", reminder.getPlan().getId());
                    dto.put("planTitle", reminder.getPlan().getTitle());
                    return dto;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(reminderDTOs);
        } catch (Exception e) {
            logger.error("Error fetching reminders: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch reminders: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/reminders/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        learningPlanService.deleteReminder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{planId}/milestones/{milestoneId}")
    public ResponseEntity<LearningPlan> updateMilestoneProgress(
            @PathVariable Long planId,
            @PathVariable Long milestoneId,
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal User user) {
        learningPlanService.getLearningPlan(planId, user.getId());
        boolean completed = (boolean) request.get("completed");
        String notes = (String) request.get("notes");
        return ResponseEntity.ok(learningPlanService.updateMilestoneProgress(planId, milestoneId, completed, notes));
    }

    @PostMapping("/{planId}/materials")
    public ResponseEntity<?> uploadMaterial(
            @PathVariable Long planId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        try {
            logger.info("Starting file upload for plan: {}", planId);
            LearningPlan plan = learningPlanService.getLearningPlan(planId, user.getId());
            
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create upload directory");
                }
            }
            
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "file";
            }
            
            // Generate a unique filename while preserving the original extension
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }
            
            String uniqueFileName = System.currentTimeMillis() + "_" + 
                originalFilename.substring(0, lastDotIndex > 0 ? lastDotIndex : originalFilename.length())
                    .replaceAll("[^a-zA-Z0-9]", "_") + extension;
            
            String filePath = UPLOAD_DIR + uniqueFileName;
            logger.info("Saving file to: {}", filePath);
            
            File destFile = new File(filePath);
            file.transferTo(destFile);
            
            List<String> materials = plan.getLearningMaterials();
            if (materials == null) {
                materials = new ArrayList<>();
                plan.setLearningMaterials(materials);
            }
            materials.add(uniqueFileName);
            learningPlanService.updateLearningPlan(plan);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Material uploaded successfully");
            response.put("fileName", uniqueFileName);
            response.put("originalFileName", originalFilename);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error uploading file: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/{planId}/materials/{index}")
    public ResponseEntity<byte[]> downloadMaterial(
            @PathVariable Long planId,
            @PathVariable int index,
            @AuthenticationPrincipal User user) {
        try {
            logger.info("Attempting to download material for plan: {}, index: {}", planId, index);
            LearningPlan plan = learningPlanService.getLearningPlan(planId, user.getId());
            
            if (plan.getLearningMaterials() == null || index >= plan.getLearningMaterials().size()) {
                logger.error("Material not found at index: {}", index);
                return ResponseEntity.notFound().build();
            }
            
            String fileName = plan.getLearningMaterials().get(index);
            String filePath = UPLOAD_DIR + fileName;
            logger.info("Attempting to read file from: {}", filePath);
            
            File file = new File(filePath);
            if (!file.exists()) {
                logger.error("File does not exist at path: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            // Get the file's content type
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                // If content type cannot be determined, fall back to application/octet-stream
                contentType = "application/octet-stream";
            }
            
            byte[] fileContent = Files.readAllBytes(file.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            
            // Set content disposition based on file type
            if (contentType.startsWith("image/") || contentType.equals("application/pdf")) {
                // For images and PDFs, allow inline display
                headers.setContentDisposition(ContentDisposition.inline()
                        .filename(fileName)
                        .build());
            } else {
                // For other files, force download
                headers.setContentDisposition(ContentDisposition.attachment()
                        .filename(fileName)
                        .build());
            }
            
            logger.info("Sending file {} with content type: {}", fileName, contentType);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } catch (IOException e) {
            logger.error("Error downloading file: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{planId}/materials/{index}")
    public ResponseEntity<String> deleteMaterial(
            @PathVariable Long planId,
            @PathVariable int index,
            @AuthenticationPrincipal User user) {
        try {
            LearningPlan plan = learningPlanService.getLearningPlan(planId, user.getId());
            if (plan.getLearningMaterials() == null || index >= plan.getLearningMaterials().size()) {
                logger.warn("Material not found at index {} for plan {}", index, planId);
                return ResponseEntity.notFound().build();
            }
            String fileName = plan.getLearningMaterials().get(index);
            String filePath = UPLOAD_DIR + fileName; // Prepend UPLOAD_DIR to the filename
            File file = new File(filePath);
            if (file.exists() && file.delete()) {
                plan.getLearningMaterials().remove(index);
                learningPlanService.updateLearningPlan(plan);
                logger.info("Material deleted successfully: {}", filePath);
                return ResponseEntity.ok("Material deleted");
            } else {
                logger.error("Failed to delete file or file not found: {}", filePath);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to delete file from server");
            }
        } catch (Exception e) {
            logger.error("Error deleting material for plan {} at index {}: ", planId, index, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete material: " + e.getMessage());
        }
    }

    @PutMapping("/{planId}/milestones/{milestoneId}/status")
    public ResponseEntity<?> updateMilestoneStatus(
            @PathVariable Long planId,
            @PathVariable Long milestoneId,
            @RequestBody Map<String, Boolean> request,
            @AuthenticationPrincipal User user) {
        try {
            Boolean completed = request.get("completed");
            if (completed == null) {
                throw new IllegalArgumentException("Completed status is required");
            }

            LearningPlan plan = learningPlanService.getLearningPlan(planId, user.getId());
            boolean updated = false;

            for (Milestone milestone : plan.getMilestones()) {
                if (milestone.getId().equals(milestoneId)) {
                    milestone.setCompleted(completed);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                return ResponseEntity.notFound().build();
            }

            LearningPlan updatedPlan = learningPlanService.updateLearningPlan(plan);
            return ResponseEntity.ok(updatedPlan);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

  

    private LearningPlanResponse convertToResponse(LearningPlan plan) {
        LearningPlanResponse response = new LearningPlanResponse();
        response.setId(plan.getId());
        response.setTitle(plan.getTitle());
        response.setDescription(plan.getDescription());
        response.setMilestones(plan.getMilestones());
        response.setLearningMaterials(plan.getLearningMaterials());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());
        response.setPublic(plan.isPublic());
        
        LearningPlanResponse.UserSummary userSummary = new LearningPlanResponse.UserSummary();
        userSummary.setId(plan.getUser().getId());
        userSummary.setName(plan.getUser().getName());
        userSummary.setEmail(plan.getUser().getEmail());
        response.setUser(userSummary);
        
        return response;
    }
}

class ProgressUpdate {
    private boolean completed;
    private String notes;

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}