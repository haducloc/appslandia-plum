# AppsLandia Plum - Java Web Framework

## Features
- Utilize the power of **Jakarta EE 10+** technology.
- Utilize the power of Jakarta EE Security
- MVC architecture
- No need for web.xml security-constraint, login-config
- Multiple authentication schemes implemented
- Authentication by code mechanism implemented
- RememberMe implemented
- Re-authentication implemented
- Model binding implemented
- Standard JSTL tags implemented
- PebbleTemplates integrated
- i18n implemented
- Handles CSRF, ETag, Compression, CAPTCHA, CORS, HSTS, CSP, Cache-Control, Logger, TempData, etc.
- 360+ Unit tests

## Installation

### VERSIONS
- [appslandia-plum](https://search.maven.org/search?q=a:appslandia-plum)
- [appslandia-common](https://search.maven.org/search?q=a:appslandia-common)
- Java 21

### Maven
```XML
<dependency>
    <groupId>com.appslandia</groupId>
    <artifactId>appslandia-plum</artifactId>
    <version>{LATEST_VERSION}</version>
</dependency>

<dependency>
    <groupId>com.appslandia</groupId>
    <artifactId>appslandia-common</artifactId>
    <version>{LATEST_VERSION}</version>
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
  compile 'com.appslandia:appslandia-plum:{LATEST_VERSION}'
  compile 'com.appslandia:appslandia-common:{LATEST_VERSION}'
  
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

### Module-Specific Implementation (Each module has its own authentication scheme):

- All modules must use the same types of authentication schemes (e.g., FORM, JWT, etc.).
- Implementation of `UsernamePasswordCredential.UserPrincipal`
- Implementation of `UsernamePasswordCredential`
- Implementation of `UsernamePasswordIdentityStore` that supports the implementation of `UsernamePasswordCredential`
- Implementation of `AuthHandler` (e.g., `FormAuthHandler`) with `@MappedID(module)`

### Shared Beans

- Implementation of `IdentityHandler`
- Implementation of `HttpAuthenticationMechanismBase` with `@Alternative` and `@Priority(APPLICATION)`
- Implementation of `DynHandlersRegister` with `@StartupConfig`
- Implementation of `ErrorServlet`
- Implementation of `LanguageSupplier` with `@Alternative` and `@Priority(APPLICATION)` (default: `en-US` locale)
- Implementation of `AuthTokenManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Implementation of `RemMeTokenManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Implementation of `LoginEventManager` with `@Alternative` and `@Priority(APPLICATION)` (default: in-memory version)
- Implementation of `EntityManagerFacade` with `@Dependent` (if JPA is used)

## License
This code is distributed under the terms and conditions of the [MIT license](LICENSE).
