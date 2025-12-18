import {EntryReteGraphNode} from "@/editor/node/EntryReteGraphNode.ts";
import {ExitReteGraphNode} from "@/editor/node/ExitReteGraphNode.ts";
import type {ItemDefinition} from "rete-context-menu-plugin/_types/presets/classic/types";
import type {ReteGraphSchemes} from "@/editor/types/editor-scheme.ts";
import {AddReteGraphNode} from "@/editor/node/math/AddReteGraphNode.ts";
import {SubReteGraphNode} from "@/editor/node/math/SubReteGraphNode.ts";
import {MulReteGraphNode} from "@/editor/node/math/MulReteGraphNode.ts";
import {DivReteGraphNode} from "@/editor/node/math/DivReteGraphNode.ts";
import {IfReteGraphNode} from "@/editor/node/condition/IfReteGraphNode.ts";
import {NumberComparatorReteGraphNode} from "@/editor/node/condition/NumberComparatorReteGraphNode.ts";

function randomUUID() {
  return crypto.randomUUID()
}

export const WORKFLOW_GRAPH_EDITOR_CONTEXT_MENUS = [
  ["Entry", () => new EntryReteGraphNode(randomUUID(), "ENTRY", [], true)],
  ["Exit", () => new ExitReteGraphNode(randomUUID(), "EXIT", [], true)],
  ["Math", [
    ["add(x, y)", () => new AddReteGraphNode(randomUUID(), "ADD")],
    ["subtract(x, y)", () => new SubReteGraphNode(randomUUID(), "SUB")],
    ["multiply(x, y)", () => new MulReteGraphNode(randomUUID(), "MUL")],
    ["divide(x, y)", () => new DivReteGraphNode(randomUUID(), "DIV")],
  ]],
  [
    "Condition", [
    ["if (boolean)", () => new IfReteGraphNode(randomUUID(), "IF")],
    ["Comparators", [
      ["Number Comparator", () => new NumberComparatorReteGraphNode(randomUUID(), "NUMBER_COMPARATOR")],
    ]],
  ]
  ]
] as ItemDefinition<ReteGraphSchemes>[];