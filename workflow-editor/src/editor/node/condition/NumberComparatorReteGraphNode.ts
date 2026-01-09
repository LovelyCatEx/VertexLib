import {BaseReteGraphNode} from "../BaseReteGraphNode.ts";
import {JVM_BOOLEAN_PRIMITIVE, JVM_NUMBER} from "../../../types/jvm.ts";

export class NumberComparatorReteGraphNode extends BaseReteGraphNode {
  constructor(
    nodeId: string,
    nodeName: string
  ) {
    super(
      nodeId,
      "NUMBER_COMPARATOR",
      nodeName,
      [
        { type: JVM_NUMBER, name: 'x' },
        { type: JVM_NUMBER, name: 'y' },
      ],
      [
        { type: JVM_BOOLEAN_PRIMITIVE, name: 'z' },
      ]
    );
  }
}