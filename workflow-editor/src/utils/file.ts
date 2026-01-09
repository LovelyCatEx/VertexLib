export function exportJsonFile(json: string) {
  const blob = new Blob([json], { type: 'application/json' });
  const url = URL.createObjectURL(blob);

  const a = document.createElement('a');
  a.href = url;
  a.download = 'workflow-graph.json';
  a.click();

  URL.revokeObjectURL(url);
}