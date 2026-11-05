package m2fst.matheo.dm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import m2fst.matheo.dm.dto.EvaluationCreateDto;
import m2fst.matheo.dm.dto.EvaluationDto;
import m2fst.matheo.dm.dto.ImagePutDto;
import m2fst.matheo.dm.services.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/restaurants/{id}/evaluations")
    public ResponseEntity<EvaluationDto> addEvaluation(
            @PathVariable(name = "id") int id,
            @Valid @RequestBody EvaluationCreateDto evaluation,
            @AuthenticationPrincipal Jwt jwt) {
        String nomEvaluateur = getNomUtilisateur(jwt);
        return ResponseEntity.ok(this.evaluationService.addEvaluation(
                id,
                nomEvaluateur,
                evaluation.getCommentaire(),
                evaluation.getNote(),
                evaluation.getPhotos()
        ));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/evaluations/{evaluationId}")
    public ResponseEntity<EvaluationDto> updateEvaluation(
            @PathVariable(name = "evaluationId") int evaluationId,
            @Valid @RequestBody EvaluationCreateDto evaluation,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        String nomUtilisateur = getNomUtilisateur(jwt);
        boolean admin = isAdmin(authentication);
        return ResponseEntity.ok(this.evaluationService.updateEvaluation(
                evaluationId,
                evaluation.getCommentaire(),
                evaluation.getNote(),
                evaluation.getPhotos(),
                nomUtilisateur,
                admin
        ));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/evaluations/{evaluationId}")
    public ResponseEntity<Void> deleteEvaluation(
            @PathVariable(name = "evaluationId") int evaluationId,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        String nomUtilisateur = getNomUtilisateur(jwt);
        boolean admin = isAdmin(authentication);
        this.evaluationService.deleteEvaluation(evaluationId, nomUtilisateur, admin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/evaluations")
    public List<EvaluationDto> getEvaluationsByKeywords(@RequestParam(required = false) List<String> keywords) {
        return this.evaluationService.getEvaluationsByKeywords(keywords);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/evaluations/me")
    public List<EvaluationDto> getMyEvaluations(@AuthenticationPrincipal Jwt jwt) {
        String nomUtilisateur = getNomUtilisateur(jwt);
        return this.evaluationService.getEvaluationsByUser(nomUtilisateur);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/evaluations/{evaluationId}/photo")
    public ResponseEntity<ImagePutDto> uploadPhoto(
            @PathVariable(name = "evaluationId") int evaluationId,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {
        String nomUtilisateur = getNomUtilisateur(jwt);
        boolean admin = isAdmin(authentication);
        return ResponseEntity.ok(ImagePutDto.fromEntity(
                this.evaluationService.uploadPhoto(evaluationId, nomUtilisateur, admin)
        ));
    }

    private String getNomUtilisateur(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        if (username != null) {
            return username;
        }
        return jwt.getSubject();
    }

    private boolean isAdmin(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }
}

