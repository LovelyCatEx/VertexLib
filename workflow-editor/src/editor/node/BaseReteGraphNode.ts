import {ClassicPreset} from "rete";
import {parameterSocket, triggerSocket} from "../socket-definitions.ts";
import {DEFAULT_EDGE_GROUP, type GraphNodeType} from "@/types/workflow-graph.ts";
import {getTriggerIONameInGraph} from "@/editor/utils/connection-utils.ts";

export const DEFAULT_INPUT_TRIGGER_NAME = DEFAULT_EDGE_GROUP;
export const DEFAULT_INPUT_TRIGGER_NAME_IN_GRAPH = getTriggerIONameInGraph(DEFAULT_INPUT_TRIGGER_NAME);

export type ReteGraphNodeAttributes<T extends BaseReteGraphNode> = Omit<T,
  | keyof BaseReteGraphNode
  | keyof ClassicPreset.Node
>;

const BaseReteGraphNodeAttributeKeys = [
  'nodeType',
  'nodeId',
  'nodeName',
  'nodeInputs',
  'nodeOutputs',
  'readonlyInputs',
  'readonlyOutputs',
  'nodeOutputTriggers',
  'hasInputTrigger',
  'id',
  'inputs',
  'outputs',
  'selected',
  'label',
  'controls'
] as const

type BaseReteGraphNodeAttributeKey = typeof BaseReteGraphNodeAttributeKeys[number]


export class BaseReteGraphNode extends ClassicPreset.Node<
  Record<string, ClassicPreset.Socket>,
  Record<string, ClassicPreset.Socket>,
  object
> {
  public readonly nodeId: string;
  public readonly nodeType: GraphNodeType;
  public nodeName: string;
  public nodeInputs: { name: string; type: string }[];
  public nodeOutputs: { name: string; type: string }[];
  public readonlyInputs: boolean = false;
  public readonlyOutputs: boolean = false;
  public readonly nodeOutputTriggers: { groupId: string }[];
  public readonly hasInputTrigger: boolean;

  constructor(
    nodeId: string,
    nodeType: GraphNodeType,
    nodeName: string,
    inputs: { name: string; type: string }[],
    outputs: { name: string; type: string }[],
    readonlyInputs: boolean = true,
    readonlyOutputs: boolean = true,
    hasInputTrigger: boolean = true,
    outputTriggers: { groupId: string }[] = [{ groupId: DEFAULT_EDGE_GROUP }],
  ) {
    super(nodeType);

    this.nodeId = nodeId;
    this.nodeType = nodeType;
    this.nodeName = nodeName;
    this.nodeInputs = inputs;
    this.nodeOutputs = outputs;
    this.readonlyInputs = readonlyInputs;
    this.readonlyOutputs = readonlyOutputs;
    this.nodeOutputTriggers = outputTriggers;
    this.hasInputTrigger = hasInputTrigger;

    this.rerenderSockets();
  }

  public rerenderSockets() {
    for (let input in this.inputs) {
      this.removeInput(input);
    }

    for (let output in this.outputs) {
      this.removeOutput(output);
    }

    if (this.hasInputTrigger) {
      this.addInput(
        DEFAULT_INPUT_TRIGGER_NAME_IN_GRAPH,
        new ClassicPreset.Input(triggerSocket, undefined, true)
      );
    }

    for (const outputTrigger of this.nodeOutputTriggers) {
      this.addOutput(
        getTriggerIONameInGraph(outputTrigger.groupId),
        new ClassicPreset.Output(
          triggerSocket,
          outputTrigger.groupId == DEFAULT_EDGE_GROUP ? undefined : outputTrigger.groupId
        )
      );
    }

    for (const p of this.nodeInputs) {
      this.addInput(
        p.name,
        new ClassicPreset.Input(parameterSocket, p.name)
      );
    }

    for (const p of this.nodeOutputs) {
      this.addOutput(
        p.name,
        new ClassicPreset.Output(parameterSocket, p.name)
      );
    }
  }

  public getAttributes(): { type: string, name: string, value: any }[] {
    return Object.keys(this)
      .filter((key) => {
        return !BaseReteGraphNodeAttributeKeys.includes(key as BaseReteGraphNodeAttributeKey)
      })
      .map((key) => {
        const [k, value] = Object.entries(this).find(([k]) => k == key)!
        return {
          type: typeof value,
          name: k,
          value: value
        }
      })
  }

  public addNodeInput(parameterType: string, parameterName: string) {
    this.nodeInputs.push({ type: parameterType, name: parameterName });
    this.rerenderSockets();
  }

  public modifyInputParameterType(parameterName: string, newType: string) {
    const target = this.nodeInputs.find((input) => input.name == parameterName);
    if (target) {
      target.type = newType;
    }
    this.rerenderSockets();
  }

  public addNodeOutput(parameterType: string, parameterName: string) {
    this.nodeOutputs.push({ type: parameterType, name: parameterName });
    this.rerenderSockets();
  }

  public modifyOutputParameterType(parameterName: string, newType: string) {
    const target = this.nodeOutputs.find((output) => output.name == parameterName);
    if (target) {
      target.type = newType;
    }
    this.rerenderSockets();
  }

  public isIOParameterExists(parameterName: string) {
    return !this.nodeInputs.find((input) => input.name == parameterName) &&
      !this.nodeOutputs.find((output) => output.name == parameterName);
  }

  public isIOParameterNameValid(parameterName: string) {
    const name = parameterName.trim();
    return name.length > 0 && isNaN(Number(name[0]));
  }
}