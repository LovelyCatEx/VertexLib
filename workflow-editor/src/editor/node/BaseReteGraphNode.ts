import {ClassicPreset} from "rete";
import {parameterSocket, triggerSocket} from "../socket-definitions.ts";
import {DEFAULT_EDGE_GROUP, type GraphNodeType} from "@/types/workflow-graph.ts";
import {getTriggerIONameInGraph} from "@/editor/utils/connection-utils.ts";

export const DEFAULT_INPUT_TRIGGER_NAME = DEFAULT_EDGE_GROUP;
export const DEFAULT_INPUT_TRIGGER_NAME_IN_GRAPH = getTriggerIONameInGraph(DEFAULT_INPUT_TRIGGER_NAME);

export class BaseReteGraphNode extends ClassicPreset.Node<
  Record<string, ClassicPreset.Socket>,
  Record<string, ClassicPreset.Socket>,
  object
> {
  public readonly nodeId: string;
  public readonly nodeType: GraphNodeType;
  public nodeName: string;
  public readonly nodeInputs: { name: string; type: string }[];
  public readonly nodeOutputs: { name: string; type: string }[];
  public readonly nodeOutputTriggers: { groupId: string }[];
  public readonly hasInputTrigger: boolean;

  constructor(
    nodeId: string,
    nodeType: GraphNodeType,
    nodeName: string,
    inputs: { name: string; type: string }[],
    outputs: { name: string; type: string }[],
    hasInputTrigger: boolean = true,
    outputTriggers: { groupId: string }[] = [{ groupId: DEFAULT_EDGE_GROUP }],
  ) {
    super(nodeType);

    this.nodeId = nodeId;
    this.nodeType = nodeType;
    this.nodeName = nodeName;
    this.nodeInputs = inputs;
    this.nodeOutputs = outputs;
    this.nodeOutputTriggers = outputTriggers;
    this.hasInputTrigger = hasInputTrigger;

    if (hasInputTrigger) {
      this.addInput(
        DEFAULT_INPUT_TRIGGER_NAME_IN_GRAPH,
        new ClassicPreset.Input(triggerSocket, undefined, true)
      );
    }

    for (const outputTrigger of outputTriggers) {
      this.addOutput(
        getTriggerIONameInGraph(outputTrigger.groupId),
        new ClassicPreset.Output(
          triggerSocket,
          outputTrigger.groupId == DEFAULT_EDGE_GROUP ? undefined : outputTrigger.groupId
        )
      );
    }

    for (const p of inputs) {
      this.addInput(
        p.name,
        new ClassicPreset.Input(parameterSocket, p.name)
      );
    }

    for (const p of outputs) {
      this.addOutput(
        p.name,
        new ClassicPreset.Output(parameterSocket, p.name)
      );
    }
  }
}