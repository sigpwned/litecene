package com.sigpwned.litecene.linting;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS)
@Target({METHOD, CONSTRUCTOR})
public @interface Generated {

}
