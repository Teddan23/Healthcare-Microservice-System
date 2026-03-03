# Healthcare Journal System: Microservices & DevOps Showcase 🏥📋

This repository features a distributed healthcare journal system developed as a comprehensive laboratory series in Fullstack Development & DevOps. The project demonstrates a transition from a monolithic architecture to a fully orchestrated microservice environment with a focus on event-driven data flows.

---

## 🧩 System Architecture & Microservices

The system uses a decoupled architecture where services communicate via REST and asynchronous event streaming:

* **`Fullstack-Frontend`**: A responsive web interface (React/SPA) that serves as the entry point for both patients and practitioners. It communicates with the various backends using secure JWT-authenticated requests.
* **`Fullstack-UserService` (Producer)**: Manages user roles and identities. It acts as a **Kafka Producer**, streaming new user registration events to the `patient-topic` in a JSON format.
* **`Fullstack-FhirService` (Consumer)**: Acts as a **Kafka Consumer** that listens for new user events. It processes and transforms these events to adhere to the **HAPI FHIR** international healthcare standard before storing them.
* **`Fullstack-MessageService`**: Facilitates internal communication within the system. It handles the logic for secure messaging between practitioners and patients, ensuring a decoupled flow for clinical notifications.
* **`Fullstack-ImageService` (Node.js)**: A dedicated service for binary data, allowing practitioners to upload and annotate medical imagery.
* **`Fullstack-Quarkus`**: A reactive search service optimized for high-performance querying of patient and practitioner data.

---

## 🛠 Tech Stack & Event-Driven Design

* **Messaging:** **Apache Kafka** for asynchronous data synchronization between User and FHIR services.
* **Security:** Integrated **Keycloak** (OIDC) for centralized authentication and Spring Security for Role-Based Access Control (RBAC).
* **Orchestration:** Fully containerized with **Docker** and deployed using **Kubernetes** on a cloud environment.
* **CI/CD:** Automated pipelines via GitHub Actions for building, containerization, and cloud deployment.

---

## 💡 Insights

In this project, I specifically focused on:
* **Event-Driven Synchronization:** Implementing a robust Producer-Consumer pattern to ensure that patient data remains consistent across multiple distributed databases.
* **Data Transformation:** Handling complex JSON serialization/deserialization, specifically managing Java 8 Date/Time types across different service environments.
* **Cloud Native Deployment:** Managing the lifecycle of microservices in a Kubernetes cluster, including service discovery and environment configuration.

---

## 👥 Collaboration
This project was developed as a collaborative effort by:
* [Theodor Darra](https://github.com/Teddan23)
* [Michel Wu](https://github.com/Mochi-chel)

---

## Notes
* For this educational showcase, some infrastructure URLs (Keycloak, Frontend origins) remain hardcoded in the configuration files. In a production-grade system, these would be moved to environment variables or a centralized configuration server.