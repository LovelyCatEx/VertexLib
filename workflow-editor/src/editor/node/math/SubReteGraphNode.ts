import {BaseReteGraphNode} from "../BaseReteGraphNode.ts";
import {JVM_NUMBER} from "../../../types/jvm.ts";

export class SubReteGraphNode extends BaseReteGraphNode {
  constructor(
    nodeId: string,
    nodeName: string
  ) {
    super(
      nodeId,
      "SUB",
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