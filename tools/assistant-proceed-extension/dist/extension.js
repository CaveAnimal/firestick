"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.activate = activate;
exports.deactivate = deactivate;
const vscode = __importStar(require("vscode"));
function activate(context) {
    // Add a Status Bar "Proceed" button as a fallback for environments without chat participant UI
    const statusProceed = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Right, 1000);
    statusProceed.text = '$(debug-continue) Proceed';
    statusProceed.tooltip = 'Insert the Proceed prompt into your clipboard to paste into chat';
    statusProceed.command = 'assistantProceed.insertProceed';
    statusProceed.show();
    context.subscriptions.push(statusProceed);
    // Register a simple command that inserts a "Proceed" prompt in the active chat input (best-effort)
    const insertProceed = vscode.commands.registerCommand('assistantProceed.insertProceed', async () => {
        try {
            // Best-effort: put text into the editor or chat input (fallback to clipboard/info message)
            const proceedPrompt = 'Proceed to the next recommended step with minimal validation and a brief status.';
            await vscode.env.clipboard.writeText(proceedPrompt);
            vscode.window.showInformationMessage('"Proceed" prompt copied to clipboard. Paste it into the chat input.');
        }
        catch (err) {
            vscode.window.showErrorMessage(`Failed to stage Proceed prompt: ${err}`);
        }
    });
    context.subscriptions.push(insertProceed);
    // If chat participant APIs are available, register a participant with a persistent "Proceed" follow-up
    const anyApi = vscode;
    const chat = (anyApi.chat ?? anyApi['chat']);
    if (chat && typeof chat.createChatParticipant === 'function') {
        const participant = chat.createChatParticipant('assistantProceed.participant', 'Proceed Assistant', async (request, context, stream, token) => {
            // Provide a short status and a clickable Proceed button
            stream.markdown('Proceed Assistant is ready. Click the button or use the follow-up chip to continue.');
            stream.button({
                command: {
                    command: 'assistantProceed.insertProceed',
                    title: 'Proceed',
                    tooltip: 'Insert the Proceed prompt into your clipboard so you can send it in chat.'
                }
            });
            return { metadata: { handled: true, command: 'proceed' } };
        });
        // Provide a "Proceed" follow-up after each response
        participant.followupProvider = {
            provideFollowups: (result, chatContext, token) => {
                const followups = [
                    { prompt: 'Proceed', label: 'Proceed' }
                ];
                return followups;
            }
        };
        context.subscriptions.push({ dispose: () => participant.dispose?.() });
    }
}
function deactivate() { }
//# sourceMappingURL=extension.js.map