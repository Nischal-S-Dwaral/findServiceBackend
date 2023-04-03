package org.msc.web.dev.exceptions.handler;

import org.msc.web.dev.exceptions.BadRequest;
import org.msc.web.dev.exceptions.Forbidden;
import org.msc.web.dev.exceptions.JsonException;
import org.msc.web.dev.exceptions.Unauthorized;
import org.msc.web.dev.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.*;

/**
 * custom exception handler controller Advice which handles all type of exception and returns custom error messages
 */
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static final String CLIENT_ERROR = "CLIENT_ERROR";
    private static final String SERVER_ERROR = "SERVER_ERROR";
    private static final String VAGUE_ERROR_MESSAGE = "Sorry, something failed.";

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(Unauthorized.class)
    @ResponseBody
    public ErrorResponse handleUnauthorized(HttpServletRequest request, Exception exception) {
        return ErrorResponse.Builder.anError()
                .withStatus(UNAUTHORIZED.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(exception.getMessage())
                .build();
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(Forbidden.class)
    @ResponseBody
    public ErrorResponse handleForbidden(HttpServletRequest request, Exception exception) {
        return ErrorResponse.Builder.anError()
                .withStatus(FORBIDDEN.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(exception.getMessage())
                .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    public ErrorResponse handleMultipartException(HttpServletRequest httpServletRequest, MultipartException e) {
        log.error("Multipart resolution failed with message : {} and cause: {}", e.getMessage(), e.getMostSpecificCause());
        return ErrorResponse.Builder.anError()
                .withStatus(INTERNAL_SERVER_ERROR.value())
                .withUrl(httpServletRequest.getRequestURL().toString())
                .withMessage(SERVER_ERROR)
                .withDescription(VAGUE_ERROR_MESSAGE)
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequest.class)
    @ResponseBody
    public ErrorResponse handleBadRequest(HttpServletRequest request, BadRequest exception) {
        log.error("Bad request error ", exception);
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(exception.getMessage())
                .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public ErrorResponse handleHttpClientErrorException(HttpServletRequest httpServletRequest,
                                                        HttpClientErrorException e) {
        log.error("Downstream call failed with status: {} and response: {} with the error message as: {}",
                e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
        return ErrorResponse.Builder.anError()
                .withStatus(INTERNAL_SERVER_ERROR.value())
                .withUrl(httpServletRequest.getRequestURL().toString())
                .withMessage(SERVER_ERROR)
                .withDescription(VAGUE_ERROR_MESSAGE)
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DateTimeParseException.class)
    @ResponseBody
    public ErrorResponse handleInvalidDateTime(HttpServletRequest httpServletRequest,
                                               DateTimeParseException e) {
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(httpServletRequest.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(e.getParsedString())
                .build();
    }

    @ResponseStatus(METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ErrorResponse handleMethodNotAllowed(HttpServletRequest request, Exception exception) {
        return ErrorResponse.Builder.anError()
                .withStatus(METHOD_NOT_ALLOWED.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(exception.getMessage())
                .build();
    }

    @ResponseStatus(UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public ErrorResponse handleMediaTypeNotSupported(HttpServletRequest request,
                                                     Exception exception) {
        return ErrorResponse.Builder.anError()
                .withStatus(UNSUPPORTED_MEDIA_TYPE.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(exception.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValid(HttpServletRequest request,
                                                      MethodArgumentNotValidException exception) {

        String description = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> format("{0} {1}", error.getField(), error.getDefaultMessage())).collect(
                        Collectors.joining(", "));
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(description)
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ErrorResponse handleMessageNotReadable(HttpServletRequest request) {
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription("Http message was not readable")
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ErrorResponse handleMethodTypeNotValid(HttpServletRequest request,
                                                  MethodArgumentTypeMismatchException exception) {

        String description = String.format("Parameter value '%s' is not valid for request parameter '%s'",
                exception.getValue(), exception.getName());
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(description)
                .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpServerErrorException.class)
    @ResponseBody
    public ErrorResponse handleHttpServerErrorException(HttpServletRequest httpServletRequest,
                                                        HttpServerErrorException e) {
        log.error("Request failed with status: {} and response: {}, with error message: {}",
                e.getStatusCode(), e.getResponseBodyAsString(), e.getMessage());
        return ErrorResponse.Builder.anError()
                .withStatus(INTERNAL_SERVER_ERROR.value())
                .withUrl(httpServletRequest.getRequestURL().toString())
                .withMessage(SERVER_ERROR)
                .withDescription(VAGUE_ERROR_MESSAGE)
                .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RestClientException.class)
    @ResponseBody
    public ErrorResponse handleRestClientException(HttpServletRequest httpServletRequest, RestClientException e) {
        log.error("RestClient call failed with message : {} and cause: {}", e.getMessage(), e.getMostSpecificCause());
        return ErrorResponse.Builder.anError().withStatus(INTERNAL_SERVER_ERROR.value())
                .withUrl(httpServletRequest.getRequestURL().toString())
                .withMessage(SERVER_ERROR)
                .withDescription(e.getMessage())
                .build();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    @ResponseBody
    public ErrorResponse handleUnsatisfiedParameter(HttpServletRequest request,
                                                    UnsatisfiedServletRequestParameterException exception) {

        String unsatisfiedConditions = Stream.of(exception.getParamConditions())
                .collect(joining(","));
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(
                        format("Parameter conditions not met for request: {0}", unsatisfiedConditions))
                .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ErrorResponse catchAllHandler(HttpServletRequest request, Throwable ex) {
        log.error("Unexpected error handled", ex);
        return ErrorResponse.Builder.anError()
                .withStatus(INTERNAL_SERVER_ERROR.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(SERVER_ERROR)
                .withDescription(VAGUE_ERROR_MESSAGE)
                .build();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(JsonException.class)
    @ResponseBody
    public ErrorResponse handleJsonException(HttpServletRequest request, Throwable ex) {
        log.error("Unexpected error handled", ex);
        return ErrorResponse.Builder.anError()
                .withStatus(BAD_REQUEST.value())
                .withUrl(request.getRequestURL().toString())
                .withMessage(CLIENT_ERROR)
                .withDescription(VAGUE_ERROR_MESSAGE)
                .build();
    }
}
