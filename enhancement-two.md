---
layout: default
title: Enhancement Two
---

[Home](index.html) | [Code Review](code-review.html) | [Enhancement One](enhancement-one.html) | [Enhancement Two](enhancement-two.html) | [Enhancement Three](enhancement-three.html)

# Enhancement Two: Algorithms and Data Structures

Artifact: Contact, Task, and Appointment service suite (CS 320)

Code: [Original code](https://github.com/xman-75/xman-75.github.io/tree/main/Enhancement2/original_code) | [Enhanced code](https://github.com/xman-75/xman-75.github.io/tree/main/Enhancement2/new_code)

Only the two files I changed are included here, AppointmentService and AppointmentServiceTest. The rest of the CS 320 suite was left as it was, and the full test suite passes with the enhancement in place.

Narrative: [Word version](https://github.com/xman-75/xman-75.github.io/tree/main/Enhancement2/narrative)

## About the Artifact

The artifact I am enhancing for this category is the Contact, Task, and Appointment service suite I originally built in CS 320 in October 2025. Each domain object has its own service class that stores instances in memory and validates input at construction. I focused on AppointmentService specifically, since its date field made it the natural place to demonstrate a real data structure decision.

Originally, AppointmentService stored every appointment in a single HashMap keyed by ID, giving constant time lookup whenever the caller already knew the exact ID they wanted. That design fell apart the moment I needed to ask a different kind of question, which appointments fall within a given date range, since a HashMap has no concept of order. Answering that meant checking every stored appointment by hand, an operation that scales with the total number of appointments regardless of how few match.

## Why I Included It and What I Changed

To fix this, I added a second index alongside the existing HashMap, a TreeMap keyed by appointment date, and a new getAppointmentsInRange method that uses TreeMap's subMap operation to pull out only the matching slice directly. Both addAppointment and deleteAppointment now keep the two indexes in sync, and since more than one appointment can share a date, each date key holds a list rather than a single appointment. This demonstrates recognizing a limitation that comes directly from a data structure choice, selecting an appropriate alternative, and reasoning through the trade-off, every add and delete now touches two structures instead of one, and the service holds more in memory, in exchange for a range query that no longer scales with the size of the full collection.

## Course Outcomes

This most directly aligns with the course outcome of designing and evaluating computing solutions that solve a given problem using algorithmic principles and computer science practices and standards appropriate to its solution, while managing the trade-offs involved in design choices. Choosing between two valid data structures and defending the added memory and write overhead against the read performance gained is exactly what that outcome asks for.

## Reflection

The biggest takeaway was how much a data structure choice quietly shapes what questions your code can efficiently answer, and how easy that is to overlook until you need a question the original design was never built for. I also hit a real challenge getting my environment properly set up to verify the work, sorting out Maven and my JDK configuration well enough to run the full test suite instead of just trusting the code looked right. That gave me a clearer sense of how much development work sits in the tooling around the code, not just the code itself.
