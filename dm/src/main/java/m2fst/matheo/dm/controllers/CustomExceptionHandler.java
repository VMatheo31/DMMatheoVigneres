package m2fst.matheo.dm.controllers;

import lombok.extern.slf4j.Slf4j;
import m2fst.matheo.dm.dto.ErrorDto;
import m2fst.matheo.dm.exceptions.BadRequestException;
import m2fst.matheo.dm.exceptions.ForbiddenException;
import m2fst.matheo.dm.exceptions.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ErrorDto> handleNotFound(NotFoundException ex) {
                log.warn("Ressource non trouvée : {}", ex.getMessage());
                return ResponseEntity.status(404).body(
                                ErrorDto.builder()
                                                .code(404)
                                                .message(ex.getMessage())
                                                .build());
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorDto> handleBadRequest(BadRequestException ex) {
                log.warn("Requete invalide : {}", ex.getMessage());
                return ResponseEntity.status(400).body(
                                ErrorDto.builder()
                                                .code(400)
                                                .message(ex.getMessage())
                                                .build());
        }

        @ExceptionHandler({ ForbiddenException.class, AccessDeniedException.class })
        public ResponseEntity<ErrorDto> handleForbidden(RuntimeException ex) {
                log.warn("Acces refusé : {}", ex.getMessage());
                return ResponseEntity.status(403).body(
                                ErrorDto.builder()
                                                .code(403)
                                                .message(ex.getMessage())
                                                .build());
        }

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                String message = ex.getBindingResult().getFieldErrors().stream()
                                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                                .findFirst()
                                .orElse("Données de requete invalides");
                log.warn("Erreur de validation : {}", message);
                return ResponseEntity.status(400).body(
                                ErrorDto.builder()
                                                .code(400)
                                                .message(message)
                                                .build());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
                log.error("Erreur interne du serveur", ex);
                return ResponseEntity.status(500).body(
                                ErrorDto.builder()
                                                .code(500)
                                                .message("Erreur interne du serveur : " + ex.getMessage())
                                                .build());
        }
}
