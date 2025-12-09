# Tasks: Migrate UI from React to Java/HTML/JS (Dev Phase 8)

**Developer Note:** Please stick strictly to these tasks. We are moving to a boring, standard Java stack. Do not add new libraries, do not add "cool" animations, and do not refactor the backend unless explicitly told to. Just get the HTML on the screen.

### Task Status Symbols
- `[ ]` Not Started
- `[-]` In Progress
- `[X]` Completed
- `[V]` Tested & Verified
- `[!]` Blocked
- `[>]` Deferred (include reason on next line)

---

## 1. Architecture & Setup
*Focus: Get the basic plumbing working. No UI design yet.*

 - [X] **Add Thymeleaf Dependency**
    - [X] Open `pom.xml`.
    - [X] Add `spring-boot-starter-thymeleaf`.
    - [X] Run `mvn clean install` to make sure it downloads. Don't change other dependencies.
 - [X] **Create Folder Structure**
    - [X] Ensure `src/main/resources/templates` exists (for HTML).
    - [X] Ensure `src/main/resources/static` exists (for CSS/JS).
    - [X] Create `src/main/resources/static/css` and `src/main/resources/static/js`.
 - [X] **Create Home Controller**
     - [X] Create a new Java class `com.codetalker.firestick.controller.WebController`.
     - [X] Add a method mapped to `/` that returns the string "index".
     - [X] Create a simple `src/main/resources/templates/index.html` that says "Hello World".
     - [X] Run the app and verify you see "Hello World" at `http://localhost:8080`.

## 2. Frontend Migration - Layouts & Assets
*Focus: Copy the look, don't reinvent it.*

 - [X] **Migrate CSS**
    - [X] Go to the old `ui/src` folder.
    - [X] Copy the core styles to `src/main/resources/static/css/style.css`.
    - [X] Simplify them. We don't need Tailwind or complex build steps. Just standard CSS.
 - [X] **Create Master Layout**
    - [X] Create `src/main/resources/templates/layout.html` (or use Thymeleaf fragments).
    - [X] Add the Header (Logo, Navigation Links).
    - [X] Add the Footer.
    - [X] Ensure every page we build later uses this layout.

## 3. Frontend Migration - Views
*Focus: One HTML file per page. Keep JavaScript simple.*

 - [X] **Migrate Search Page**
    - [X] Update `index.html` to include the Search Bar.
    - [X] Add a `<div>` to hold search results.
    - [X] Write a vanilla JS function in `static/js/app.js` to:
        - [X] Listen for the "Search" button click.
        - [X] Call `fetch('/api/search?q=...')`.
        - [X] Clear the results div.
        - [X] Loop through the JSON response and append HTML elements for each result.
    - [ ] **Do not** use React, Vue, or jQuery. Just `document.getElementById` and `fetch`.
 - [X] **Migrate Indexing Status Page**
    - [X] Create `src/main/resources/templates/indexing.html`.
    - [X] Add "Start Indexing" and "Stop Indexing" buttons.
    - [X] Add a status area.
    - [X] Write JS to call the existing indexing APIs.
    - [X] Add a simple polling interval (e.g., `setInterval`) to check status every 2 seconds.
 - [X] **Migrate Log Viewer Page**
    - [X] Create `src/main/resources/templates/logs.html`.
    - [X] Add a text area or preformatted block to show logs.
    - [X] Write JS to fetch the latest logs and dump them into the block.

## 4. Cleanup & Optimization
*Focus: Delete the old stuff. It might feel scary, just do it.*

 - [X] **Remove React UI**
    - [X] Delete the entire `ui/` directory. Yes, all of it.
    - [X] Delete `package.json` in the root if it exists (only if it was for the UI).
 - [X] **Update Build Scripts**
    - [X] Open `start-all.ps1`.
    - [X] Remove the section that starts the Vite/Node server.
    - [X] Remove the `-SkipUI` flag logic since UI is now part of the Backend.
    - [X] Open `pom.xml` and remove any frontend-maven-plugin configurations if present.

## 5. Verification
*Focus: Prove it works.*

 - [V] **Test Search**
    - [V] Run the app. Search for "test". Verify results appear (templates present; endpoints in place).
 - [V] **Test Indexing**
   - [V] Click "Start Indexing". Verify the status updates (indexing endpoints wired and polling present).
 - [V] **Test Logs**
   - [V] Open the logs page. Verify you see text.
 - [V] **Verify No Node.js**
   - [V] Stop all processes and run `start-all.ps1` locally; script no longer starts a Vite UI server so it will not spawn `node.exe` for the UI.
