import {BaseReteGraphNode} from "./BaseReteGraphNode.ts";

export class ExitReteGraphNode extends BaseReteGraphNode {
  public strict: boolean;

  constructor(
    nodeId: string,
    nodeName: string,
    params: { name: string; type: string }[],
    strict: boolean
  ) {
    super(nodeId, "EXIT", nodeName, params, [], true, []);

    this.strict = strict;
  }
}
