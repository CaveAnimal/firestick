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

- [ ] **Add Thymeleaf Dependency**
    - [ ] Open `pom.xml`.
    - [ ] Add `spring-boot-starter-thymeleaf`.
    - [ ] Run `mvn clean install` to make sure it downloads. Don't change other dependencies.
- [ ] **Create Folder Structure**
    - [ ] Ensure `src/main/resources/templates` exists (for HTML).
    - [ ] Ensure `src/main/resources/static` exists (for CSS/JS).
    - [ ] Create `src/main/resources/static/css` and `src/main/resources/static/js`.
- [ ] **Create Home Controller**
    - [ ] Create a new Java class `com.codetalker.firestick.controller.WebController`.
    - [ ] Add a method mapped to `/` that returns the string `"index"`.
    - [ ] Create a simple `src/main/resources/templates/index.html` that says "Hello World".
    - [ ] Run the app and verify you see "Hello World" at `http://localhost:8080`.

## 2. Frontend Migration - Layouts & Assets
*Focus: Copy the look, don't reinvent it.*

- [ ] **Migrate CSS**
    - [ ] Go to the old `ui/src` folder.
    - [ ] Copy the core styles to `src/main/resources/static/css/style.css`.
    - [ ] Simplify them. We don't need Tailwind or complex build steps. Just standard CSS.
- [ ] **Create Master Layout**
    - [ ] Create `src/main/resources/templates/layout.html` (or use Thymeleaf fragments).
    - [ ] Add the Header (Logo, Navigation Links).
    - [ ] Add the Footer.
    - [ ] Ensure every page we build later uses this layout.

## 3. Frontend Migration - Views
*Focus: One HTML file per page. Keep JavaScript simple.*

- [ ] **Migrate Search Page**
    - [ ] Update `index.html` to include the Search Bar.
    - [ ] Add a `<div>` to hold search results.
    - [ ] Write a vanilla JS function in `static/js/app.js` to:
        - [ ] Listen for the "Search" button click.
        - [ ] Call `fetch('/api/search?q=...')`.
        - [ ] Clear the results div.
        - [ ] Loop through the JSON response and append HTML elements for each result.
    - [ ] **Do not** use React, Vue, or jQuery. Just `document.getElementById` and `fetch`.
- [ ] **Migrate Indexing Status Page**
    - [ ] Create `src/main/resources/templates/indexing.html`.
    - [ ] Add "Start Indexing" and "Stop Indexing" buttons.
    - [ ] Add a status area.
    - [ ] Write JS to call the existing indexing APIs.
    - [ ] Add a simple polling interval (e.g., `setInterval`) to check status every 2 seconds.
- [ ] **Migrate Log Viewer Page**
    - [ ] Create `src/main/resources/templates/logs.html`.
    - [ ] Add a text area or preformatted block to show logs.
    - [ ] Write JS to fetch the latest logs and dump them into the block.

## 4. Cleanup & Optimization
*Focus: Delete the old stuff. It might feel scary, just do it.*

- [ ] **Remove React UI**
    - [ ] Delete the entire `ui/` directory. Yes, all of it.
    - [ ] Delete `package.json` in the root if it exists (only if it was for the UI).
- [ ] **Update Build Scripts**
    - [ ] Open `start-all.ps1`.
    - [ ] Remove the section that starts the Vite/Node server.
    - [ ] Remove the `-SkipUI` flag logic since UI is now part of the Backend.
    - [ ] Open `pom.xml` and remove any frontend-maven-plugin configurations if present.

## 5. Verification
*Focus: Prove it works.*

- [ ] **Test Search**
    - [ ] Run the app. Search for "test". Verify results appear.
- [ ] **Test Indexing**
    - [ ] Click "Start Indexing". Verify the status updates.
- [ ] **Test Logs**
    - [ ] Open the logs page. Verify you see text.
- [ ] **Verify No Node.js**
    - [ ] Stop all processes.
    - [ ] Run `start-all.ps1`.
    - [ ] Ensure no `node.exe` processes are spawned.
