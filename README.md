# RNAB (REE Needs A Budget)


## Introduction
***RNAB***  is a personal budget app that helps you to create, manage, and track your finances. 
I created this app to address some of the limitations I had with the popular *YNAB(You Need A Budget)* app. 
This was also a fun way for me to practice my java and web development skills.


## Installation
1. Clone the repository
   ```
   git clone https://github.com/rheedhar/RNAB
   ```
2. Download docker: [Docker](https://www.docker.com/)
3. Open your terminal, navigate to the project folder. Run the following command: 
   ```
   docker-compose up 
   ```

## Current Features
***RNAB***  follows the ***YNAB*** philosophy of giving every dollar a job. The following features are currently supported

- Creating a budget plan
- Auto-generation of budget plans for future months
- Creating spending categories 
- Assigning money to spending categories 
- Grouping spending categories
- Deciding if you want a modification in a current plan to affect future plan. For example, you can decide to delete a 
spending category or change the target amount for a specific month's plan without it affecting future months
- Creating multiple spending accounts (e.g., checking, savings, credit)
- User authentication

## Technologies
- Language: Java 21
- Framework: Spring Boot
- API: REST API
- Database: JPA/Hibernate with mySQL
- Security: Spring Security with JWT




