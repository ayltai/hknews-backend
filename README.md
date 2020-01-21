# HK News Backend

| Category      | Measurement                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Pipeline      | [![CircleCI](https://img.shields.io/circleci/project/github/ayltai/hknews-backend/master.svg?style=flat)](https://circleci.com/gh/ayltai/hknews-backend)                                                                                                                                                                                                                                                                                                                                                                                                                     |
| Quality       | [![Code Quality](https://img.shields.io/codacy/grade/2c5a8d9d71ca4da794494c08bffdd73a.svg?style=flat)](https://app.codacy.com/app/AlanTai/hknews-backend/dashboard) [![LGTM Grade](https://img.shields.io/lgtm/grade/java/github/ayltai/hknews-backend)](https://lgtm.com/projects/g/ayltai/hknews-backend/context:java) [![Sonar Quality Gate](https://img.shields.io/sonar/quality_gate/ayltai_hknews-backend?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_hknews-backend) [![Sonar Violations (short format)](https://img.shields.io/sonar/violations/ayltai_hknews-backend?format=short&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_hknews-backend)  |
| Coverage      | [![Sonar Test Success Rate](https://img.shields.io/sonar/test_success_density/ayltai_hknews-backend?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_hknews-backend) [![Code Coverage](https://img.shields.io/codacy/coverage/2c5a8d9d71ca4da794494c08bffdd73a.svg?style=flat)](https://app.codacy.com/app/AlanTai/hknews-backend/dashboard) [![Code Coverage](https://img.shields.io/codecov/c/github/ayltai/hknews-backend.svg?style=flat)](https://codecov.io/gh/ayltai/hknews-backend) [![Sonar Coverage](https://img.shields.io/sonar/coverage/ayltai_hknews-backend?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_hknews-backend)                        |
| Ratings       | [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ayltai_hknews-backend&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=ayltai_hknews-backend) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ayltai_hknews-backend&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=ayltai_hknews-backend) [![Sonar Tech Debt](https://img.shields.io/sonar/tech_debt/ayltai_hknews-backend?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=ayltai_hknews-backend)                                                                                                                                                         |
| Security      | [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ayltai_hknews-backend&metric=security_rating)](https://sonarcloud.io/dashboard?id=ayltai_hknews-backend) [![CII Best Practices Tiered Percentage](https://img.shields.io/cii/percentage/2686)](https://bestpractices.coreinfrastructure.org/projects/2686) [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=ayltai_hknews-backend&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=ayltai_hknews-backend)                                                                                                                                                                                                 |
| Miscellaneous | [![Uptime Robot status](https://img.shields.io/uptimerobot/status/m783235303-dd3e7baceda2ae13eb1881cd)](https://stats.uptimerobot.com/8o3Erh6PyD) [![Swagger Validator](https://img.shields.io/swagger/valid/2.0/https/raw.githubusercontent.com/ayltai/hknews-backend/master/swagger.yaml)](https://app.swaggerhub.com/apis-docs/ayltai/hknews-backend/1.0.0) ![Maintenance](https://img.shields.io/maintenance/yes/2020) [![Release](https://img.shields.io/github/release/ayltai/hknews-backend.svg?style=flat)](https://github.com/ayltai/hknews-backend/releases) [![License](https://img.shields.io/github/license/ayltai/hknews-backend.svg?style=flat)](https://github.com/ayltai/hknews-backend/blob/master/LICENSE) |

Serves aggregated news from 10+ local news publishers in Hong Kong. Made with ❤

## Features
* Support text, image and video news
* Read news from 10+ local news publishers
* No ads. We hate ads as much as you do

## News Publishers
* [Apple Daily (蘋果日報)](http://hk.apple.nextmedia.com)
* [Oriental Daily (東方日報)](http://orientaldaily.on.cc)
* [Sing Tao (星島日報)](http://std.stheadline.com)
* [Hong Kong Economic Times (經濟日報)](http://www.hket.com)
* [Sing Pao (成報)](https://www.singpao.com.hk)
* [Ming Pao (明報)](http://www.mingpao.com)
* [Headline (頭條日報)](http://hd.stheadline.com)
* [Sky Post (晴報)](http://skypost.ulifestyle.com.hk)
* [Hong Kong Economic Journal (信報)](http://www.hkej.com)
* [RTHK (香港電台)](http://news.rthk.hk)
* [South China Morning Post (南華早報)](http://www.scmp.com/frontpage/hk)
* [The Standard (英文虎報)](http://www.thestandard.com.hk)
* [Wen Wei Po (文匯報)](http://news.wenweipo.com)

## API Documentation
[View interactive API documentation](https://app.swaggerhub.com/apis-docs/ayltai/hknews-backend/1.0.0)

## HK News Frontend
* [hknews-android](https://github.com/ayltai/hknews-android)
* [hknews-web](https://github.com/ayltai/hknews-web)

## HK News Infrastructure
[hknews-infrastructure](https://github.com/ayltai/hknews-infrastructure)

## Installation
1. Install [JDK 11](https://openjdk.java.net/install)<sup>*</sup>
2. Install [MongoDB](https://docs.mongodb.com/manual/installation) 4.x
3. Start MongoDB daemon
   ```sh
   mongod
   ```
4. Build the project
   ```sh
   ./build.sh
   ```

<sup>*</sup> JDK 8, 9 and 10 also work. JDK 11 is used here for better runtime performance.

### How to run for development environment
1. Start the server
   ```sh
   export SPRING_PROFILES_ACTIVE=development
   ./gradlew bootRun
   ```
2. Test specific API
   ```sh
   curl http://localhost:8080/sources
   ```

### How to run for production environment
1. Export SSL certificate key for using HTTPS
   ```sh
   export SSL_KEY=x123y456
   ```
2. Export Instrumental API key for using sending system metrics to cloud service
   ```sh
   export INSTRUMENTAL_API_KEY=a111b222
   ```
3. Export Logz.io token for using sending application logs to cloud service
   ```sh
   export LOGZIO_TOKEN=abcdef
   ```
4. Start the server
   ```sh
   java -server -Xverify:none -Xms256m -Xmx512m -Xss512k -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark -Dfile.encoding=UTF-8 -Dserver.ssl.key-store-password=${SSL_KEY} -Dserver.ssl.trust-store-password=${SSL_KEY} -Dspring.profiles.active=production -jar ./build/libs/hknews-backend-1.2.3.jar
   ```
5. Test specific API
   ```sh
   curl https://hknews.dev/sources
   ```

## Acknowledgements
This software is made with the support of open source projects:
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data](https://spring.io/projects/spring-data)
* [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
* [Undertow](http://undertow.io)
* [OkHttp](http://square.github.io/okhttp)
* [Retrofit](https://github.com/square/retrofit)
* [Apache Commons Lang](https://commons.apache.org/proper/commons-lang)
* [Project Lombok](https://projectlombok.org)
* [JSON](https://json.org)
* [Gson](https://github.com/google/gson)
* [JUnit](https://junit.org/junit4)
* [Mockito](https://site.mockito.org)
* [Checkstyle](http://checkstyle.sourceforge.net)
* [SpotBugs](https://spotbugs.github.io)
* [JaCoCo](https://www.jacoco.org/jacoco)
* [OWASP Dependency Check](https://www.owasp.org/index.php/OWASP_Dependency_Check)

... and closed source services:
* [CircleCI](https://circleci.com)
* [SonarCloud](https://sonarcloud.io)
* [Instrumental](https://instrumentalapp.com)
* [Logz.io](https://logz.io):
