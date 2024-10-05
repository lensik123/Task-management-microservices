package ru.baysarov.task.service.feign;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Component
public class FeignClientInterceptor implements RequestInterceptor {
  private static final Logger logger = LoggerFactory.getLogger(FeignClientInterceptor.class);

  @Override
  public void apply(RequestTemplate template) {
    String token = getTokenFromRequest();

    if (token != null) {
      template.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
      logger.info("add to header");
    }
  }

  private String getTokenFromRequest() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    logger.info("trying to get token from request");
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        logger.info("token found{}", authHeader);
        return authHeader.substring(7);
      }
    }
    return null;
  }
}


