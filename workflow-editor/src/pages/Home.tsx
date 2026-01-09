import '../App.css'
import {WorkFlowGraphEditor} from "../editor/WorkFlowGraphEditor.tsx";
import {emptyWorkFlowGraphSerialization} from "../types/workflow-graph.ts";

export default function Home() {
  return (
    <WorkFlowGraphEditor
      className="w-full h-[100vh]"
      graphData={emptyWorkFlowGraphSerialization()}
    />
  );
}