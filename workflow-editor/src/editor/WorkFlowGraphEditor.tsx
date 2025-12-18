import {useRete} from "rete-react-plugin";
import {createWorkFlowGraphEditor, type WorkFlowGraphEditorContext} from "./index.tsx";
import * as React from "react";
import {useCallback, useEffect, useState} from "react";
import {type BaseReteGraphNode, DEFAULT_INPUT_TRIGGER_NAME_IN_GRAPH} from "./node/BaseReteGraphNode.ts";
import {
  type GraphNode,
  type GraphNodeEntry,
  type GraphNodeExit,
  type GraphNodeIf,
  type WorkFlowGraphSerialization
} from "../types/workflow-graph.ts";
import {EntryReteGraphNode} from "./node/EntryReteGraphNode.ts";
import {ExitReteGraphNode} from "./node/ExitReteGraphNode.ts";
import {IfReteGraphNode} from "./node/condition/IfReteGraphNode.ts";
import {AddReteGraphNode} from "./node/math/AddReteGraphNode.ts";
import {SubReteGraphNode} from "./node/math/SubReteGraphNode.ts";
import {MulReteGraphNode} from "./node/math/MulReteGraphNode.ts";
import {DivReteGraphNode} from "./node/math/DivReteGraphNode.ts";
import {NumberComparatorReteGraphNode} from "./node/condition/NumberComparatorReteGraphNode.ts";
import {ReteGraphNodeConnection} from "./types/connections.ts";
import {toast} from "sonner";
import {Button} from "../components/ui/button.tsx";
import {exportWorkFlowGraph} from "./utils/export-utils.ts";
import {getTriggerIONameInGraph} from "@/editor/utils/connection-utils.ts";

export interface WorkFlowGraphEditorProps extends React.HTMLAttributes<HTMLDivElement> {
  graphData: WorkFlowGraphSerialization
}

export interface WorkFlowGraphRenderResult {
  reteGraphNodes: Map<string, BaseReteGraphNode>,
  findReteGraphNodeById: (reteGraphNodeId: string) => BaseReteGraphNode | undefined
}

async function renderWorkFlowGraph(
  ctx: WorkFlowGraphEditorContext,
  graphData: WorkFlowGraphSerialization
): Promise<WorkFlowGraphRenderResult> {
  await ctx.editor.clear();

  const reteNodeMap = new Map<string, BaseReteGraphNode>();

  for (const [id, node] of Object.entries(graphData.graphNodeMap)) {
    async function addGraphNode<T extends BaseReteGraphNode>(createdNode: T) {
      reteNodeMap.set(id, createdNode);
      await ctx!.editor.addNode(createdNode);
    }

    switch (node.nodeType) {
      case "ENTRY": {
        const entryNode = node as GraphNodeEntry;
        await addGraphNode(new EntryReteGraphNode(entryNode.nodeId, entryNode.nodeName, entryNode.inputs, entryNode.strict));
        break;
      }

      case "EXIT": {
        const exitNode = node as GraphNodeExit;
        await addGraphNode(new ExitReteGraphNode(exitNode.nodeId, exitNode.nodeName, exitNode.inputs, exitNode.strict));
        break;
      }

      case "IF": {
        const ifNode = node as GraphNodeIf
        await addGraphNode(new IfReteGraphNode(ifNode.nodeId, ifNode.nodeName));
        break;
      }

      case "ADD": {
        const addNode = node as GraphNode;
        await addGraphNode(new AddReteGraphNode(addNode.nodeId, addNode.nodeName));
        break;
      }

      case "SUB": {
        const subNode = node as GraphNode;
        await addGraphNode(new SubReteGraphNode(subNode.nodeId, subNode.nodeName));
        break;
      }

      case "MUL": {
        const mulNode = node as GraphNode;
        await addGraphNode(new MulReteGraphNode(mulNode.nodeId, mulNode.nodeName));
        break;
      }

      case "DIV": {
        const divNode = node as GraphNode;
        await addGraphNode(new DivReteGraphNode(divNode.nodeId, divNode.nodeName));
        break;
      }

      case "NUMBER_COMPARATOR": {
        const numberComparatorNode = node as GraphNode;
        await addGraphNode(new NumberComparatorReteGraphNode(numberComparatorNode.nodeId, numberComparatorNode.nodeName));
        break;
      }

      case "STRING_CONTAINS": {
        break;
      }
    }
  }

  for (const {from, to, groupId} of graphData.graphNodeTriggerEdges) {
    await ctx.editor.addConnection(
      new ReteGraphNodeConnection(
        reteNodeMap.get(from)!,
        getTriggerIONameInGraph(groupId),
        reteNodeMap.get(to)!,
        DEFAULT_INPUT_TRIGGER_NAME_IN_GRAPH
      )
    )
  }

  for (const {from, to, fromParameterName, toParameterName} of graphData.graphNodeParameterTransmissionEdges) {
    await ctx.editor.addConnection(
      new ReteGraphNodeConnection(
        reteNodeMap.get(from)!,
        fromParameterName,
        reteNodeMap.get(to)!,
        toParameterName
      )
    )
  }

  return {
    reteGraphNodes: reteNodeMap,
    findReteGraphNodeById: (id) => [...reteNodeMap.values()].find((it) => it.id == id)
  }
}

export function WorkFlowGraphEditor({
   graphData, className
}: WorkFlowGraphEditorProps) {
  const createEditor = useCallback(
    (container: HTMLElement) =>
      createWorkFlowGraphEditor(container, {
        onInvalidConnection: () => {
          toast.warning("Incapable socket type");
        }
      }),
    []
  );

  const [ref, ctx] = useRete(createEditor);
  const [renderResult, setRenderResult] = useState<WorkFlowGraphRenderResult | null>(null);

  useEffect(() => {
    if (ctx == null) return;

    renderWorkFlowGraph(ctx, graphData)
      .then((res) => {
        setRenderResult(res);
      })
      .catch((err) => console.error("graph render failed", err))

    return () => {
      ctx.destroy();
    };
  }, [ctx, graphData])

  const onExportGraphClick = async () => {
    if (!ctx) {
      toast.error("WorkFlow Graph Editor is not ready yet")
      return
    }

    if (!renderResult) {
      toast.warning("Please wait until the graph rendered completely")
      return
    }

    const a = await exportWorkFlowGraph(ctx, renderResult)
    console.log(a)
  }

  return (
    <div className={className + " flex flex-col"}>
      <div className="w-full p-4">
        <Button onClick={onExportGraphClick}>Export</Button>
      </div>
      <div ref={ref} className="flex-1" />
    </div>
  )
}