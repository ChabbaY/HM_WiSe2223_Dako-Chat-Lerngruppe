package edu.hm.dako.api.errorhandling;

import io.swagger.annotations.Api;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * showing error page if no applicable request mapping is found
 *
 * @author Linus Englert
 */
@RestController
@Api(tags="Error")
public class ErrorMapping implements ErrorController {
    @GetMapping("/error")
    @ResponseBody
    public String error() {
        return "!!! No Mapping found !!!";
    }
}