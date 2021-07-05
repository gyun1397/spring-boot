//package com.domain.common.context;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//import javax.persistence.RollbackException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.ConstraintViolation;
//import javax.validation.ConstraintViolationException;
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.springframework.beans.propertyeditors.CustomDateEditor;
//import org.springframework.beans.propertyeditors.StringTrimmerEditor;
//import org.springframework.core.convert.ConversionFailedException;
//import org.springframework.data.mapping.PropertyReferenceException;
//import org.springframework.data.rest.core.RepositoryConstraintViolationException;
//import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;
//import org.springframework.data.rest.webmvc.ResourceNotFoundException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.transaction.CannotCreateTransactionException;
//import org.springframework.transaction.TransactionSystemException;
//import org.springframework.web.bind.MissingServletRequestParameterException;
//import org.springframework.web.bind.WebDataBinder;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.InitBinder;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
//import org.springframework.web.servlet.ModelAndView;
//import com.common.error.Error;
//import com.common.exception.BadRequestException;
//import com.common.exception.CustomDefaultException;
//import com.common.exception.CustomValidationException;
//import com.common.exception.DuplicatonCheckFailedException;
//import com.common.exception.ExpiredDateException;
//import com.common.exception.InvalidException;
//import com.common.exception.MandatoryParamInvalidException;
//import com.common.exception.MandatoryParamRequireException;
//import com.common.exception.NotAllowedRequestException;
//import com.common.exception.NotFoundException;
//import com.common.exception.OptionalParamInvalidException;
//import com.common.exception.PreconditionException;
//import com.common.exception.UnauthorizedException;
//import com.common.util.Const.CUSTOM_ERROR_TYPE;
//import com.common.util.ErrorUtil;
//import com.common.util.ValidMessage;
//import com.common.valid.BaseValidator;
//import com.domain.common.util.CacheManager;
//import com.fasterxml.jackson.databind.JsonMappingException.Reference;
//import com.fasterxml.jackson.databind.exc.InvalidFormatException;
//import lombok.extern.slf4j.Slf4j;
//
//
//@Slf4j
//@ControllerAdvice(basePackages = { "com"}, basePackageClasses = RepositoryRestExceptionHandler.class)
//public class CustomControllerAdvice {
//
//    /**
//     * client 단에서 배열 형태로 넘어 오는 데이터가 스프링에서 bind 될때, defalut max length가 256이다 하여 이를
//     * 1024로 증가시킴.
//     *
//     * @param binder
//     */
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        // binder.setAutoGrowCollectionLimit(1024);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        dateFormat.setLenient(true);
//        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
//        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
//    }
//
//    /**
//     *
//     * @param request
//     * @param response
//     * @param exception
//     * @return
//     */
//    @ExceptionHandler(value = Exception.class)
//    public ModelAndView exceptionHandler(HttpServletRequest request, HttpServletResponse response,
//            Exception exception) {
//        ResponseStatus annotation = exception.getClass().getAnnotation(ResponseStatus.class);
//        if (exception instanceof IllegalArgumentException) {
//            if (exception.getCause() != null && exception.getCause() instanceof InvalidFormatException) {
//                return invalidFormatExceptionHandler(request, response, (InvalidFormatException) exception.getCause(), HttpStatus.BAD_REQUEST);
//            }
//        }
//        if (annotation == null || annotation.value() == null) {
//            return commonExceptionHandler(request, response, exception, HttpStatus.INTERNAL_SERVER_ERROR);
//        } else {
//            return commonExceptionHandler(request, response, exception, HttpStatus.valueOf(annotation.value().value()));
//        }
//    }
//
//    /**
//     *
//     * @param request
//     * @param response
//     * @param exception
//     * @param httpStatus
//     * @return
//     */
//    //TODO 오픈전 실서버 오류 메세지 예외처리 필! (세관신고 및 다른 프로젝트 포함!!)
//    private ModelAndView commonExceptionHandler(HttpServletRequest request, HttpServletResponse response,
//            Exception exception, HttpStatus httpStatus) {
//        if (CacheManager.isLocal()) System.out.println(exception.getMessage());
//        log.error(exception.getMessage());
//        ModelAndView model = new ModelAndView("jsonView");
//        List<Error> errors = new ArrayList<Error>();
//        if(exception instanceof CannotCreateTransactionException) {
//            errors.add(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), httpStatus.getReasonPhrase(), "DB커넥션 실패 또는 DB정보동기화 실패", "DB커넥션이 끊겼습니다.다시 WAS를 재기동해주세요. "+ ExceptionUtils.getStackTrace(exception)));
//        }else {
////            if(CacheManager.isReal()) {
////                errors.add(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "시스템오류가 발생하였습니다.", "시스템 오류가 발생 하였습니다. 관리자에게 문의하세요"));
////            } else {
////            }
//            String errorMessage = CacheManager.isLocal() ? ExceptionUtils.getStackTrace(exception) : "시스템 오류가 발생 하였습니다. 관리자에게 문의하세요.";
//            errors.add(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "시스템오류가 발생하였습니다.", errorMessage));
//        }
//        model.addObject("errors",errors);
//        response.setStatus(HttpStatus.BAD_REQUEST.value()); // 500 등  기타 상태코드로 올수 있는 여지가 있으므로 FE단과 일괄 처리하는거로(FE단 수정이 많음)
//        // log.debug("URL : {}, header & parameters : {}, exception : ", request.getRequestURL(), HttpUtil.getHeadersNParams(request), exception);
//        return model;
//    }
//    
//   
//    /**
//     * 자바 빈에 의한 유효성 체크 Exception Handler
//     * 주로 size 체크 not null 체크에 이용
//     * @param request
//     * @param response
//     * @param exception
//     * @return
//     */
//    @ExceptionHandler({ ConstraintViolationException.class })
//    public ModelAndView constraintViolationExceptionHandler(HttpServletRequest request, HttpServletResponse response,
//            ConstraintViolationException exception) {
//        if (CacheManager.isLocal()) System.out.println(exception.getMessage());
//        log.error(exception.getMessage());
//        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
//        List<Error> errors = new ArrayList<>();
//        errors.addAll(constraintViolations.stream().map(constraintViolation -> {
//            String field = constraintViolation.getPropertyPath().toString();
//            return new Error(ValidMessage.DEFAULT_CODE, field, ValidMessage.DEFAULT_TITLE, constraintViolation.getMessage());
//            })
//                .collect(Collectors.toList()));
//        // for(ConstraintViolation<?> constraintViolation : constraintViolations) {
//        // errors.add(new
//        // Error(ValidMessage.DEFAULT_CODE,constraintViolation.getPropertyPath().toString(),ValidMessage.DEFAULT_TITLE,constraintViolation.getMessage()));
//        // }
//        if (MediaType.APPLICATION_JSON_VALUE.equals(request.getHeader("contentType"))) {
//            
//        }
//        ModelAndView model = new ModelAndView("jsonView");
//        model.addObject("errors",errors);
//        response.setStatus(HttpStatus.BAD_REQUEST.value());
//        return model;
//    }
//
//    @ExceptionHandler({ TransactionSystemException.class , CannotCreateTransactionException.class})
//    public ModelAndView transactionSystemExceptionHandler(HttpServletRequest request, HttpServletResponse response,
//            Exception exception) {
//        if (CacheManager.isLocal()) System.out.println(exception.getMessage());
//        log.error(exception.getMessage());
//        if(exception.getCause() instanceof RollbackException) {
//            RollbackException rollbackException = (RollbackException) exception.getCause();
//            if(rollbackException.getCause() != null && rollbackException.getCause() instanceof ConstraintViolationException) {
//                return constraintViolationExceptionHandler(request, response, (ConstraintViolationException) rollbackException.getCause());
//            } else {
//                return commonExceptionHandler(request, response, rollbackException, HttpStatus.BAD_REQUEST);
//            }
//        } else {
//            return commonExceptionHandler(request, response, exception, HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @ExceptionHandler({ HttpMessageNotReadableException.class})
//    public ModelAndView httpExceptionHandler(HttpServletRequest request, HttpServletResponse response, HttpMessageNotReadableException exception) {
//        if (CacheManager.isLocal()) System.out.println(exception.getMessage());
//        log.error(exception.getMessage());
//        if(exception.getCause() != null && exception.getCause() instanceof InvalidFormatException) {
//            return invalidFormatExceptionHandler(request, response, (InvalidFormatException) exception.getCause(), HttpStatus.BAD_REQUEST);
//        } else {
//            return commonExceptionHandler(request, response, exception, HttpStatus.BAD_REQUEST);
//        }
//    }
//    
//    private ModelAndView invalidFormatExceptionHandler(HttpServletRequest request, HttpServletResponse response,
//            InvalidFormatException exception, HttpStatus httpStatus) {
//        if (CacheManager.isLocal()) System.out.println(exception.getMessage());
//        log.error(exception.getMessage());
//        ModelAndView model = new ModelAndView("jsonView");
//        List<Error> errors = new ArrayList<Error>();
//        List<Reference> path = exception.getPath();
//        String field = path.get(path.size()-1).getFieldName();
//        errors.add(new Error(field, exception.getValue().toString()+" 은/는 잘못된 값입니다."));
//        model.addObject("errors",errors);
//        response.setStatus(httpStatus.value());
//        return model;
//    }
//
//    /**
//     * 필수 파라미터에 대한 Validation 체크는 Validator에
//     *
//     * @param request
//     * @param response
//     * @param exception
//     * @return
//     */
//    @ExceptionHandler({ RepositoryConstraintViolationException.class, OptionalParamInvalidException.class, MandatoryParamInvalidException.class })
//    public ModelAndView validationExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) {
//        if (CacheManager.isLocal()) System.out.println(exception.getMessage());
//        log.error(exception.getMessage());
//        ModelAndView model = new ModelAndView("jsonView");
////        if (exception instanceof OptionalParamInvalidException) {
////            Object obj = ((OptionalParamInvalidException) exception).getObject();
////            List<Error> errors = BaseValidator.convert(((OptionalParamInvalidException) exception).getErrors());
////            ErrorUtil.setErrorInObejct(obj, errors);
////            response.setStatus(((OptionalParamInvalidException) exception).getStatus().getValue());
////            model.addObject(obj);
////        } else if (exception instanceof MandatoryParamInvalidException) {
////            Object obj = ((MandatoryParamInvalidException) exception).getObject();
////            List<Error> errors = BaseValidator.convert(((MandatoryParamInvalidException) exception).getErrors());
////            ErrorUtil.setErrorInObejct(obj, errors);
////            response.setStatus(((MandatoryParamInvalidException) exception).getStatus().getValue());
////            model.addObject(obj);
////        } else if (exception instanceof RepositoryConstraintViolationException) {
////            response.setStatus(HttpStatus.BAD_REQUEST.value());
////            model.addObject("errors",BaseValidator.convert(((RepositoryConstraintViolationException) exception).getErrors()));
////        } else {
////            log.error(" 잘못된 Exception Handling 입니다. 시스템 관리자에게 즉시 문의하십시오.");
////            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
////        }
//        if (exception instanceof RepositoryConstraintViolationException) {
//            response.setStatus(HttpStatus.BAD_REQUEST.value());
//            model.addObject("errors",BaseValidator.convert(((RepositoryConstraintViolationException) exception).getErrors()));
//        } else {
//            Object obj = ((CustomValidationException) exception).getObject();
//            List<Error> errors = BaseValidator.convert(((CustomValidationException) exception).getErrors());
//            ErrorUtil.setErrorInObejct(obj, errors);
//            response.setStatus(((CustomValidationException) exception).getStatus().getValue());
//            model.addObject(obj);
//        }
//        return model;
//    }
//
//    /**
//     * search시 필수 파라미터값이 누락된 경우 발생하는 예외처리
//     * ex) 화물 검색시 masterId만 있는 경우 businessDivision이 필수
//     * @param request
//     * @param response
//     * @param exception
//     * @return
//     */
//    @ExceptionHandler({ MissingServletRequestParameterException.class, MandatoryParamRequireException.class })
//    public ModelAndView paramRequiredExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) {
//        if (CacheManager.isLocal()) System.out.println(exception.getMessage());
//        log.error(exception.getMessage());
//        ModelAndView model = new ModelAndView("jsonView");
//        if (exception instanceof MandatoryParamRequireException) {
//            response.setStatus(((MandatoryParamRequireException) exception).getStatus().getValue());
//            model.addObject("errors", ((MandatoryParamRequireException) exception).getErrors());
//        } else if(exception instanceof MissingServletRequestParameterException) {
//            MissingServletRequestParameterException e = (MissingServletRequestParameterException) exception;
//            CUSTOM_ERROR_TYPE status = CUSTOM_ERROR_TYPE.MANDATORY_PARAM_REQUIRED;
//            response.setStatus(status.getValue());
//            List<Error> errors = new ArrayList<>();
//            errors.add(new Error(status.getCode(), e.getParameterName(),status.getTitle(), "필수 파라미터 값을 입력 하셔야 합니다."));
//            model.addObject("errors", errors);
//        } else {
//            log.error(" 잘못된 Exception Handling 입니다. 시스템 관리자에게 즉시 문의하십시오.");
//            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//        return model;
//    }
//
//    /**
//     * 잘못된 api를 호출시 예외처리
//     * badRequestException.class 거의 사용 X, 추후 제외 여부 검토
//     * @param request
//     * @param response
//     * @param exception
//     * @return
//     */
//    @ExceptionHandler({ BadRequestException.class, MethodArgumentTypeMismatchException.class, ResourceNotFoundException.class, ConversionFailedException.class})
//    public ModelAndView badReqeustExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) {
//        if (CacheManager.isLocal()) System.out.println(exception.getMessage());
//        log.error(exception.getMessage());
//        ModelAndView model = new ModelAndView("jsonView");
//        CUSTOM_ERROR_TYPE status = CUSTOM_ERROR_TYPE.BAD_REQUEST;
//        List<Error> errors = new ArrayList<>();
//        if (exception instanceof BadRequestException) {
//            errors = ((BadRequestException) exception).getErrors();
//        } else {
//            errors.add(new Error(status.getCode(),null,status.getTitle(),"잘못된 API를 호출 하였습니다."));
//        }
//        response.setStatus(status.getValue());
//        model.addObject("errors", errors);
//        return model;
//    }
//
//    @ExceptionHandler({ InvalidException.class, NotFoundException.class, UnauthorizedException.class, NotAllowedRequestException.class, ExpiredDateException.class, DuplicatonCheckFailedException.class, PreconditionException.class})
//    public ModelAndView customExceptionHandler(HttpServletRequest request, HttpServletResponse response, CustomDefaultException exception) {
//        if (CacheManager.isLocal()) System.out.println(exception.getMessage());
//        log.error(exception.getMessage());
//        ModelAndView model = new ModelAndView("jsonView");
//        Object object = exception.getObject();
//        if(object == null) {
//            response.setStatus(exception.getStatus().getValue());
//            model.addObject("errors", exception.getErrors());
//        } else {
//            ErrorUtil.setErrorInObejct(object, exception.getErrors());
//            response.setStatus(exception.getStatus().getValue());
//            model.addObject(object);
//        }
//        return model;
//    }
//    
//    
//    @ExceptionHandler({ PropertyReferenceException.class})
//    public ModelAndView propertyReferenceExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) {
//        if (CacheManager.isLocal()) System.out.println(exception.getMessage());
//        log.error(exception.getMessage());
//        ModelAndView model = new ModelAndView("jsonView");
//        CUSTOM_ERROR_TYPE status = CUSTOM_ERROR_TYPE.BAD_REQUEST;
//        List<Error> errors = new ArrayList<>();
//        ((PropertyReferenceException) exception).getPropertyName();
//        errors.add(new Error(status.getCode(),null,status.getTitle(), ((PropertyReferenceException) exception).getPropertyName() + " 은/는 잘못된 필드 정보 입니다."));
//        response.setStatus(status.getValue());
//        model.addObject("errors", errors);
//        return model;
//    }
//
////    @ExceptionHandler({ InvalidException.class })
////    public ModelAndView invaildExceptionHandler(HttpServletRequest request, HttpServletResponse response, InvalidException exception) {
////        exception.printStackTrace();
////        ModelAndView model = new ModelAndView("jsonView");
////        Object object = exception.getObject();
////        if(object == null) {
////            response.setStatus(exception.getStatus().getValue());
////            model.addObject(exception.getError());
////        } else {
////            ErrorUtil.setErrorInObejct(object, exception.getError());
////            response.setStatus(exception.getStatus().getValue());
////            model.addObject(exception.getError());
////        }
////        return model;
////    }
////
////    @ExceptionHandler({ NotFoundException.class})
////    public ModelAndView notFoundExceptionHandler(HttpServletRequest request, HttpServletResponse response, NotFoundException exception) {
////        exception.printStackTrace();
////        ModelAndView model = new ModelAndView("jsonView");
////        Object object = exception.getObject();
////        ErrorUtil.setErrorInObejct(object, exception.getError());
////        response.setStatus(exception.getStatus().getValue());
////        model.addObject(object);
////        return model;
////    }
////
////
////    @ExceptionHandler({ UnauthorizedException.class, NotAllowedRequestException.class  })
////    public ModelAndView badRequestException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
////        exception.printStackTrace();
////        ModelAndView model = new ModelAndView("jsonView");
////        if (exception instanceof UnauthorizedException) {
////            response.setStatus(((UnauthorizedException) exception).getStatus().getValue());
////            model.addObject(((UnauthorizedException) exception).getError());
////        } else if (exception instanceof NotAllowedRequestException) {
////            response.setStatus(((NotAllowedRequestException) exception).getStatus().getValue());
////            model.addObject(((NotAllowedRequestException) exception).getError());
////        }  else {
////            log.error(" 잘못된 Exception Handling 입니다. 시스템 관리자에게 즉시 문의하십시오.");
////            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
////        }
////        return model;
////    }
//
//
//
//}
