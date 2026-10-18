---
layout: default
title: Enhancement One
---

[Home](index.html) | [Code Review](code-review.html) | [Enhancement One](enhancement-one.html) | [Enhancement Two](enhancement-two.html) | [Enhancement Three](enhancement-three.html)

# Enhancement One: Software Design and Engineering

Artifact: Travlr Getaways API (CS 465)

Code: [Original code](https://github.com/xman-75/xman-75.github.io/tree/main/Enhancement1/original_code) | [Enhanced code](https://github.com/xman-75/xman-75.github.io/tree/main/Enhancement1/new_code)

Narrative: [Word version](https://github.com/xman-75/xman-75.github.io/tree/main/Enhancement1/narrative)

## About the Artifact

The artifact I'm enhancing is the API layer of Travlr Getaways, a trip booking application I built across multiple milestones in CS 465 in June 2026 . It's a Node.js and Express backend using Mongoose to talk to MongoDB, with routes for logging in, registering, and listing, adding, updating, and now deleting trips. I originally built it as part of a MEAN stack course project, and the version I'm enhancing here reflects the state it was in when I selected it for this capstone.

## Why I Included It and What I Changed

I picked this artifact because it gave me a real API layer with problems worth fixing, rather than something that already worked cleanly. During my code review, I found that the routes file had no DELETE endpoint even though the app supported full create, read, and update operations, which meant CRUD was incomplete. I also found a bug in the authenticateJWT middleware where a failed token check called res.sendStatus(401) and then chained .json onto it, which tries to send a second response on a request that already got one and throws a headers-already-sent error at runtime instead of cleanly rejecting the request. On top of that, four separate functions across two controllers, tripsList, tripsFindByCode, tripsAddTrip in trips.js, and register in authentication.js, all referenced an err variable in their failure branches that was never declared anywhere in the function, so instead of returning a proper error response they would throw a reference error if that branch ever executed.

To address these, I added the missing DELETE route and controller function so the trips resource supports full CRUD. I fixed the authenticateJWT bug so a failed token check returns one clean 401 instead of crashing. And instead of patching each of the four broken error branches individually, I built a centralized error-handling middleware function, mounted in app.js right after the API router, that every controller under /api now forwards errors to using next(err). The handler logs the real error server-side for debugging, then returns a generic, safe message to the client, 400 for validation errors, 401 for authentication errors, and 500 as a catch-all for anything else. That replaces four inconsistent and mostly broken error paths with one predictable one, and it means any future route added to this API automatically gets the same error handling for free instead of needing its own hand-rolled version.

## Course Outcomes

This enhancement was planned in Module One to align with the outcome around using well-founded and innovative techniques, skills, and tools in computing practices for the purpose of implementing computer solutions that deliver value and accomplish industry-specific goals, and I believe it met that outcome. Completing CRUD coverage and replacing four broken or missing error paths with one centralized, maintainable handler is a direct improvement to whether the application works the way an API layer is supposed to. I didn't need to update my outcome-coverage plan for this category, since the enhancement stayed within the scope I originally proposed in the code review.

## Reflection

Working through this enhancement showed me how much of a difference it makes to have one consistent place for error handling instead of scattering it across every function. When I originally wrote this API, I was mostly focused on getting each route to work on its own, so every controller ended up handling failures a little differently, and in multiple cases, not handling them at all. Fixing this artifact meant going back through code I wrote previously and seeing it with a more critical eye, especially the unauthorized-error block in app.js, which I realized never actually responded to most errors because it only matched one specific error name. That was a good reminder that code can look like it is handling something without doing it. A code review needs to trace what happens when things go wrong, not just when they go right. The centralized handler feels like a more mature way to approach this than what I originally had, and it is something I will carry into future projects.
