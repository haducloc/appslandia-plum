# AppsLandia Plum - Java Web Framework

## Features
- Utilize the power of **Jakarta EE 10+** technology  
- Utilize the power of **Jakarta EE Security** and **Jakarta CDI**  
- Follows the MVC architecture; ready for any enterprise-grade Java web application  
- No need for `security-constraint`, or `login-config`  
- Multiple authentication schemes implemented  
- "Remember Me" functionality implemented  
- Re-authentication functionality implemented  
- Model binding implemented  
- JSTL/Facelet tags/functions supported  
- Multiple view types supported (JSP, Facelets, Pebble, etc.)  
- Handles CSRF, ETag, Compression, CAPTCHA, CORS, HSTS, CSP, Cache-Control, Logger, TempData, etc.  
- 300+ unit tests included

## Installation

### Java Version
- Java 21+

### Maven
```xml
<dependency>
  <groupId>com.appslandia</groupId>
  <artifactId>appslandia-plum</artifactId>
  <version>19.21.0</version>
</dependency>

<dependency>
  <groupId>com.appslandia</groupId>
  <artifactId>appslandia-common</artifactId>
  <version>19.41.0</version>
</dependency>
```

### Gradle
```groovy
dependencies {
  compile 'com.appslandia:appslandia-plum:19.21.0'
  compile 'com.appslandia:appslandia-common:19.41.0'
}
```

## Sample Usage

```java
@ApplicationScoped
@Controller
public class UserController {

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

  // Other Annotations:
  // @EnableCsrf, @EnableCaptcha, @EnableEtag, @EnableEncoding, @EnableParts, @EnableAsync, etc.

  // @ConsumeType

  // @CacheControl("cacheControl1")
  // ...
}
```

## Developer Guide

### Module-Specific Implementations
- Impl of `UserPrincipal`
- Impl of `IdentityStoreBase` (`UserPassIdentityStore`, etc.)
- Impl of `HttpAuthMechanismBase` (`FormHttpAuthMechanism`, `BasicHttpAuthMechanism`, `BearerHttpAuthMechanism`, etc.)

### Module Shared-Beans
- Impl of `IdentityHandler`
- Impl of `DynHandlersRegister`
- Impl of `ErrorServlet`
- Impl of `LanguageSupplier` with `@Alternative` and `@Priority(APPLICATION)` (default: `en-US` locale)
- Impl of `AuthTokenManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Impl of `RemMeTokenManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Impl of `LoginEventManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Impl of `JpaEntityManager` with `@Dependent` (if JPA is used)

## License
This code is distributed under the terms and conditions of the [MIT license](LICENSE).
