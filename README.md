# Project Three: A Two-Tier Client-Server Application Using MySQL and JDBC

## Overview

This project is designed to create two Java-based GUI applications that interact with a MySQL database via JDBC, focusing on client-server interactions. The first application allows clients to execute SQL commands, while the second is a specialized accountant-level client for querying transaction logs.

- **Course**: CNT 4714 Fall 2024
- **Assignment Title**: Project 3 – A Two-Tier Client-Server Application
- **Due Date**: October 20, 2024
- **Instructor**: Prof. John Doe

---

## Table of Contents

1. [Objectives](#objectives)
2. [Technologies Used](#technologies-used)
3. [Features](#features)
4. [User Instructions](#user-instructions)
5. [Application Design](#application-design)
    - [Main Client Application](#main-client-application)
    - [Accountant Application](#accountant-application)
6. [Database Setup](#database-setup)
7. [Screenshots](#screenshots)
8. [Deliverables](#deliverables)

---

## Objectives

The project focuses on developing two-tier Java client-server applications that connect to a MySQL database using JDBC. It emphasizes learning how JDBC handles SQL operations and ensuring that multiple clients with different permissions can interact with a MySQL DB server simultaneously.

## Technologies Used

- **Java**: GUI development (Swing)
- **MySQL**: Database server
- **JDBC**: Database connectivity
- **Properties Files**: Store user credentials and database connection information
- **Git**: Version control
- **NetBeans or IntelliJ IDEA**: IDEs for development

## Features

1. **Multiple Clients**: The application allows multiple client users with different levels of permissions to connect to the MySQL server.
2. **SQL Execution**: Users can execute SQL DML and DDL commands from the GUI (select, insert, update, delete).
3. **Prepared Statements**: The project uses JDBC’s PreparedStatement interface for issuing SQL commands securely.
4. **Transaction Logging**: All SQL operations are logged into a separate `operationslog` database.
5. **Accountant Client**: A specialized GUI for accountant-level users to view transaction logs.
6. **Error Handling**: Users are notified of any SQL execution errors, and invalid credentials prevent connections to the database.

---

## User Instructions

1. **Setting Up the Application**:
   - Download the source code.
   - Use the provided scripts to set up the MySQL databases (`project3` and `operationslog`).
   - Create and configure user accounts in MySQL (root, client1, client2, project3app, theaccountant).
   
2. **Running the Client Application**:
   - Open the `MainClientApplication.java` file and run the application.
   - Enter the appropriate username and password from the GUI.
   - Select a database, enter an SQL command, and press "Execute."
   - The results or status will be displayed in the result area.

3. **Running the Accountant Application**:
   - Open the `AccountantApplication.java` file and run the application.
   - Enter the `theaccountant` credentials, connect to the `operationslog` database, and run queries related to transaction logging.

---

## Application Design

### Main Client Application

- **File**: `MainClientApplication.java`
- **Purpose**: General clients use this application to issue SQL commands on the `project3` and `bikedb` databases.
- **Functionality**:
  - GUI interface for selecting a properties file to establish database connections.
  - Executes SQL queries and displays results.
  - Logs all transactions (successful queries and updates) into the `operationslog` database.
  
### Accountant Application

- **File**: `AccountantApplication.java`
- **Purpose**: This application is specialized for an accountant-level user to monitor transaction logs in the `operationslog` database.
- **Functionality**:
  - Allows querying of the transaction logging database.
  - Does not permit modification or access to other databases.
  
---

## Database Setup

1. **Create Databases**:
   - Run the `project3dbscript.sql` to create the `project3` database.
   - Run the `project3operationslog.sql` to create the `operationslog` database.

2. **Create Client Users**:
   - Execute the `clientCreationScriptProject3.sql` script to create the following MySQL users: `client1`, `client2`, `project3app`, and `theaccountant`.
   
3. **Assign Permissions**:
   - Use the `clientPermissionsScriptProject3.sql` to grant each client specific permissions to interact with the databases:
     - `client1`: Select permissions on `project3` and `bikedb`.
     - `client2`: Select and update permissions on `project3` and `bikedb`.
     - `project3app`: Select, insert, update on `operationslog`.
     - `theaccountant`: Select on `operationslog`.

---

## Screenshots

1. **Client Application**: Running SQL commands for the `client1` and `client2` users.
2. **Accountant Application**: Querying transaction logs.
3. **Error Handling**: Displaying an invalid credentials message.

---

## Deliverables

- Source Code Folder: Contains all `.java` files.
- Screenshots Folder:
  - `RootCommandsScreenshots`: 17 screenshots from the `project3rootscript.sql`.
  - `Client1CommandsScreenshots`: 11 screenshots from the `project3client1script.sql`.
  - `Client2CommandsScreenshots`: 11 screenshots from the `project3client2script.sql`.
  - `Accountant-OperationsLogScreenshots`: 3 screenshots from the accountant interface.
  - `CredentialsMismatchScreenshot`: 1 screenshot showing an invalid login attempt.
  
---

## Conclusion

This project demonstrates the development of a two-tier Java client-server system using MySQL and JDBC, with multiple client permissions, transaction logging, and a specialized monitoring application for accountant-level users. Follow the setup instructions and ensure that MySQL is configured properly to run the applications successfully.
