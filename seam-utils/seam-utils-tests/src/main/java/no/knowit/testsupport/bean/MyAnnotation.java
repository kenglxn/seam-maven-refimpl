package no.knowit.testsupport.bean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
  public String value() 
    default "Used by ToStringBuilderTest to print members tagged with a this annotation";
}
