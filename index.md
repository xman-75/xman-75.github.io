---
layout: default
title: Xavier Carter ePortfolio
---

[Home](index.html) | [Code Review](code-review.html) | [Enhancement One](enhancement-one.html) | [Enhancement Two](enhancement-two.html) | [Enhancement Three](enhancement-three.html)

## Professional Self-Assessment

Completing the Computer Science program at Southern New Hampshire University has shaped me into a software engineer who treats code as something other people have to read, trust, and maintain. My coursework moved from foundational programming into web development with the MEAN stack in CS 465, Android architecture in CS 360, testing and quality assurance in CS 320, and reinforcement learning in CS 370, where I built a deep Q-learning agent for a pirate treasure hunt. CYB 200 gave me a cybersecurity foundation that now informs how I read any piece of code. My goal is a career in software engineering and development, and this ePortfolio is built to show the habits that career demands: careful analysis, deliberate trade-offs, secure defaults, and clear communication.

My program was completed through individual work, so I built collaboration skills by writing as if a teammate would inherit every file. That means headers that record the author, date, and intent, comments that explain decisions rather than restate syntax, consistent naming, and a Git repository organized so someone else can find what they need. I also prepared for team review by working through a formal code review checklist covering structure, documentation, defensive programming, and security, and applying it to my own earlier projects. That process mirrors how a review works on a team, where the goal is better code and a better developer, and I now expect to give and receive that kind of feedback in a professional setting.

The code review video in this portfolio was aimed at a peer or manager who needed to understand what the code does, where it falls short, and why each planned change matters, so I focused on intent and decision rather than syntax. My narratives follow a consistent structure of describing the artifact, justifying its inclusion, and reflecting on what the process taught me. The same principle applies to the code, where comments and documentation are there to help the next maintainer.

My work with data structures and algorithms is best shown in the CS 320 service suite. The AppointmentService originally stored appointments in a HashMap keyed by ID, which makes lookups fast but forces a full scan for date range queries. I added a TreeMap indexed by date and built a range query method on top of it, reducing those queries from O(n) to O(log n + k), where k is the number of matching results. The trade-off is extra memory and the responsibility of keeping two indexes in sync on every add and delete, which I handled and verified with new JUnit tests. The full test suite, including my new range query tests, passes, which confirms the optimization did not break existing behavior.

In software engineering and databases, the Travlr Getaways application is my primary example. On the engineering side, I added a missing DELETE endpoint for trips, corrected a bug in the authentication middleware, and introduced centralized error-handling middleware so that errors are logged internally while clients receive safe, generic messages with appropriate status codes. On the database side, I added schema validation, a unique constraint on trip codes, corrected the price per person field from a string to a number, sanitized input in the controller, moved the MongoDB connection to environment variables, and added pagination so a single request can no longer pull an entire collection. Together these changes make the application more robust, efficient, and easier to maintain.

Security runs through all of it. Reading code the way an attacker would changed how I write it. Hardcoded credentials, unvalidated input, verbose error messages, and unbounded queries are all openings, so I fixed them and documented why in comments and narratives. Validating data at the schema and controller level, keeping secrets out of source files, and returning controlled errors align with secure coding practices I studied in the program, and I now treat them as part of the definition of done rather than a final polish step.

The artifacts fit together as a single progression. The CS 320 service suite shows algorithmic reasoning and test-driven verification, while Travlr Getaways shows full stack design, error handling, and data layer security. Whichever one a reviewer opens first, the same habits show up: identify the weakness, choose a justified fix, test it, and explain it clearly. Those habits are what I plan to bring to my first computer science role, and I intend to keep refining those habits throughout my career.

## How the Artifacts Fit Together

The portfolio contains two projects across three enhancements. Travlr Getaways, a MEAN stack travel booking application, is the base for the software engineering and database enhancements. The CS 320 Contact, Task, and Appointment service suite is the base for the algorithms enhancement. Each page below includes the original code, the enhanced code, a narrative, and the course outcomes it supports.

## Portfolio Contents

1. [Code Review](code-review.html): a walkthrough of both original artifacts, my code analysis, and my enhancement plans.
2. [Enhancement One, Software Design and Engineering](enhancement-one.html)
3. [Enhancement Two, Algorithms and Data Structures](enhancement-two.html)
4. [Enhancement Three, Databases](enhancement-three.html)
