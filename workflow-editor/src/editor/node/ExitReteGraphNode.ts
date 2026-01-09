import {BaseReteGraphNode} from "./BaseReteGraphNode.ts";

export class ExitReteGraphNode extends BaseReteGraphNode {
  public strict: boolean = true;

  constructor(
    nodeId: string,
    nodeName: string,
    params: { name: string; type: string }[],
    strict: boolean
  ) {
    super(nodeId, "EXIT", nodeName, params, [], false, true, true, []);

    this.strict = strict;
  }
}
