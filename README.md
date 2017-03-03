# ODK Codebook
![Platform](https://img.shields.io/badge/platform-Java-blue.svg)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build status](https://circleci.com/gh/nafundi/odk-codebook.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/nafundi/odk-codebook)

ODK Codebook is a desktop Java application that generates a human-friendly PDF when given a compliant [ODK XForm](http://opendatakit.github.io/xforms-spec).
   
## Setting up your development environment

1. Fork the validate project ([why and how to fork](https://help.github.com/articles/fork-a-repo/))

1. Clone your fork of the project locally. At the command line:

        git clone https://github.com/YOUR-GITHUB-USERNAME/validate

We recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/) for development. On the welcome screen, click `Import Project`, navigate to your validate folder, and select the `build.gradle` file. Use the defaults through the wizard. Once the project is imported, IntelliJ may ask you to update your remote maven repositories. Follow the instructions to do so. 

The main class is `com.nafundi.opendatakit.codebook.ui.Main`.
 
## Running the project
 
To run the project, go to the `View` menu, then `Tool Windows > Gradle`. `run` will be in `odk-codebook > Tasks > application > run`. Double-click `run` to run the application. This Gradle task will now be the default action in your `Run` menu. 

You must use the Gradle task to run the application because there is a generated class (`BuildConfig`) that IntelliJ may not properly import and recognize.

To package a runnable jar, use the `jar` Gradle task.

## Contributing code
Any and all contributions to the project are welcome.

If you're ready to contribute code, see [the contribution guide](CONTRIBUTING.md).

## Downloading builds
Per-commit debug builds can be found on [CircleCI](https://circleci.com/gh/nafundi/odk-codebook). Login with your GitHub account, click the build you'd like, then find the JAR in the Artifacts tab.