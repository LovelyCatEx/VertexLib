import {ClassicPreset} from "rete";
import type {ReteGraphNodeTypes} from "./editor-scheme.ts";

export class ReteGraphNodeConnection<
  A extends ReteGraphNodeTypes,
  B extends ReteGraphNodeTypes
> extends ClassicPreset.Connection<A, B> {
  isLoop?: boolean;
}