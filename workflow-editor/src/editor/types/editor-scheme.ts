import {ClassicPreset, type GetSchemes} from "rete";
import type {EntryReteGraphNode} from "../node/EntryReteGraphNode.ts";
import type {ExitReteGraphNode} from "../node/ExitReteGraphNode.ts";
import type {ReteGraphNodeConnection} from "./connections.ts";
import type {BaseReteGraphNode} from "../node/BaseReteGraphNode.ts";

export type ReteGraphNodeTypes = BaseReteGraphNode | EntryReteGraphNode | ExitReteGraphNode
export type ReteGraphNodeConnections = ReteGraphNodeConnection<ReteGraphNodeTypes, ReteGraphNodeTypes>

export type ReteGraphSchemes = GetSchemes<
  ReteGraphNodeTypes,
  ReteGraphNodeConnections
>;

export type Schemes = GetSchemes<
  ClassicPreset.Node,
  ClassicPreset.Connection<ClassicPreset.Node, ClassicPreset.Node>
>;