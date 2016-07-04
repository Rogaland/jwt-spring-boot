# JWT Spring Boot

[![Build Status](https://travis-ci.org/Rogaland/jwt-spring-boot.svg?branch=master)](https://travis-ci.org/Rogaland/jwt-spring-boot)

JWT Spring Boot makes it easy to integrate JSON Web Tokens ([JWT](https://jwt.io/)) and Spring boot.

---

__Features__

* Annotation or properties/yml based configuration
* Support for custom _ClaimValidators_, for easy validation of token values
* Uses the JWT model with Claims
* Encryption using [jasypt](http://www.jasypt.org/) and [bouncy castle](http://www.bouncycastle.org/)

# Installation
_build.gradle_

```groovy
compile('no.rogfk:jwt-spring-boot:0.0.6')
```

# Usage
On the main class (containing `@SpringBootApplication`) add the `@EnableJwtTokenizer` annotation.  
The jwt-spring-boot can be configured either by values in the annotation or properties/yml-files.
If both exists, the properties/yml-file values will always win.

The main class used in `SpringJwtTokenizer`, it can be `@Autowired` into your own classes.  
The important methods are `wrap` and `unwrap`.

* __wrap()__ - Creates the JWT string and encrypts (if enabled) the value. Returns a string token.
* __unwrap()__ - Decrypts (if enabled), runs validators and returns the list of claims with the content of the token.

__Example__
```java
@EnableJwtTokenizer(issuer = "my-org", maxAgeMinutes = 120)
@SpringBootApplication
public class Application {
  ...
}
```
```java
@Autowired
private SpringJwtTokenizer tokenizer;

@PostConstruct
public void init() {
    String token = tokenizer.wrap("str-test", "testing");
    Optional<Claim> claim = tokenizer.unwrap("str-test", token);
}
```

## Custom DTO

It is also possible to wrap/unwrap custom DTOs.  
When sending in your own object to `wrap()` it will find all variable names and values, and then build up claims for you.  
The standard claims are also added. When unwrapping, send in the class and the token to the `unwrap()` function and it will build up the object from claims automatically.  
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

String token = springJwtTokenizer.wrap(testDto);
TestDto unwrapped = springJwtTokenizer.unwrap(TestDto.class, token);

```

## Standard Validators
The standard validators are enabled by default.  
You can disable them by setting `standardValidators` to false in the `@EnableJwtTokenizer` annotation.  
If no properties are set for max-age and issuer, the validators are skipped.  
  
__Standard validators__:  

* __IssuedAtClaimValidator__ - Validates that the generated timestamp is not older than the max age property.
* __IssuerClaimValidator__ - Validates that the issuer in the token is the same as the values in the issuer property.


## Add custom validators
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

# Encryption
Encryption can be enabled/disabled in the `@EnableJwtTokenizer`, it is enabled by default.  
If it is enabled the property 'jasypt.encryptor.password' must be set.

# Configuration

| Key | Description | Required |
|-----|----------|-------------|
| jasypt.encryptor.password | Encrypts the token created with this password. Should be sent in as an environment variable. If this is set and the 'jwt.key' is not, this value will be used as the JWT key as well. | Y |
| jwt.key |Key used to sign the JSOM Web Token. | |
| jwt.issuer | The issuer of the token, will be added as a standard claim. | |
| jwt.max-age-minutes | The max age (in minutes) that the token is valid. Is added and validated as a standard claim. | |

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