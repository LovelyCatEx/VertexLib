export const TRIGGER_IO_NAME_PREFIX = "CONN#";

export function isTriggerConnection(groupIdInGraph: string) {
  return groupIdInGraph.startsWith(TRIGGER_IO_NAME_PREFIX);
}

export function getTriggerIONameInGraph(groupId: string) {
  return `${TRIGGER_IO_NAME_PREFIX}${groupId}`
}

export function getOriginalTriggerIONameInGraph(groupIdInGraph: string) {
  return groupIdInGraph.replace(TRIGGER_IO_NAME_PREFIX, "")
}