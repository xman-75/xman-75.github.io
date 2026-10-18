---
layout: default
title: Code Review
---

[Home](index.html) | [Code Review](code-review.html) | [Enhancement One](enhancement-one.html) | [Enhancement Two](enhancement-two.html) | [Enhancement Three](enhancement-three.html)

# Code Review

This code review is the starting point for the three enhancements in this portfolio. It is aimed at a peer or manager, and it walks through each artifact in three parts: how the existing code works, what the analysis found, and how I planned to enhance it. It covers Travlr Getaways for the software engineering and database categories, and the CS 320 Appointment service for the algorithms category.

[Watch the code review video](https://youtu.be/GNQs2KzJD0c)

## What the Review Covers

For software design and engineering, I analyzed the Travlr Getaways API, where I found a missing DELETE endpoint, a response-chaining bug in the authenticateJWT middleware, and an undeclared error variable in four failure branches. For algorithms and data structures, I analyzed AppointmentService, whose HashMap storage gives fast lookups by ID but forces a full scan for date range queries. For databases, I analyzed the Travlr connection, schema, and trips controller, where I found an unauthenticated connection string, a missing unique constraint, prices stored as text, unvalidated request data, and an unbounded query that returns the whole collection.

For each finding I planned a matching enhancement. For the Travlr API, I planned to add the missing DELETE endpoint, fix the authentication bug, and add centralized error handling. For the Appointment service, I planned to add a date-indexed TreeMap with a range query method. For the Travlr database layer, I planned to add schema validation, input sanitization, an authenticated connection that reads its settings from environment variables, and pagination.
