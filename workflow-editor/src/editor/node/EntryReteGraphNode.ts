import {BaseReteGraphNode} from "./BaseReteGraphNode.ts";

export class EntryReteGraphNode extends BaseReteGraphNode {
  constructor(
    nodeId: string,
    nodeName: string,
    params: { name: string; type: string }[]
  ) {
    super(nodeId, "ENTRY", nodeName, [], params, false);
  }
}