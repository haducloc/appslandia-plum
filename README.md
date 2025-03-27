# AppsLandia Plum - Java Web Framework

## Features
- Utilize the power of **Jakarta EE 10+** technology  
- Utilize the power of Jakarta EE Security  
- Follows MVC architecture  
- No need for `web.xml` `security-constraint` or `login-config`  
- Multiple authentication schemes implemented  
- "Remember Me" functionality implemented  
- Re-authentication functionality implemented  
- Model binding implemented  
- Standard JSTL tags supported  
- Multiple view types supported (JSP, Facelets, Pebble, etc.)  
- Handles CSRF, ETag, Compression, CAPTCHA, CORS, HSTS, CSP, Cache-Control, Logger, TempData, etc.  
- 375+ unit tests included  

## Installation

### Java Version
- Java 21+

### Maven
```XML
<dependency>
    <groupId>com.appslandia</groupId>
    <artifactId>appslandia-plum</artifactId>
    <version>19.18.0</version>
</dependency>

<dependency>
    <groupId>com.appslandia</groupId>
    <artifactId>appslandia-common</artifactId>
    <version>19.38.0</version>
</dependency>

// If Use JSTL Security Functions
<dependency>
    <groupId>org.owasp.encoder</groupId>
    <artifactId>encoder</artifactId>
    <version>1.2.3</version>
</dependency>
```

### Gradle
```
dependencies {
  compile 'com.appslandia:appslandia-plum:19.18.0'
  compile 'com.appslandia:appslandia-common:19.38.0'
  
  // If Use JSTL Security Functions
  compile 'org.owasp.encoder:encoder:1.2.3'
}
```

## Sample Usage

``` java
@ApplicationScoped
@Controller
public class UserController {

	// GET /user/index
	@HttpGet
	public ActionResult index() {
		return (request, response, requestContext) -> {
			response.getWriter().print("Hello, UserController!");
		};
	}

	// GET /user/test
	@HttpGet
	@Action("test")
	public void testAction(RequestWrapper request, HttpServletResponse response) throws Exception {
		response.getWriter().print("test");
	}

	// GET|POST /user/edit
	@HttpGetPost
	public ActionResult edit(RequestWrapper request, HttpServletResponse response) throws Exception {
		// GET
		if (request.isGetOrHead()) {
			// /user/edit.jsp
			return JspResult.DEFAULT;
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
		User u = loadUser(userId);
		return u;
	}

	// PUT /user/register
	@HttpPut
	public Result<String> register(@Model(Source.JSON_BODY) user, ModelState modelState) {
		if (!modelState.isValid()) {
			throw new BadRequestException();
		}
		// Add user
		return new Result<String>().setMessage("Registered user successfully.");
	}

	// POST /user/testAuthorize
	@HttpPost
	@Authorize(roles="admin")
	public void testAuthorize() {
		// ...
	}
		
	// Other Annotations:
	// @EnableCsrf, @EnableEtag, @EnableEncoding, @EnableParts, @EnableAsync, etc.
	
	// @ConsumeType
	
	// @CacheControl("cacheControl1")
	// ...
}
```

## Developer Guide

### Module-Specific Implementations

- Impl of `UserPrincipal`
- Impl of `Credential` (`UsernamePasswordCredential`, etc.)
- Impl of `IdentityStoreBase` (`UserPassIdentityStore`, etc.)
- Impl of `AuthHandler` (`FormAuthHandler`, etc.) with `@MappedID(module)`

### Shared Beans

- Impl of `IdentityHandler`
- Impl of `DynHandlersRegister` with `@StartupConfig`
- Impl of `ErrorServlet`
- Impl of `LanguageSupplier` with `@Alternative` and `@Priority(APPLICATION)` (default: `en-US` locale)
- Impl of `AuthTokenManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Impl of `RemMeTokenManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Impl of `LoginEventManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Impl of `JpaEntityManager` with `@Dependent` (if JPA is used)

## License
This code is distributed under the terms and conditions of the [MIT license](LICENSE).
