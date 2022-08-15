package org.ptg.analyzer;

public class MatchTypes {
  public static final double classNameMatch = 0;
  public static final double extendsMatch = 20.0;
  public static final double implementsMatch = 30.0;
  public static final double lineMatch = 40.0;
  public static final double fieldAccessMatch = 60.0;
  
  public static final double methodReplace = 50.0;
  public static final double methodBefore = 51.0;
  public static final double methodAfter = 52.0;
  public static final double methodAfterFinal = 53.0;
  public static final double methodLine = 54.0;
}
