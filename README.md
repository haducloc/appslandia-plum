# AppsLandia Plum - Java Web Framework

## Features
- Leverages the power of **Jakarta EE 11+** technologies  
- Integrates **Jakarta EE Security** and **Jakarta CDI**  
- Implements the **MVC** architecture, ready for enterprise-grade Java web applications  
- Supports building REST APIs  
- Eliminates the need for `security-constraint` or `login-config` configurations  
- Supports multiple authentication schemes, with security always in mind  
- Includes **Remember Me** functionality  
- Provides **re-authentication** support  
- Features built-in **model binding**  
- Supports **JSTL** and **Facelets** tags and functions  
- Supports multiple view technologies (**JSP**, **Facelets**, **Pebble**, etc.)  
- Provides built-in support for **CSRF**, **ETag**, **compression**, **CAPTCHA**, **logging**, **TempData**, **header policies**, and more  
- Allows configuration of **ETag**, **compression**, **CORS**, and **header policies** using **Ant-style path matcher** rules  

## Installation

### Java Version
- Java 21+

### Maven
```xml
<dependency>
  <groupId>com.appslandia</groupId>
  <artifactId>appslandia-plum</artifactId>
  <version>19.26.0</version>
</dependency>

<dependency>
  <groupId>com.appslandia</groupId>
  <artifactId>appslandia-common</artifactId>
  <version>19.45.0</version>
</dependency>
```

### Gradle
```groovy
dependencies {
  compile 'com.appslandia:appslandia-plum:19.26.0'
  compile 'com.appslandia:appslandia-common:19.45.0'
}
```

## Sample Usage

```java
@ApplicationScoped
public class UserController extends ControllerBase {

  // GET /user
  @HttpGet
  public void index(RequestWrapper request, HttpServletResponse response) throws Exception {
    response.getWriter().print("test");
  }

  // GET|POST /user/edit
  @HttpGetPost
  public ActionResult edit(RequestWrapper request, HttpServletResponse response) throws Exception {
    // GET
    if (request.isGetOrHead()) {
      // /user/edit.jsp or /user/edit.xhtml, etc.
      return ViewResult.DEFAULT;
    }
    // POST
    // Create Or Update User
    return new RedirectResult("index");
  }

  // GET /user/get/{userId}
  @HttpGet
  @PathParams("/{userId}")
  public User get(int userId) {
    if (userId <= 0) {
      throw new NotFoundException();
    }
    var u = loadUser(userId);
    return u;
  }

  // PUT /user/register
  @HttpPut
  public Result<String> register(@BindModel(ModelSource.JSON_BODY) user, ModelState modelState) {
    if (!modelState.isValid()) {
      throw new BadRequestException();
    }
    // Add user
    return new Result<String>().setMessage("Registered user successfully.");
  }

  // POST /user/testAuthorize
  @HttpPost
  @Authorize(roles = { "admin", "manager" })
  public void testAuthorize() {
    // ...
  }

  // Annotations:
  // @EnableCsrf, @EnableCaptcha, @EnableParts, @EnableAsync, etc.
  // ...
}
```

## Developer Guide

### Module-Specific Implementations
- Impl of `UserPrincipal`
- Impl of `IdentityValidator`
- Impl of `IdentityStoreBase` (extends `UserPassIdentityStore`, or `JwtIdentityStore`, etc.)
- Impl of `HttpAuthMechanismBase` (extends `FormHttpAuthMechanism`, `BasicHttpAuthMechanism`, or `BearerHttpAuthMechanism`, etc.)

### Module Shared-Beans
- Impl of `ErrorServlet`
- Impl of `LanguageConfig`
- Impl of `AuthTokenManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Impl of `RemMeTokenManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Impl of `LoginEventManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Impl of `JpaEntityManager` with `@Dependent` (if JPA is used)

## License
This code is distributed under the terms and conditions of the [MIT license](LICENSE).
