# Date Converter
Date converter is a java library that converts the date in A.D. to B.S. and vice versa

# Usage
### Converting A.D to B.S
```java
Date date = new Date();                             //default java Date object
DateBS dateBS = DateConverter.convertADToBS(date);  //returns corresponding DateBS
```

### Converting B.S to A.D
```java
DateBS dateBS = new DateBS();                       //returns current date in B.S
Date date = DateConverter.convertBSToAD(dateBS);    //returns corresponding Date object
```

### Add days in DateBS
```java
DateBS newDateBS = CalendarBS.addXDays(dateBS, daysToAdd)
```

# Download and Install
Download the following jar and put it on your classpath
[``dateconverter.jar``](https://github.com/binodnme/date-converter/releases/download/v1.0.1/dateconverter.jar)

Or add a dependency:
### Maven
```xml
<dependency>
  <groupId>com.github.binodnme</groupId>
  <artifactId>date-converter</artifactId>
  <version>1.0.1</version>
</dependency>
```
### Gradle
```compile 'com.github.binodnme:date-converter:1.0.1'```

or find your dependency here
[``maven central``](https://search.maven.org/#artifactdetails%7Ccom.github.binodnme%7Cdate-converter%7C1.0.1%7Cjar)
