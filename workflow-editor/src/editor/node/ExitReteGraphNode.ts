import {BaseReteGraphNode} from "./BaseReteGraphNode.ts";

export class ExitReteGraphNode extends BaseReteGraphNode {
  constructor(
    nodeId: string,
    nodeName: string,
    params: { name: string; type: string }[]
  ) {
    super(nodeId, "EXIT", nodeName, params, [], true, []);
  }
}
