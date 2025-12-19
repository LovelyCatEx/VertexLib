import {BaseReteGraphNode} from "./BaseReteGraphNode.ts";

export class EntryReteGraphNode extends BaseReteGraphNode {
  public strict: boolean = true;

  constructor(
    nodeId: string,
    nodeName: string,
    params: { name: string; type: string }[],
    strict: boolean
  ) {
    super(nodeId, "ENTRY", nodeName, [], params, true, false, false);

    console.log(nodeId, nodeName, params, strict)
    this.strict = strict;
  }
}