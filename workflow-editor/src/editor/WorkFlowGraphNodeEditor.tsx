import type {BaseReteGraphNode} from "@/editor/node/BaseReteGraphNode.ts";
import * as React from "react";
import type {WorkFlowGraphEditorContext} from "@/editor/index.tsx";
import {Input} from "@/components/ui/input.tsx";
import {useEffect, useState} from "react";
import {Plus, SquareFunction, Variable} from "lucide-react";
import {Button} from "@/components/ui/button.tsx";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";
import {WORKFLOW_GRAPH_SUPPORTS_DATA_TYPES} from "@/types/workflow-graph.ts";

export interface WorkFlowGraphNodeEditorProps extends React.HTMLAttributes<HTMLDivElement> {
  ctx: WorkFlowGraphEditorContext,
  selectedNodes: BaseReteGraphNode[]
}

export interface WorkFlowGraphSingleNodeEditorProps extends React.HTMLAttributes<HTMLDivElement> {
  ctx: WorkFlowGraphEditorContext,
  selectedNode: BaseReteGraphNode
}

export interface WorkFlowGraphMultiNodeEditorProps extends React.HTMLAttributes<HTMLDivElement> {
  ctx: WorkFlowGraphEditorContext,
  selectedNodes: BaseReteGraphNode[]
}

export function WorkFlowGraphParameterDataTypeSelector(
  { initial, onSelected }: { initial: string, onSelected?: (newType: string) => void }
) {
  const [current, setCurrent] = useState(initial);

  return <Select
    value={current}
    onValueChange={(n) => {
      setCurrent(n);
      onSelected?.(n);
    }}
  >
    <SelectTrigger className="w-full">
      <SelectValue placeholder="Theme" />
    </SelectTrigger>
    <SelectContent>
      {WORKFLOW_GRAPH_SUPPORTS_DATA_TYPES.map((type) => (
        <SelectItem value={type}>{type}</SelectItem>
      ))}
    </SelectContent>
  </Select>
}

export function WorkFlowGraphNodeEditor(props: WorkFlowGraphNodeEditorProps) {
  return props.selectedNodes.length > 0 ? (
    props.selectedNodes.length > 1 ? (
      <WorkFlowGraphMultiNodeEditor {...props} />
    ) : (
      <WorkFlowGraphSingleNodeEditor selectedNode={props.selectedNodes[0]} {...props} />
    )
  ) : <div className={props.className + " p-4 bg-[#262626] text-white"}>
    <p>No Selection</p>
  </div>
}

function WorkFlowGraphSingleNodeEditor({
  selectedNode, className
}: WorkFlowGraphSingleNodeEditorProps) {
  const [nodeName, setNodeName] = useState<string>(selectedNode.nodeName)

  useEffect(() => {
    setNodeName(selectedNode.nodeName)
  }, [selectedNode]);

  useEffect(() => {
    if (nodeName.length > 0) {
      selectedNode.nodeName = nodeName;
    }
  }, [nodeName]);

  return <div className={className + " p-4 bg-[#262626] text-white"}>
    <div className="flex flex-row items-center space-x-2">
      <SquareFunction size="20" />
      <p>{selectedNode.nodeName} ({selectedNode.nodeType})</p>
    </div>

    <div className="flex flex-row items-center space-x-2 mt-4">
      <span>Name</span>
      <Input
        value={nodeName}
        onChange={(e) => setNodeName(e.currentTarget.value)}
      />
    </div>

    <div className="flex flex-col mt-4">
      <div className="w-full flex flex-row items-center space-x-2">
        <Variable size="20" />
        <span>Inputs</span>
      </div>

      <div className="grid grid-cols-[1fr_2fr] gap-y-2 items-center">
        {selectedNode.nodeInputs.map((input) => (
          <React.Fragment key={input.name}>
            <span>{input.name}</span>

            <WorkFlowGraphParameterDataTypeSelector
              initial={input.type}
              onSelected={(t) => {
                selectedNode.modifyInputParameterType(input.name, t)
              }}
            />
          </React.Fragment>
        ))}
      </div>

      <Button className="mt-4" variant="secondary"><Plus size="20" /> Add Input</Button>
    </div>

    <div className="flex flex-col mt-4">
      <div className="w-full flex flex-row items-center space-x-2">
        <Variable size="20" />
        <span>Outputs</span>
      </div>

      <div className="grid grid-cols-[1fr_2fr] gap-y-2 items-center">
        {selectedNode.nodeOutputs.map((output) => (
          <React.Fragment key={output.name}>
            <span>{output.name}</span>

            <WorkFlowGraphParameterDataTypeSelector
              initial={output.type}
              onSelected={(t) => {
                selectedNode.modifyOutputParameterType(output.name, t)}
              }
            />
          </React.Fragment>
        ))}
      </div>
      <Button className="mt-4" variant="secondary"><Plus size="20" /> Add Output</Button>
    </div>

  </div>
}

function WorkFlowGraphMultiNodeEditor({
  selectedNodes, className
}: WorkFlowGraphMultiNodeEditorProps) {
  return <div className={className + " p-4 bg-[#262626] text-white"}>
    <p>Selected {selectedNodes.length} Nodes</p>
  </div>
}

