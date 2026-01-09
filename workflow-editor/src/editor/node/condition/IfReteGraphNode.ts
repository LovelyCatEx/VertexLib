import {BaseReteGraphNode} from "../BaseReteGraphNode.ts";
import {JVM_BOOLEAN_PRIMITIVE} from "../../../types/jvm.ts";

export class IfReteGraphNode extends BaseReteGraphNode {
  constructor(
    nodeId: string,
    nodeName: string,
  ) {
    super(
      nodeId,
      "IF",
      nodeName,
      [{ name: 'condition', type: JVM_BOOLEAN_PRIMITIVE }],
      [],
      true,
      true,
      true,
      [
        {
          groupId: "True",
        },
        {
          groupId: "False",
        }
      ]
    );
  }
}
