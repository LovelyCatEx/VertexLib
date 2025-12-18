import {BaseReteGraphNode} from "./BaseReteGraphNode.ts";

export class EntryReteGraphNode extends BaseReteGraphNode {
  public strict: boolean;

  constructor(
    nodeId: string,
    nodeName: string,
    params: { name: string; type: string }[],
    strict: boolean
  ) {
    super(nodeId, "ENTRY", nodeName, [], params, false);

    this.strict = strict;
  }
}