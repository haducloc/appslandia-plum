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
	public void testAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
	// @EnableCsrf, @EnableEtag, @EnableCompression, @EnableParts, @EnableAsync, etc.
	
	// @ConsumeType
	
	// @CacheControl("cacheControl1")
	// ...
}
```

## License
This code is distributed under the terms and conditions of the [MIT license](LICENSE).
