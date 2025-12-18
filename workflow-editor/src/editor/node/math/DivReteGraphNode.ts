import {BaseReteGraphNode} from "../BaseReteGraphNode.ts";
import {JVM_NUMBER} from "../../../types/jvm.ts";

export class DivReteGraphNode extends BaseReteGraphNode {
  constructor(
    nodeId: string,
    nodeName: string
  ) {
    super(
      nodeId,
      "ADD",
      nodeName,
      [
        { type: JVM_NUMBER, name: 'x' },
        { type: JVM_NUMBER, name: 'y' },
      ],
      [
        { type: JVM_NUMBER, name: 'z' },
      ]
    );
  }
}