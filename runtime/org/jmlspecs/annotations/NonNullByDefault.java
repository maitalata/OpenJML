package org.jmlspecs.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Documented;

/** Defines the 'non_null_by_default' JML annotation */

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NonNullByDefault {

}