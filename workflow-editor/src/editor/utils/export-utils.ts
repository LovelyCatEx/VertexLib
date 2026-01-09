import {type BaseReteGraphNode} from "../node/BaseReteGraphNode.ts";
import type {
  GraphNode,
  GraphNodeEntry,
  GraphNodeExit,
  GraphNodeIf,
  WorkFlowGraphSerialization
} from "@/types/workflow-graph.ts";
import type {WorkFlowGraphEditorContext} from "../index.tsx";
import type {ReteGraphNodeConnections} from "../types/editor-scheme.ts";
import {EntryReteGraphNode} from "../node/EntryReteGraphNode.ts";
import {ExitReteGraphNode} from "../node/ExitReteGraphNode.ts";
import {getOriginalTriggerIONameInGraph, isTriggerConnection} from "@/editor/utils/connection-utils.ts";
import {toast} from "sonner";

export function transformReteToGraphNodeSerialization(node: BaseReteGraphNode): GraphNode {
  return {
    nodeId: node.nodeId,
    nodeName: node.nodeName,
    nodeType: node.nodeType,
    inputs: node.nodeInputs,
    outputs: node.nodeOutputs
  }
}

export async function exportWorkFlowGraph(
  ctx: WorkFlowGraphEditorContext
): Promise<WorkFlowGraphSerialization> {
  const nodes = ctx.editor.getNodes() as BaseReteGraphNode[]
  const connections = ctx.editor.getConnections() as ReteGraphNodeConnections[]

  return {
    graphNodeMap: Object.fromEntries(
      nodes.map((node) => {
        let builtNode: GraphNode = {} as GraphNodeEntry

        switch (node.nodeType) {
          case "ENTRY": {
            const actualNode = node as EntryReteGraphNode
            builtNode = {
              ...transformReteToGraphNodeSerialization(node),
              strict: actualNode.strict
            } as GraphNodeEntry
            break;
          }
          case "EXIT": {
            const actualNode = node as ExitReteGraphNode
            builtNode = {
              ...transformReteToGraphNodeSerialization(node),
              strict: actualNode.strict
            } as GraphNodeExit
            break;
          }
          case "IF": {
            builtNode = {
              ...transformReteToGraphNodeSerialization(node),
            } as GraphNodeIf
            break;
          }
          case "ADD": {
            builtNode = {
              ...transformReteToGraphNodeSerialization(node),
            } as GraphNode
            break;
          }
          case "SUB": {
            builtNode = {
              ...transformReteToGraphNodeSerialization(node),
            } as GraphNode
            break;
          }
          case "MUL": {
            builtNode = {
              ...transformReteToGraphNodeSerialization(node),
            } as GraphNode
            break;
          }
          case "DIV": {
            builtNode = {
              ...transformReteToGraphNodeSerialization(node),
            } as GraphNode
            break;
          }
          case "NUMBER_COMPARATOR": {
            builtNode = {
              ...transformReteToGraphNodeSerialization(node),
            } as GraphNode
            break;
          }
          case "STRING_CONTAINS": {
            break;
          }
        }

        return [
          node.nodeId,
          builtNode
        ]
      })
    ),
    graphNodeTriggerEdges: connections
      .filter((conn) => isTriggerConnection(conn.targetInput))
      .map((conn) => {
        const fromNode = ctx.getReteGraphNodeById(conn.source)
        const toNode = ctx.getReteGraphNodeById(conn.target)

        if (!fromNode || !toNode) {
          toast.error("Could not find node metadata of source or target")
          throw new Error(`Could not find node metadata of source or target, ${!!fromNode} & ${!!toNode}`)
        }

        return {
          from: fromNode.nodeId,
          to: toNode.nodeId,
          groupId: getOriginalTriggerIONameInGraph(conn.sourceOutput)
        }
      }),
    graphNodeParameterTransmissionEdges: connections
      .filter((conn) => !isTriggerConnection(conn.targetInput))
      .map((conn) => {
        const fromNode = ctx.getReteGraphNodeById(conn.source)
        const toNode = ctx.getReteGraphNodeById(conn.target)

        if (!fromNode || !toNode) {
          toast.error("Could not find node metadata of source or target")
          throw new Error(`Could not find node metadata of source or target, ${!!fromNode} & ${!!toNode}`)
        }


        return {
          from: fromNode.nodeId,
          to: toNode.nodeId,
          fromParameterName: conn.sourceOutput,
          toParameterName: conn.targetInput
        }
      }),
    graphNodePositions: Object.fromEntries(
      nodes.map((node) => {
        return [node.nodeId, ctx.getNodePosition(node.id)]
      })
    )
  }
}