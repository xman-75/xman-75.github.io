---
layout: default
title: Enhancement Three
---

[Home](index.html) | [Code Review](code-review.html) | [Enhancement One](enhancement-one.html) | [Enhancement Two](enhancement-two.html) | [Enhancement Three](enhancement-three.html)

# Enhancement Three: Databases

Artifact: Travlr Getaways database layer (CS 465)

Code: [Original code](https://github.com/xman-75/xman-75.github.io/tree/main/Enhancement3/original_code) | [Enhanced code](https://github.com/xman-75/xman-75.github.io/tree/main/Enhancement3/new_code)

## About the Artifact

Travlr Getaways is a MEAN stack travel booking application I built in CS 465 in June 2026. It includes a customer-facing site, an Angular admin application, and an Express API backed by MongoDB. For this milestone I enhanced the database layer, which consists of the connection file (db.js), the trip schema (travlr.js), and the trips controller (trips.js).

## Why I Included It and What I Changed

I selected this artifact because the original database layer worked but trusted its inputs and its environment more than it should have. The connection string was built directly in code, the schema accepted strings of any length, trip codes were not unique, prices were stored as text, and the trips listing returned the entire collection in a single query. Those weaknesses made it a strong candidate for showing database skills. The connection now reads its settings from environment variables loaded through dotenv and authenticates with a dedicated MongoDB user limited to readWrite access on the travlr database, so no credentials appear in source code or console output. The schema enforces required fields, trimmed strings, maximum lengths, a unique trip code, and a numeric price with a minimum of zero. The controller whitelists the fields it accepts, rejects objects and arrays so injection payloads can't reach a query, strips angle brackets from text, applies schema validation on updates, and returns trips in sorted pages with a capped page size and a total count header instead of one unbounded query.

## Course Outcomes

These changes support two of the course outcomes I planned in Module One. The work develops a security mindset that anticipates adversarial exploits in software architecture and designs, mitigates design flaws, and ensures privacy and enhanced security of data and resources, through input sanitization, least-privilege database access, and keeping secrets out of the codebase. It also demonstrates well-founded techniques, skills, and tools in computing practices that deliver value, since schema constraints protect data integrity and pagination keeps response size and memory use stable as the collection grows. The headers and comments added to each file contribute to the professional communication outcome as well. My outcome plan did not change from Module One.

## Reflection

The most useful lesson was how much database work depends on details outside the code itself. The MongoDB Shell was not installed, so I had to add it before I could create the application user. The dotenv startup message reported one loaded variable when I expected six, which showed me that configuration should be verified rather than assumed. I also learned that Mongoose skips schema validation on updates unless runValidators is enabled, and that a local MongoDB server does not enforce authentication until access control is turned on in its configuration. Because of that, my authenticated connection works but is not enforced server wide. A second limitation is that I did not migrate the documents already stored in the database. They still hold prices as text, which Mongoose casts to numbers when reading, so the application behaves correctly, but a one-time conversion would make the stored data match the new schema. In future work I would run that migration, enable access control on the server, and add automated tests for the sanitizer and pagination logic.
