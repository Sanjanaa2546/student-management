# Student Management — CI/CD Pipeline Assignment

Spring Boot Student Management app with two operations (add, view students),
containerized with Docker and deployed through a Jenkins pipeline.

## Project layout
```
student-management/
├── pom.xml
├── Dockerfile
├── Jenkinsfile
├── .gitignore
├── README.md
└── src/
    ├── main/java/com/sanjanaa/studentmanagement/
    │   ├── StudentManagementApplication.java
    │   ├── Student.java
    │   ├── StudentService.java
    │   └── StudentController.java
    └── test/java/com/sanjanaa/studentmanagement/
        └── StudentServiceTest.java
```

## 1. Git — push to your own repository

Open PowerShell in the `student-management` folder:

```powershell
git init
git add .
git commit -m "Initial commit: Student Management app with Maven, Docker, Jenkins setup"
git branch -M main
git remote add origin https://github.com/Sanjanaa2546/student-management.git
git push -u origin main
```

Make a couple more small, meaningful commits as you go (e.g. after adding the
Dockerfile, after adding the Jenkinsfile) — the assignment asks for meaningful
commit history, not just one big commit.

**Screenshot:** the repo page on GitHub showing commit history.

## 2. Maven — build and test locally

You need Java 17 and Maven installed (`java -version`, `mvn -version`).

```powershell
mvn clean package
```

This compiles the app, runs `StudentServiceTest`, and packages
`target/student-management.jar`.

**Screenshot:** terminal output showing `BUILD SUCCESS` and the test results
(`Tests run: 2, Failures: 0`).

Run it directly to sanity-check before containerizing:

```powershell
java -jar target/student-management.jar
```

Then in a browser or with curl:
- Add a student: `POST http://localhost:8080/students?name=Sanjanaa&department=CSE`
- View students: `GET http://localhost:8080/students`

## 3. Docker — containerize

Build the image (multi-stage: builds with Maven, runs on a slim JRE):

```powershell
docker build -t student-management:latest .
```

Run the container:

```powershell
docker run -d --name student-management-container -p 8080:8080 student-management:latest
```

Verify it's accessible from the host:

```powershell
curl http://localhost:8080/students/health
```

**Screenshots:**
- `docker images` showing the built image
- `docker ps` showing the running container
- Browser or curl output hitting `http://localhost:8080/students`

## 4. Jenkins — CI/CD pipeline

1. Install Jenkins (or run it in Docker):
   ```powershell
   docker run -d --name jenkins -p 8081:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
   ```
2. Unlock Jenkins and install the suggested plugins, plus **Docker Pipeline** plugin.
3. Make sure the Jenkins agent has access to `mvn` and `docker` (if Jenkins runs
   in a container, mount the host Docker socket: `-v /var/run/docker.sock:/var/run/docker.sock`).
4. Create a new **Pipeline** job → point "Pipeline script from SCM" at your Git
   repo → script path `Jenkinsfile`.
5. Update the `git` URL in `Jenkinsfile` if your repo URL differs.
6. Click **Build Now**.

The pipeline runs four stages: Checkout → Build & Test with Maven → Build
Docker Image → Deploy Container.

**Screenshot:** the Jenkins pipeline view showing all four stages green/successful.

## Deliverables checklist

- [ ] Git repository URL (pushed with meaningful commits)
- [ ] Source code (this project)
- [ ] `pom.xml`
- [ ] Unit test (`StudentServiceTest.java`)
- [ ] `Dockerfile`
- [ ] `Jenkinsfile`
- [ ] Screenshot: Git repo + commits
- [ ] Screenshot: Maven build/test success
- [ ] Screenshot: Docker image + running container
- [ ] Screenshot: Jenkins pipeline success
