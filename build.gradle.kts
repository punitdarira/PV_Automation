plugins {
    id("java")
    id ("application")
}
application {
    mainClass = 'org.gradle.sample.Main'
}
group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-chrome-driver
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.12.0")
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    // https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-support
    implementation("org.seleniumhq.selenium:selenium-support:4.12.0")
    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.0")




}

tasks.test {
    useJUnitPlatform()
}