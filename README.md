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
- Provides built-in support for **CSRF**, **ETag**, **ASYNC**, **Compression**, **Logging**, **TempData**, **Header Policies**, and more  
- **X-Forwarded-*** headers and client IP address parsing, ready for any server environment

## Installation

### Java Version
- Java 21+

### Maven
```xml
<dependency>
  <groupId>com.appslandia</groupId>
  <artifactId>appslandia-plum</artifactId>
  <version>20.5.0</version>
</dependency>

<dependency>
  <groupId>com.appslandia</groupId>
  <artifactId>appslandia-common</artifactId>
  <version>19.51.0</version>
</dependency>

<dependency>
  <groupId>com.googlecode.owasp-java-html-sanitizer</groupId>
  <artifactId>owasp-java-html-sanitizer</artifactId>
  <version>20260313.1</version>
</dependency>

<dependency>
  <groupId>com.aayushatharva.brotli4j</groupId>
  <artifactId>brotli4j</artifactId>
  <version>1.23.0</version>
</dependency>
```

## Sample Usage

```java
@ApplicationScoped
public class UserController extends ControllerBase {

  // GET /user
  @HttpGet
  public void index(HttpRequestFacade request, HttpServletResponse response) throws Exception {
    response.getWriter().print("test");
  }

  // GET|POST /user/edit
  @HttpGetPost
  public ActionResult edit(HttpRequestFacade request, HttpServletResponse response) throws Exception {
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
  public Result<String> register(@BindModel(BindSource.JSON_BODY) user, ModelState modelState) {
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
  // @EnableCsrf, @EnableEtag, @EnableCompress, @EnablePart, @EnableAsync, @BindField, etc.
  // ...
}
```

## Developer Guide

### Module-Specific Implementations
- Impl of `AuthPrincipal`
- Impl of `IdentityStoreBase` (or extends `AuthByCodeIdentityStore`, `JwtIdentityStore`, etc.)
- Impl of `HttpAuthMechanismBase` (or extends `FormHttpAuthMechanism`, `BasicHttpAuthMechanism`, or `BearerHttpAuthMechanism`, etc.)

### Module Shared-Beans
- Implementation of `LanguageConfig`
- Implementation of `ErrorServlet`
- Implementation of `DynHandlerRegister`
- Implementation of `HeaderPolicyProvider` for header policies based on an Ant-style path matcher
- Implementation of `AuthTokenManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Implementation of `RemMeTokenIdentityStore` with `@Alternative` and `@Priority(APPLICATION)` (if a `remember me` feature is needed)
- Implementation of `RemMeTokenManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Implementation of `LoginEventManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Implementation of `InitialRequestGuard` with `@Alternative` and `@Priority(APPLICATION)` (if you want to guard HTTP requests for all types of resources)
- Implementation of `TempDataManager` with `@Alternative` and `@Priority(APPLICATION)` (default: HTTP session version)
- Implementation of `SimpleCsrfManager` with `@Alternative` and `@Priority(APPLICATION)` (default: HTTP session version)
- Implementation of `BasicHtmlSanitizer` with `@Alternative` and `@Priority(APPLICATION)` (default: `DefaultBasicHtmlSanitizer`)
- Implementation of `RemMePostHandler` with `@Alternative` and `@Priority(APPLICATION)` (if you want to log `remember me` login events)
- CDI producers for `AppLogger` (without a qualifier) and with the `@AccessLog` qualifier (default: `JulAppLogger`)
- CDI producers for `JsonProcessor` (without a qualifier) and with the `@Json(Profile.COMPACT)` or `@Json(Profile.PRETTY)` qualifier (default: JSON-B implementation)

## Plum Demo
See the complete **Plum** framework demo application on GitHub:
- [Plum Demo](https://github.com/haducloc/plum-demo)
  
## License
This code is distributed under the terms and conditions of the [MIT license](LICENSE).
