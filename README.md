# BFH Java Qualifier — Complete Implementation

Spring Boot app that:
1. On startup, POSTs to generate a webhook (and receives `webhook` + `accessToken`).
2. Chooses the assigned SQL question based on the last two digits of `regNo` (odd → Q1, even → Q2).
3. Produces the **final SQL query**, stores it to `finalQuery.sql` locally, and
4. Submits it to the provided `webhook` URL (or the fallback `/hiring/testWebhook/JAVA`) using the JWT from `accessToken` in the `Authorization` header.

> Spec summarized from the PDF prompt.

---

## Project layout

```
bfh-java-qualifier/
  ├─ pom.xml
  ├─ src/main/java/com/example/bfh/
  │   ├─ Application.java
  │   ├─ config/AppProperties.java
  │   ├─ dto/GenerateWebhookRequest.java
  │   ├─ dto/GenerateWebhookResponse.java
  │   └─ service/
  │       ├─ StartupRunner.java
  │       ├─ WebhookClient.java
  │       ├─ SqlSolver.java
  │       └─ storage/ResultStorage.java
  └─ src/main/resources/application.yml
```

---

## Prerequisites

- Java 17+
- Maven 3.9+
- Internet access to the BFH API host

---

## Configure your identity

Set your identification in `application.yml` (or via environment variables).

```yaml
bfh:
  name: ${BFH_NAME:John Doe}
  regNo: ${BFH_REG_NO:REG12347}
  email: ${BFH_EMAIL:john@example.com}
```

> You can override with env vars:
>
> ```bash
> export BFH_NAME="Your Name"
> export BFH_REG_NO="YOURREG123"
> export BFH_EMAIL="you@example.com"
> ```

---

## Build

```bash
mvn -q -DskipTests package
```

This produces `target/bfh-java-qualifier-1.0.0.jar` (executable).

---

## Run

```bash
BFH_NAME="Your Name" \
BFH_REG_NO="REG12347" \
BFH_EMAIL="you@example.com" \
java -jar target/bfh-java-qualifier-1.0.0.jar
```

What happens:

1. The app starts **without exposing any controller/endpoint**.
2. It calls:
   - `POST https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA`
     body:
     ```json
     {"name":"Your Name","regNo":"REG12347","email":"you@example.com"}
     ```
3. The API responds with `{ "webhook": "...", "accessToken": "..." }`.
4. The app chooses Q1/Q2 based on the last two digits of the `regNo`:
   - Odd → Q1
   - Even → Q2
5. **Edit** `SqlSolver#solve(boolean isOdd)` and paste your final SQL for the respective question.
6. The app stores the SQL to `finalQuery.sql` and submits it to the `webhook` URL (or the fallback `/hiring/testWebhook/JAVA`) with header:
   - `Authorization: <accessToken>`
   - `Content-Type: application/json`
   - Body: `{ "finalQuery": "YOUR_SQL_QUERY_HERE" }`

> If the platform expects `Bearer <token>`, set `bfh.useBearerPrefix=true` in `application.yml`.

---

## Where to put your real SQL

Open `src/main/java/com/example/bfh/service/SqlSolver.java` and replace the placeholders:

```java
public String solve(boolean isOdd) {
    if (isOdd) {
        return "/* Your SQL for Question 1 */\nSELECT ...";
    } else {
        return "/* Your SQL for Question 2 */\nSELECT ...";
    }
}
```

---

## Logs (sample)

```
... BFH Java Qualifier app starting with regNo=REG12347
... Calling generateWebhook at https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA
... Received webhook='https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA'
... Assigned question: Question 1 (odd)
... Saved final query to /path/to/finalQuery.sql
... Submitting finalQuery to https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA
... Submission response: {"status":"ok"}
... Final query submitted successfully.
```

---

## Submission checklist (per prompt)

- Public GitHub repo with:
  - Code
  - Final JAR (`target/bfh-java-qualifier-1.0.0.jar`)
  - **Raw downloadable** link to the JAR
- Also provide a public JAR file link (downloadable)
- Format example: `https://github.com/your-username/your-repo.git`

---

## Notes

- The code tolerates either a dynamic `webhook` returned by the API or the fixed `/hiring/testWebhook/JAVA` path described in the prompt.
- We set `spring.main.web-application-type=none` so no HTTP server is started.
- Networking timeouts and retries can be added to `WebhookClient` if desired.
