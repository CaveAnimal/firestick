# Assistant Proceed Button (VS Code Extension)

This extension adds a simple chat participant that suggests a clickable **Proceed** follow-up after every response. It also includes a command to copy a prebuilt "Proceed" prompt.

## Features
- Chat participant "Proceed Assistant" with a **Proceed** follow-up (if chat participant API is available in your VS Code version)
- Command: `Assistant Proceed: Insert Proceed Prompt` — Copies a ready-to-use Proceed prompt text to clipboard

## Install & Run (Development)
1. Open this folder in VS Code: `tools/assistant-proceed-extension`
2. Install dependencies:
```powershell
npm install
```
3. Build:
```powershell
npm run compile
```
4. Launch the Extension Development Host (Press F5).
5. Open the Chat view, choose the participant "Proceed Assistant", send any message.
   - You should see a **Proceed** follow-up chip after the response.

## Notes
- If your VS Code version does not yet expose the `vscode.chat.createChatParticipant` API, the participant will be skipped. You can still use the command to quickly insert the Proceed prompt into chat.
- To package the extension:
```powershell
npm run package
```
- Then install the generated `.vsix` via the Extensions view (gear icon > Install from VSIX...).
