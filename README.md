# AppsLandia Plum - Java Web Framework

## Features
- Build modern web apps efficiently using **Java EE 8** technology.
- MVC architecture
- Microservices-oriented architecture
- Role & policy based authorization
- Multiple authentication schemes supported
- Model binding supported
- i18n supported
- Problem Details for HTTP APIs (rfc7807)
- Handles CSRF, ETag, GZIP, CAPTCHA, CORS, HSTS, CSP, Cache-Control, Logger, TempData, Server Cache, Remember Me, Reauthentication, Rate Limit, etc.
- 400+ Unit tests

## Installation

### Maven
```XML
<dependency>
    <groupId>com.appslandia</groupId>
    <artifactId>appslandia-plum-javaee8</artifactId>
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
    <version>1.2.2</version>
</dependency>
```

### Gradle
```
dependencies {
  compile 'com.appslandia:appslandia-plum-javaee8:{LATEST_VERSION}'
  compile 'com.appslandia:appslandia-common:{LATEST_VERSION}'
  
  // If Use JSTL Security Functions
  compile 'org.owasp.encoder:encoder:1.2.2'
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
	public ActionResult edit(RequestAccessor request, HttpServletResponse response) throws Exception {
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
	// @EnableCsrf, @EnableEtag, @EnableGzip, @EnableAsync, @EnableParts
	
	// @ConsumeType
	
	// @EnableCache("jcache1")
	// @CacheControl("cacheControl1")
	// ...
}
```

## Sample Project
Personal Expense Tracker
https://github.com/haducloc/expense-tracker

## Questions?
Please feel free to contact me if you have any questions or comments.
Email: haducloc13@gmail.com

## License
This code is distributed under the terms and conditions of the [MIT license](LICENSE).
