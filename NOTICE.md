# Notice for Docker image

This document provides important information related to the Docker image published for this project.  
The image is provided **for demonstration and development purposes only**.

## Docker image

Container registry:  
https://hub.docker.com/r/<your-org-or-username>/<your-image-name>

## Information about your code

**<your product name>**

- Project home: https://github.com/<your-org-or-username>/<your-repo>
- GitHub repository: https://github.com/<your-org-or-username>/<your-repo>
- Dockerfile: https://github.com/<your-org-or-username>/<your-repo>/blob/main/<path-to-Dockerfile>
- Project license: Apache License, Version 2.0  
  https://github.com/<your-org-or-username>/<your-repo>/blob/main/LICENSE

⚠️ The project license applies to the **source code** of the project and does **not** represent a license for the Docker image itself.

## Information about 3rd-party components

This Docker image includes third-party dependencies such as system libraries, runtime environments, and language-specific packages required to run the application.

These components are provided under their respective licenses. For more details, please refer to the documentation of the individual components and the base image.

If package or vulnerability scans are performed for this image, the results will be published separately.

## Information about the base image

**Used base image**

- Docker image: `eclipse-temurin:20-jre-alpine`
- Official DockerHub page: https://hub.docker.com/_/eclipse-temurin
- Project home: https://projects.eclipse.org/projects/adoptium.temurin
- Source repository: https://github.com/adoptium/containers
- Additional image information: https://github.com/docker-library/repo-info/tree/master/repos/eclipse-temurin

## General notice

As with all Docker images, this image may contain additional software which is subject to other licenses  
(e.g. shell utilities, system libraries, or indirect dependencies provided by the base image).

This Docker image is intended **for demo and experimental purposes only**.  
It is the responsibility of the image user to ensure that any use of this image complies with all applicable licenses, legal requirements, and security best practices.