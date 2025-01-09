package com.infogain.annotations;

import java.lang.annotation.ElementType;
import com.infogain.enums.Author;
import com.infogain.enums.Category;
import com.infogain.enums.Service;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface FrameworkAnnotations {

    Author[] Author() default Author.RAVI;

    Category Category();

    Service Service();
}
