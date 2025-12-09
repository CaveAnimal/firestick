# Goals: Migrate UI from React to Java/HTML/JS (Dev Phase 8)

**Objective:** Replace the existing React-based frontend with a traditional server-side rendered application using Spring Boot, HTML, CSS, and vanilla JavaScript. This eliminates the Node.js/Vite dependency.

## High-Level Goals

1.  **Architecture & Setup**
    *   Establish the standard Spring Boot web structure (likely using Thymeleaf for templating).
    *   Configure Spring MVC controllers to serve UI views in addition to the existing REST APIs.
    *   Set up the static asset pipeline (CSS, JS, images) within `src/main/resources/static` and `src/main/resources/templates`.

2.  **Frontend Migration**
    *   **Layouts:** Create a master layout (header, footer, navigation) using server-side templating.
    *   **Views:** Convert key React pages (Search, Indexing Status, Log Viewer) into HTML templates.
    *   **Interactivity:** Replace React state management and hooks with vanilla JavaScript and standard DOM manipulation.
    *   **Styling:** Migrate CSS/SCSS from the React project to standard CSS files served by Spring Boot.

3.  **Integration**
    *   Refactor API interactions:
        *   Use standard HTML Forms for simple data submissions.
        *   Use vanilla `fetch()` for asynchronous updates (e.g., live search results, log streaming) to maintain responsiveness without a framework.
    *   Ensure backend controllers are updated to support both the new view-based routes and necessary data endpoints.

4.  **Cleanup & Optimization**
    *   Remove the `ui/` directory and associated Node.js build artifacts (`package.json`, `node_modules`, `vite.config.ts`).
    *   Update build scripts (`start-all.ps1`, `pom.xml`) to remove UI build steps and dependencies.
    *   Verify application performance and responsiveness.

5.  **Verification**
    *   Test all core workflows (Search, Indexing, Log Viewing) to ensure feature parity with the old React app.
    *   Validate that the application runs successfully in the target environment without any Node.js presence.
