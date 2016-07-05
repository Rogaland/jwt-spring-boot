# JWT Spring Boot

[![Build Status](https://travis-ci.org/Rogaland/jwt-spring-boot.svg?branch=master)](https://travis-ci.org/Rogaland/jwt-spring-boot)

JWT Spring Boot makes it easy to integrate JSON Web Tokens ([JWT](https://jwt.io/)) and Spring boot.

__Features__

* Annotation or properties/yml based configuration
* Support for custom _ClaimValidators_, for easy validation of token values
* Uses the JWT model with Claims
* Encryption using [jasypt](http://www.jasypt.org/) and [bouncy castle](http://www.bouncycastle.org/)

---

* [Installation](#installation)
* [Usage](#usage)
 * [Standard Validators](#standard-validators)
 * [Custom Validators](#custom-validators)
* [Configuration](#configuration)
* [Deployment](#deployment)
* [References](#references)

# Installation
_build.gradle_

```groovy
compile('no.rogfk:jwt-spring-boot:0.0.8')
```

# Usage
On the main class (containing `@SpringBootApplication`) add the `@EnableJwt` annotation.  
The jwt-spring-boot can be configured either by values in the annotation or properties/yml-files.  
If both exists, the properties/yml-file values will always win.  

Encryption can be enabled/disabled in the `@EnableJwt`, it is enabled by default.
If it is enabled the property _'jasypt.encryptor.password'_ must be set.  
If encryption is disabled the JWT string will be returned from `SpringJwtTokenizer.create()`.


__Example__
```java
@EnableJwt(issuer = "my-org", maxAgeMinutes = 120)
@SpringBootApplication
public class Application {
  ...
}
```

The main class is `SpringJwtTokenizer` and it can be `@Autowired` into your own classes.  
The important methods are `create` and `parse`.

* __create()__ - Creates the JWT string and encrypts (if enabled) the value. Returns a string token.
* __parse()__ - Decrypts (if enabled), runs validators and returns the list of claims with the content of the token.

```java
@Autowired
private SpringJwtTokenizer tokenizer;

@PostConstruct
public void init() {
    String token = tokenizer.create("str-test", "testing");
    Optional<Claim> claim = tokenizer.parse("str-test", token);
}
```

By using the `@JwtParam` annotation in a `RestController` class the parsing of a JWT string is done automatically.  
The controller expects the input to be sent as a URL parameter. The name of the param can be specified in the annotation. If the name is not set, the name of the method variable is used.

__Example__
```java
@RequestMapping("/custom-claim")
public TestDto test(@JwtParam TestDto testDto) {
    return testDto;
}
```
The url contains: `/custom-claim?testDto=<jwt>`

It is also possible to create/parse custom DTOs.
When sending in your own object to `create()` it will find all variable names and values, and then build up claims for you.
The standard claims are also added. When unwrapping, send in the class and the token to the `parse()` function and it will build up the object from claims automatically.
If there are claims present in the token, but they do not have corresponding fields in the object they are skipped.  
By extending the class `DefaultClaim` you will get the default claims (such as Issuer).  

__Example__

```java
@Data
public class TestDto extends DefaultClaim {
    private String text1;
}
```

```java
TestDto testDto = new TestDto();
testDto.setText1("value1");

String token = springJwtTokenizer.create(testDto);
TestDto testDto = springJwtTokenizer.parse(TestDto.class, token);

```

## Standard Validators
The standard validators are enabled by default.  
You can disable them by setting `standardValidators` to false in the `@EnableJwt` annotation.  
If no properties are set for max-age and issuer, the validators are skipped.  
  
__Standard validators__:  

* __IssuedAtClaimValidator__ - Validates that the generated timestamp is not older than the max age property.
* __IssuerClaimValidator__ - Validates that the issuer in the token is the same as the values in the issuer property.


## Custom validators
By using standard Spring dependency injection it is easy to add your own validators.  
Simply create a new bean that extends the `ClaimValidator` interface. Implement the name, valid and optionally the exceptionMessage methods.

* __name()__ - The claim name. Examples of standard claim names are: _iss_ (Issuer), _sub_ (Subject), _iat_ (IssuedAt). In custom claims find a new name that is not used by the standard (https://tools.ietf.org/html/rfc7519#page-9).
* __valid()__ - The method that implements the validation. You can either return false for non-valid messages or throw an exception.
* __exceptionMessage()__ - An optional method that makes it possible to create custom exception messages.

As long as the bean you have created is registered in the Spring container (for example by using `@Component`), it will be picked up the jwt-spring-boot and used during the validation phase.

__Example__
```java
@Component
public class MyStringLengthValidator implements ClaimValidator {
    @Override
    public String name() {
        return "str-length";
    }

    @Override
    public boolean valid(String s) {
        return s.length() == 10;
    }
}
```

# Configuration

| Key | Description |
|-----|----------|
| jasypt.encryptor.password | Encrypts the token created with this password. Should be sent in as an environment variable. If this is set and the 'jwt.key' is not, this value will be used as the JWT key as well. |
| jwt.key |Key used to sign the JSON Web Token. |
| jwt.issuer | The issuer of the token, will be added as a standard claim. |
| jwt.max-age-minutes | The max age (in minutes) that the token is valid. Is added and validated as a standard claim. |

# Deployment
Update the maven settings.xml file with id, username and password (api key):
```xml
<server>
    <id>bintray-rfkikt-maven</id>
    <username></username>
    <password></password>
</server>
```

Deploy a new version by running the following command:
```
./mvnw deploy
```

# References
* [Java JWT](https://github.com/jwtk/jjwt)
* [Introduction to JSON Web Tokens](https://jwt.io/introduction/)
* [Get started with JSON Web Tokens](https://auth0.com/learn/json-web-tokens/)
* [JSON Web Token (JWT) Signing Algorithms Overview](https://auth0.com/blog/2015/12/17/json-web-token-signing-algorithms-overview/)