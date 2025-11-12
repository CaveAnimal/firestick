import * as vscode from 'vscode';

export function activate(context: vscode.ExtensionContext) {
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
    } catch (err) {
      vscode.window.showErrorMessage(`Failed to stage Proceed prompt: ${err}`);
    }
  });

  context.subscriptions.push(insertProceed);

  // If chat participant APIs are available, register a participant with a persistent "Proceed" follow-up
  const anyApi: any = vscode as any;
  const chat = (anyApi.chat ?? anyApi['chat']);
  if (chat && typeof chat.createChatParticipant === 'function') {
    const participant = chat.createChatParticipant('assistantProceed.participant', 'Proceed Assistant', async (request: any, context: any, stream: any, token: vscode.CancellationToken) => {
      // Provide a short status and a clickable Proceed button
      stream.markdown('Proceed Assistant is ready. Click the button or use the follow-up chip to continue.');
      stream.button({
        command: {
          command: 'assistantProceed.insertProceed',
          title: 'Proceed',
          tooltip: 'Insert the Proceed prompt into your clipboard so you can send it in chat.'
        }
      } as any);
      return { metadata: { handled: true, command: 'proceed' } };
    });

    // Provide a "Proceed" follow-up after each response
    participant.followupProvider = {
      provideFollowups: (result: any, chatContext: any, token: vscode.CancellationToken) => {
        const followups: vscode.ChatFollowup[] = [
          { prompt: 'Proceed', label: 'Proceed' }
        ];
        return followups;
      }
    };

    context.subscriptions.push({ dispose: () => participant.dispose?.() });
  }
}

export function deactivate() {}
