package com.example.dissertation_backend.solution.Internationalization.Controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/languages")
@CrossOrigin(
  origins = { "*" },
  methods = {
    RequestMethod.OPTIONS,
    RequestMethod.GET,
    RequestMethod.PUT,
    RequestMethod.DELETE,
    RequestMethod.POST,
  }
)
public class LanguageController {

  @Autowired
  private MessageSource messageSource;

  @GetMapping("/messages")
  public Map<String, String> getLocalizedMessages(HttpServletRequest request) {
    Locale locale = LocaleContextHolder.getLocale();
    String[] keys = { "greeting", "farewell", "welcome", "cart", "checkOut" };
    Map<String, String> localizedMessages = new HashMap<>();

    for (String key : keys) {
      localizedMessages.put(key, messageSource.getMessage(key, null, locale));
    }

    return localizedMessages;
  }
}
